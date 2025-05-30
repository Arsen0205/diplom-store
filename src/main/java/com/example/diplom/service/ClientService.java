package com.example.diplom.service;


import com.example.diplom.dto.response.CartDtoResponse;
import com.example.diplom.dto.response.CartItemDtoResponse;
import com.example.diplom.dto.response.ClientsDtoResponse;
import com.example.diplom.models.Cart;
import com.example.diplom.models.Client;
import com.example.diplom.models.Image;
import com.example.diplom.models.Product;
import com.example.diplom.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ClientService {
    private final ProductService productService;
    private final CartRepository cartRepository;


    public ResponseEntity<ClientsDtoResponse> clientInfo(Principal principal){
        Client currentUser = (Client) productService.getUserByPrincipal(principal);

        ClientsDtoResponse clientsDtoResponse = new ClientsDtoResponse(
                currentUser.getId(),
                currentUser.getLogin(),
                currentUser.getLoginTelegram(),
                currentUser.getChatId(),
                currentUser.isActive(),
                currentUser.getRole()
        );

        return ResponseEntity.ok(clientsDtoResponse);
    }

    public ResponseEntity<?> getCart(Principal principal) {
        Client client = (Client) productService.getUserByPrincipal(principal);

        Optional<Cart> optionalCart = cartRepository.findByClient(client);

        if (optionalCart.isEmpty() || optionalCart.get().getItems().isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Корзина пустая");
        }

        Cart cart = optionalCart.get();

        List<CartItemDtoResponse> items = cart.getItems().stream().map(cartItem -> {
            Product product = cartItem.getProduct();

            String imageUrl = product.getImages().stream()
                    .filter(Image::isPreviewImage)
                    .map(Image::getUrl)
                    .findFirst()
                    .orElseGet(() -> product.getImages().stream()
                            .map(Image::getUrl)
                            .findFirst()
                            .orElse("/images/placeholder.png")
                    );

            BigDecimal total = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            return new CartItemDtoResponse(
                    cartItem.getId(),
                    product.getId(),
                    product.getSupplier().getLogin(),
                    product.getTitle(),
                    cartItem.getQuantity(),
                    imageUrl,
                    product.getPrice(),
                    total
            );
        }).toList();

        return ResponseEntity.ok(new CartDtoResponse(items, cart.getPrice()));
    }
}
