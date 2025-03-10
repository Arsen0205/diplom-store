package com.example.diplom.service;

import com.example.diplom.dto.request.CreateOrderDtoRequest;
import com.example.diplom.models.*;
import com.example.diplom.models.enums.OrderStatus;
import com.example.diplom.repository.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class OrderService {
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;
    private final SupplierRepository supplierRepository;

    @Transactional
    public void createdOrder(Principal principal, CreateOrderDtoRequest request){
        Cart cart = cartRepository.findByClientId(getUserByPrincipal(principal).getId()).orElseThrow(()-> new IllegalArgumentException("Корзина пуста"));

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
                        .quantity(cartItem.getQuantity())
                        .costPrice(product.getPrice())
                        .sellingPrice(product.getSellingPrice())
                        .totalCost(totalCost)
                        .totalPrice(totalPrice)
                        .build();

                orderItems.add(orderItem);

                // Уменьшаем количество товара
                product.setQuantity(product.getQuantity() - cartItem.getQuantity());
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


            createdOrders.add(orderRepository.save(savedOrder));
        }

        // Очищаем корзину после оформления заказа
        cartRepository.delete(cart);

    }


    public Client getUserByPrincipal(Principal principal) {
        if (principal == null) {
            return null;
        }
        return clientRepository.findByLogin(principal.getName()).orElse(new Client());
    }
}
