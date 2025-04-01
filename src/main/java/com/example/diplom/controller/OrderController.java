package com.example.diplom.controller;


import com.example.diplom.dto.request.CreateOrderDtoRequest;
import com.example.diplom.models.*;
import com.example.diplom.repository.*;
import com.example.diplom.service.OrderService;
import com.example.diplom.service.PayService;
import com.example.diplom.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
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
    private PayService payService;

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
        //Object currentUser = productService.getUserByPrincipal(principal);
        Supplier supplier = supplierRepository.findByLogin(principal.getName()).orElseThrow(()-> new IllegalArgumentException("Пользователя не существует"));
        Order order = orderRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("Такого заказа не существует"));

        if (supplier.getId().equals(order.getSupplier().getId())) {
            Map<String, Object> response = new HashMap<>();

            response.put("order", order);
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Ошибка просмотра заказа: вы не можете смотреть чужие заказы");
    }

    @PostMapping("/confirmed/{id}")
    public ResponseEntity<?> confirmedOrder(@PathVariable("id") Long id, Principal principal){
        return ResponseEntity.ok(orderService.confirmedOrder(id, principal));
    }

    @PostMapping("/cancelled/{id}")
    public ResponseEntity<?> cancelledOrder(@PathVariable("id") Long id, Principal principal){
        return ResponseEntity.ok(orderService.cancelledOrder(id, principal));
    }

    @PostMapping("/shipped/{id}")
    public ResponseEntity<?> shippedOrder(@PathVariable("id") Long id, Principal principal){
        return ResponseEntity.ok(orderService.shippedOrder(id,principal));
    }

    @PostMapping("/delivered/{id}")
    public ResponseEntity<?> deliveredOrder(@PathVariable("id") Long id, Principal principal){
        return ResponseEntity.ok(orderService.deliveredOrder(id, principal));
    }

    @PostMapping("/pay/{id}")
    public ResponseEntity<byte[]> payOrder(@PathVariable("id") Long id, Principal principal) {
        return payService.payForOrder(id, principal);
    }


}
