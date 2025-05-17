package com.example.diplom.controller;

import com.example.diplom.dto.request.CreateOrderDtoRequest;
import com.example.diplom.models.Cart;
import com.example.diplom.repository.CartRepository;
import com.example.diplom.service.OrderService;
import com.example.diplom.service.PayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "Order", description = "Операции по оформлению и управлению заказами")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController {
    private final OrderService orderService;
    private final CartRepository cartRepository;
    private final PayService payService;

    @Operation(
            summary     = "Проверка корзины",
            description = "Проверяет, есть ли товары в корзине пользователя перед оформлением заказа"
    )
    @GetMapping("/checkout")
    public ResponseEntity<String> checkoutPage(Principal principal) {
        Cart cart = cartRepository
                .findByClientId(orderService.getUserByPrincipal(principal).getId())
                .orElse(new Cart());
        if (cart.getItems().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Корзина пустая");
        }
        return ResponseEntity.ok("Корзина готова к оформлению");
    }

    @Operation(
            summary     = "Создать заказ",
            description = "Оформляет новый заказ на основе текущей корзины пользователя"
    )
    @PostMapping("/create")
    public ResponseEntity<String> orderCreate(
            @RequestBody CreateOrderDtoRequest request,
            Principal principal
    ) {
        orderService.createdOrder(principal, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Заказ успешно оформлен");
    }

    @Operation(
            summary     = "Подтвердить заказ",
            description = "Меняет статус заказа на CONFIRMED"
    )
    @PostMapping("/confirmed/{id}")
    public ResponseEntity<?> confirmedOrder(
            @PathVariable("id") Long id,
            Principal principal
    ) {
        return ResponseEntity.ok(orderService.confirmedOrder(id, principal));
    }

    @Operation(
            summary     = "Отменить заказ",
            description = "Меняет статус заказа на CANCELLED"
    )
    @PostMapping("/cancelled/{id}")
    public ResponseEntity<?> cancelledOrder(
            @PathVariable("id") Long id,
            Principal principal
    ) {
        return ResponseEntity.ok(orderService.cancelledOrder(id, principal));
    }

    @Operation(
            summary     = "Отправить заказ",
            description = "Меняет статус заказа на SHIPPED"
    )
    @PostMapping("/shipped/{id}")
    public ResponseEntity<?> shippedOrder(
            @PathVariable("id") Long id,
            Principal principal
    ) {
        return ResponseEntity.ok(orderService.shippedOrder(id, principal));
    }

    @Operation(
            summary     = "Подтвердить доставку",
            description = "Меняет статус заказа на DELIVERED"
    )
    @PostMapping("/delivered/{id}")
    public ResponseEntity<?> deliveredOrder(
            @PathVariable("id") Long id,
            Principal principal
    ) {
        return ResponseEntity.ok(orderService.deliveredOrder(id, principal));
    }

    @Operation(
            summary     = "Оплатить заказ",
            description = "Генерирует и возвращает PDF-счёт для оплаты заказа"
    )
    @PostMapping("/pay/{id}")
    public ResponseEntity<byte[]> payOrder(
            @PathVariable("id") Long id,
            Principal principal
    ) {
        return payService.payForOrder(id, principal);
    }
}
