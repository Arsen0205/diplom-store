package com.example.diplom.controller;


import com.example.diplom.dto.request.CreateOrderDtoRequest;
import com.example.diplom.models.*;
import com.example.diplom.repository.CartRepository;
import com.example.diplom.repository.OrderItemRepository;
import com.example.diplom.repository.OrderRepository;
import com.example.diplom.repository.SupplierRepository;
import com.example.diplom.service.OrderService;
import com.example.diplom.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private OrderService orderService;
    private CartRepository cartRepository;
    private ProductService productService;
    private SupplierRepository supplierRepository;
    private OrderRepository orderRepository;
    private OrderItemRepository orderItemRepository;

    @GetMapping("/checkout")
    public ResponseEntity<String> checkoutPage(Principal principal) {
        Cart cart = cartRepository.findByClientId(orderService.getUserByPrincipal(principal).getId()).orElse(new Cart());

        if (cart.getItems().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Корзина пустая");
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body("Корзина готова к оформлению");
    }

    @PostMapping("/create")
    public ResponseEntity<String> orderCreate(@RequestBody CreateOrderDtoRequest request, Principal principal){
        orderService.createdOrder(principal, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Заказ успешно оформлен");
    }

    @GetMapping("/info/{id}")
    public ResponseEntity<?> orderInfo(@PathVariable("id") Long id, Principal principal){
        Object currentUser = productService.getUserByPrincipal(principal);
        Supplier supplier = supplierRepository.findByLogin(principal.getName()).orElseThrow(()-> new IllegalArgumentException("Пользователя не существует"));
        Order order = orderRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("Такого заказа не существует"));

        Map<String, Object> response = new HashMap<>();
        List<OrderItem> items = orderItemRepository.findByOrder(order);

        response.put("order", order);

        return ResponseEntity.ok(response);
    }
}
