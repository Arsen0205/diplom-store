package com.example.diplom.controller;

import com.example.diplom.dto.request.AddCartDtoRequest;
import com.example.diplom.models.Cart;
import com.example.diplom.models.CartItem;
import com.example.diplom.models.Client;
import com.example.diplom.repository.CartItemRepository;
import com.example.diplom.service.CartService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class CartController {
    private CartService cartService;
    private CartItemRepository cartItemRepository;

    @PostMapping("/cart/add")
    public String addCart(@Valid @ModelAttribute AddCartDtoRequest request, Principal principal){
        cartService.addCart(request, principal);
        Client client = cartService.getUserByPrincipal(principal);
        return "redirect:/user/" + client.getId();
    }

    @GetMapping("/cart/remove/{itemId}")
    public String cartRemove(@PathVariable("itemId") Long id, Principal principal){
        cartService.cartRemove(id, principal);
        return "redirect:/user/" + cartService.getUserByPrincipal(principal).getId();
    }

    @GetMapping("/cart/decrease/{itemId}")
    public String decreaseItemQuantity(@PathVariable("itemId") Long itemId, Principal principal) {
        cartService.cartRemoveQuantity(itemId, principal);
        return "redirect:/user/" + cartService.getUserByPrincipal(principal).getId();
    }
}
