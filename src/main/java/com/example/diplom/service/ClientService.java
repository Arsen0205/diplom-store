package com.example.diplom.service;


import com.example.diplom.dto.response.CartDtoResponse;
import com.example.diplom.dto.response.CartItemDtoResponse;
import com.example.diplom.dto.response.ClientsDtoResponse;
import com.example.diplom.models.Cart;
import com.example.diplom.models.Client;
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
    private final ClientRepository clientRepository;
    private final ProductService productService;
    private final CartRepository cartRepository;


    public ResponseEntity<ClientsDtoResponse> clientInfo(Long id, Principal principal){
        Client client = clientRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Пользователь не найден"));

        Client currentUser = (Client) productService.getUserByPrincipal(principal);

        if (!client.getId().equals(currentUser.getId())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .build();
        }

        ClientsDtoResponse clientsDtoResponse = new ClientsDtoResponse(
                client.getId(),
                client.getLogin(),
                client.getLoginTelegram(),
                client.getChatId(),
                client.isActive(),
                client.getRole()
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
            BigDecimal total = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            return new CartItemDtoResponse(
                    product.getId(),
                    product.getTitle(),
                    cartItem.getQuantity(),
                    product.getPrice(),
                    total
            );
        }).toList();

        return ResponseEntity.ok(new CartDtoResponse(items, cart.getPrice()));
    }
}
