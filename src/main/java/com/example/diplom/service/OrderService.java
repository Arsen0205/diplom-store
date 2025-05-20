package com.example.diplom.service;

import com.example.diplom.dto.request.CreateOrderDtoRequest;
import com.example.diplom.dto.response.OrderClientDtoResponse;
import com.example.diplom.dto.response.OrderItemClientDtoResponse;
import com.example.diplom.models.*;
import com.example.diplom.models.enums.OrderStatus;
import com.example.diplom.repository.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class OrderService {
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;
    private final SupplierRepository supplierRepository;
    private final ProductService productService;
    private final TelegramNotificationService telegramNotificationService;

    @Transactional
    public void createdOrder(Principal principal, CreateOrderDtoRequest request){
        Cart cart = cartRepository.findByClientId(getUserByPrincipal(principal).getId())
                .orElseThrow(()-> new IllegalArgumentException("Корзина пуста"));
        Client client = clientRepository.findById(getUserByPrincipal(principal).getId())
                .orElseThrow(()->new RuntimeException("Пользователя с таким айди не существует"));

        if (cart.getItems().isEmpty()){
            throw new IllegalArgumentException("Корзина пустая");
        }

        Map<Supplier, List<CartItem>> groupedItems = cart.getItems().stream()
                .collect(Collectors.groupingBy(item->item.getProduct().getSupplier()));

        List<Order> createdOrders = new ArrayList<>();

        for (Map.Entry<Supplier, List<CartItem>> entry: groupedItems.entrySet()){
            Supplier supplier = entry.getKey();
            List<CartItem> cartItems = entry.getValue();

            Order newOrder = Order.builder()
                    .supplier(supplier)
                    .totalCost(BigDecimal.ZERO)
                    .totalPrice(BigDecimal.ZERO)
                    .profit(BigDecimal.ZERO)
                    .status(OrderStatus.PENDING)
                    .address(request.getAddress())
                    .city(request.getCity())
                    .orderItems(new ArrayList<>())
                    .client(client)
                    .build();

            Order savedOrder = orderRepository.save(newOrder);

            List<OrderItem> orderItems = new ArrayList<>();

            for (CartItem cartItem : cartItems) {
                Product product = cartItem.getProduct();

                if (product.getQuantity() < cartItem.getQuantity()) {
                    throw new IllegalArgumentException("Недостаточно товара: " + product.getTitle());
                }

                BigDecimal totalCost = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
                BigDecimal totalPrice = product.getSellingPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));

                OrderItem orderItem = OrderItem.builder()
                        .order(savedOrder)
                        .product(product)
                        .productSku(product.getId())
                        .title(product.getTitle())
                        .quantity(cartItem.getQuantity())
                        .costPrice(product.getPrice())
                        .sellingPrice(product.getSellingPrice())
                        .totalCost(totalCost)
                        .totalPrice(totalPrice)
                        .build();

                orderItems.add(orderItem);

                // Уменьшаем количество товара
                //product.setQuantity(product.getQuantity() - cartItem.getQuantity());
                productRepository.save(product);
            }

            orderItemRepository.saveAll(orderItems);
            savedOrder.getOrderItems().addAll(orderItems);

            BigDecimal totalCost = orderItems.stream().map(OrderItem::getTotalCost).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalPrice = orderItems.stream().map(OrderItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal profit = totalCost.subtract(totalPrice);

            savedOrder.setTotalCost(totalCost);
            savedOrder.setTotalPrice(totalPrice);
            savedOrder.setProfit(profit);

            telegramNotificationService.sendOrderNotification(supplier.getChatId(), client.getChatId(), savedOrder);

            createdOrders.add(orderRepository.save(savedOrder));
        }

        // Очищаем корзину после оформления заказа
        cartRepository.delete(cart);

    }

    @Transactional
    public List<OrderClientDtoResponse> getOrdersByClient(Principal principal){
        Client client = clientRepository.findById(getUserByPrincipal(principal).getId())
                .orElseThrow(()-> new RuntimeException("Пользователя с таким id не существует"));

        List<Order> orders = orderRepository.findByClient(client);

        return orders.stream()
                .map(order -> new OrderClientDtoResponse(
                        order.getId(),
                        order.getStatus(),
                        order.getTotalCost(),
                        order.getDateTime()
                ))
                .toList();
    }

    @Transactional
    public List<OrderItemClientDtoResponse> ordersInfo(Long id, Principal principal) {
        Client client = clientRepository.findById(getUserByPrincipal(principal).getId())
                .orElseThrow(() -> new RuntimeException("Пользователь с таким id не существует"));

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Такого заказа не существует"));

        if (!client.getId().equals(order.getClient().getId())) {
            throw new RuntimeException("Доступ запрещен: заказ не принадлежит данному пользователю");
        }

        List<OrderItem> items = orderItemRepository.findByOrder(order);

        return items.stream()
                .map(orderItem -> {
                    // Извлечь URL картинки точно так же, как в getAllProduct()
                    String url = orderItem.getProduct().getImages().stream()
                            .filter(Image::isPreviewImage)
                            .map(Image::getUrl)
                            .findFirst()
                            .orElseGet(() -> orderItem.getProduct().getImages().stream()
                                    .map(Image::getUrl)
                                    .findFirst()
                                    .orElse("/images/placeholder.png")
                            );

                    return new OrderItemClientDtoResponse(
                            orderItem.getTitle(),
                            orderItem.getQuantity(),
                            orderItem.getCostPrice(),
                            orderItem.getProductSku(),
                            orderItem.getCostPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())),
                            url
                    );
                })
                .toList();
    }


    @Transactional
    public ResponseEntity<String> confirmedOrder(Long id, Principal principal){
        Order order = orderRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Такого заказа не существует"));

        Object currentUser = productService.getUserByPrincipal(principal);

        if(currentUser instanceof Supplier){
            Supplier supplier = (Supplier)productService.getUserByPrincipal(principal);
            if (supplier.getId().equals(order.getSupplier().getId())){
                for (OrderItem item: order.getOrderItems()){
                    Product product = item.getProduct();
                    product.setQuantity(product.getQuantity() - item.getQuantity());
                    productRepository.save(product);
                }
                order.setStatus(OrderStatus.CONFIRMED);
                orderRepository.save(order);
                telegramNotificationService.acceptOrder(id);
                return ResponseEntity.status(HttpStatus.OK)
                        .body("Вы приняли заказ");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("У вас нет прав, чтобы принять заказ");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Ошибка при принятии заказа");
    }

    @Transactional
    public ResponseEntity<String> cancelledOrder(Long id, Principal principal){
        Object currentUser = productService.getUserByPrincipal(principal);
        Order order = orderRepository.findById(id).
                orElseThrow(()-> new RuntimeException("Такого заказа не существует"));

        if (currentUser instanceof Client){
            Client client = (Client) productService.getUserByPrincipal(principal);
            if (order.getClient().getId().equals(client.getId())){
                order.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
                telegramNotificationService.rejectClient(id);
                return ResponseEntity.ok("Заказ отменен клиентом");
            }

        }else if(currentUser instanceof Supplier) {
            Supplier supplier = (Supplier) productService.getUserByPrincipal(principal);
            if (order.getSupplier().getId().equals(supplier.getId())) {
                order.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
                telegramNotificationService.rejectOrder(id);
                return ResponseEntity.ok("Заказ отменен поставщиком");
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Ошибка: неизвестный пользователь");
    }

    @Transactional
    public ResponseEntity<String> shippedOrder(Long id, Principal principal){
        Object currentUser = productService.getUserByPrincipal(principal);
        Order order = orderRepository.findById(id).
                orElseThrow(()-> new RuntimeException("Такого заказа не существует"));

        if (currentUser instanceof Supplier){
            Supplier supplier = (Supplier) productService.getUserByPrincipal(principal);
            if (order.getSupplier().getId().equals(supplier.getId())){
                order.setStatus(OrderStatus.SHIPPED);
                orderRepository.save(order);
                telegramNotificationService.shippedOrder(id);
                return ResponseEntity.ok("Статус заказа изменен");
            }
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("У вас нет прав на данную операцию");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Ошибка: неизвестный пользователь");
    }

    @Transactional
    public ResponseEntity<String> deliveredOrder(Long id, Principal principal){
        Object currentUser = productService.getUserByPrincipal(principal);
        Order order = orderRepository.findById(id).
                orElseThrow(()-> new RuntimeException("Такого заказа не существует"));

        if (currentUser instanceof Supplier){
            Supplier supplier = (Supplier) productService.getUserByPrincipal(principal);
            if (order.getSupplier().getId().equals(supplier.getId())){
                order.setStatus(OrderStatus.DELIVERED);
                orderRepository.save(order);
                telegramNotificationService.deliveredOrder(id);
                return ResponseEntity.ok("Статус заказа изменен");
            }
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("У вас нет прав на данную операцию");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Ошибка: неизвестный пользователь");
    }

    public Client getUserByPrincipal(Principal principal) {
        if (principal == null) {
            return null;
        }
        return clientRepository.findByLogin(principal.getName()).orElse(new Client());
    }
}
