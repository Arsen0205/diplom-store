package com.example.diplom.controller;


import com.example.diplom.dto.request.CreateOrderDtoRequest;
import com.example.diplom.models.*;
import com.example.diplom.models.enums.OrderStatus;
import com.example.diplom.repository.*;
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
import java.util.Optional;

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

    //Проверка корзины перед оформлением заказа
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

    //Создание заказа
    @PostMapping("/create")
    public ResponseEntity<String> orderCreate(@RequestBody CreateOrderDtoRequest request, Principal principal){
        orderService.createdOrder(principal, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Заказ успешно оформлен");
    }

    //Информация о заказе
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

    @GetMapping("/confirmed/{id}")
    public ResponseEntity<String> confirmedOrder(@PathVariable("id") Long id, Principal principal){
        Order order = orderRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Такого заказа не существует"));

        Object currentUser = productService.getUserByPrincipal(principal);

        if(currentUser instanceof Supplier){
            Supplier supplier = (Supplier)productService.getUserByPrincipal(principal);
            if (supplier.getId().equals(order.getSupplier().getId())){
                order.setStatus(OrderStatus.CONFIRMED);
                orderRepository.save(order);
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

    @GetMapping("/cancelled/{id}")
    public ResponseEntity<String> cancelledOrder(@PathVariable("id") Long id, Principal principal){
        Object currentUser = productService.getUserByPrincipal(principal);
        Order order = orderRepository.findById(id).
                orElseThrow(()-> new RuntimeException("Такого заказа не существует"));
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        if (currentUser instanceof Client){
            return ResponseEntity.ok("Заказ отменен клиентом");
        }else if(currentUser instanceof Supplier) {
            return ResponseEntity.ok("Заказ отменен поставщиком");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Ошибка: неизвестный пользователь");
    }
}
