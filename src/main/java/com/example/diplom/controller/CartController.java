package com.example.diplom.controller;

import com.example.diplom.dto.request.AddCartDtoRequest;
import com.example.diplom.dto.request.DeleteCartItemDtoRequest;
import com.example.diplom.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "Cart", description = "Операции с корзиной пользователя")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/cart")
public class CartController {
    private final CartService cartService;

    @Operation(
            summary     = "Добавить товар в корзину",
            description = "Добавляет указанный товар и количество в корзину текущего пользователя"
    )
    @PostMapping("/add")
    public ResponseEntity<String> addCart(
            @Valid @RequestBody AddCartDtoRequest request,
            Principal principal
    ) {
        cartService.addCart(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Товар добавлен");
    }


    @Operation(
            summary     = "Удалить товар из корзины",
            description = "Удаляет весь указанный элемент корзины текущего пользователя по его ID"
    )

    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<String> cartRemove(
            @PathVariable("itemId") Long id,
            Principal principal
    ) {
        cartService.cartRemove(id, principal);
        return ResponseEntity.ok("Вы удалили товар из корзины");
    }


    @Operation(
            summary     = "Уменьшить количество товара в корзине",
            description = "Уменьшает количество указанного элемента корзины текущего пользователя"
    )
    @PostMapping("/decrease")
    public ResponseEntity<String> decreaseItemQuantity(
            @Valid @RequestBody DeleteCartItemDtoRequest request,
            Principal principal
    ) {
        cartService.cartRemoveQuantity(request, principal);
        return ResponseEntity.ok("Продукт удален в количестве: " + request.getQuantity());
    }

    @PostMapping("/increase")
    public ResponseEntity<String> increaseItemQuantity(
            @Valid @RequestBody DeleteCartItemDtoRequest request,
            Principal principal
    ) {
        cartService.increaseItemQuantity(request, principal);
        return ResponseEntity.ok("Продукт удален в количестве: " + request.getQuantity());
    }
}
