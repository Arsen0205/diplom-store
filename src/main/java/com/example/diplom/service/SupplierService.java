package com.example.diplom.service;

import com.example.diplom.dto.response.OrderItemSupplierDtoResponse;
import com.example.diplom.dto.response.OrderSupplierDtoResponse;
import com.example.diplom.dto.response.ProductDtoResponse;
import com.example.diplom.dto.response.SuppliersDtoResponse;
import com.example.diplom.models.Order;
import com.example.diplom.models.OrderItem;
import com.example.diplom.models.Product;
import com.example.diplom.models.Supplier;
import com.example.diplom.repository.OrderItemRepository;
import com.example.diplom.repository.OrderRepository;
import com.example.diplom.repository.ProductRepository;
import com.example.diplom.repository.SupplierRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;


@Service
@AllArgsConstructor
public class SupplierService {
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;


    public ResponseEntity<SuppliersDtoResponse> supplierInfo(Principal principal){
        Supplier currentUser = getUserByPrincipal(principal);

        SuppliersDtoResponse suppliersDtoResponse = new SuppliersDtoResponse(
                currentUser.getId(),
                currentUser.getLogin(),
                currentUser.getFio(),
                currentUser.getEmail(),
                currentUser.getPhoneNumber(),
                currentUser.getLoginTelegram(),
                currentUser.getChatId(),
                currentUser.isActive(),
                currentUser.getRole()
        );
        return ResponseEntity.ok(suppliersDtoResponse);
    }

    @Transactional
    public List<ProductDtoResponse> getProductSupplier(Principal principal){
        Supplier supplier = getUserByPrincipal(principal);

        List<Product> products = productRepository.findBySupplier(supplier);

        return products.stream()
                .map(product -> new ProductDtoResponse(
                        product.getId(),
                        product.getTitle(),
                        product.getQuantity(),
                        product.getPrice(),
                        product.getSellingPrice()
                ))
                .toList();
    }

    @Transactional
    public List<OrderSupplierDtoResponse> getSupplierOrders(Principal principal) {
        Supplier supplier = getUserByPrincipal(principal);

        List<Order> orders = orderRepository.findBySupplier(supplier);

        return orders.stream()
                .map(order -> new OrderSupplierDtoResponse(
                        order.getId(),
                        order.getAddress(),
                        order.getCity(),
                        order.getProfit(),
                        order.getStatus(),
                        order.getTotalCost(),
                        order.getTotalPrice(),
                        order.getClient().getLogin()
                ))
                .toList();
    }

    @Transactional
    public List<OrderItemSupplierDtoResponse> orderInfo(Long id, Principal principal) {
        Supplier supplier = getUserByPrincipal(principal);

        Order order = orderRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Такого заказа не существует"));

        if(!supplier.getId().equals(order.getSupplier().getId())){
            throw new RuntimeException("Доступ запрещен: заказ не принадлежит данному клиенту");
        }

        List<OrderItem> items = orderItemRepository.findByOrder(order);

        return items.stream()
                .map(orderItem -> new OrderItemSupplierDtoResponse(
                        orderItem.getTitle(),
                        orderItem.getQuantity(),
                        orderItem.getSellingPrice(),
                        orderItem.getProductSku(),
                        orderItem.getTotalPrice(),
                        orderItem.getTotalCost(),
                        orderItem.getCostPrice(),
                        orderItem.getTotalCost().subtract(orderItem.getTotalPrice())
                ))
                .toList();
    }

    public Supplier getUserByPrincipal(Principal principal) {
        if (principal == null) {
            return null;
        }
        return supplierRepository.findByLogin(principal.getName()).orElse(new Supplier());
    }
}
