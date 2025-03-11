package com.example.diplom.controller;

import com.example.diplom.dto.request.AddCartDtoRequest;
import com.example.diplom.dto.request.DeleteCartItemDtoRequest;
import com.example.diplom.repository.CartItemRepository;
import com.example.diplom.service.CartService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@RestController
@AllArgsConstructor
@RequestMapping("/cart")
public class CartController {
    private CartService cartService;
    private CartItemRepository cartItemRepository;

    //Добавление товара в заказ
    @PostMapping("/add")
    public ResponseEntity<String> addCart(@Valid @RequestBody AddCartDtoRequest request, Principal principal){
        cartService.addCart(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Товар добавлен");
    }

    //Удаление определенного товара в корзине
    @GetMapping("/remove/{itemId}")
    public ResponseEntity<String> cartRemove(@PathVariable("itemId") Long id, Principal principal){
        cartService.cartRemove(id, principal);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Вы удалили товар из корзины");
    }

    //Удаление определенного кол-ва товара в корзине
    @PostMapping("/decrease")
    public ResponseEntity<String> decreaseItemQuantity(@RequestBody DeleteCartItemDtoRequest request, Principal principal) {
        cartService.cartRemoveQuantity(request, principal);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Продукт удален в количестве: " + request.getQuantity());
    }
}
