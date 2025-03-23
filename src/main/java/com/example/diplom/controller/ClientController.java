package com.example.diplom.controller;


import com.example.diplom.dto.response.CartDtoResponse;
import com.example.diplom.dto.response.ClientsDtoResponse;
import com.example.diplom.dto.response.OrderClientDtoResponse;
import com.example.diplom.dto.response.OrderItemClientDtoResponse;
import com.example.diplom.models.Cart;
import com.example.diplom.models.Client;
import com.example.diplom.repository.CartRepository;
import com.example.diplom.repository.ClientRepository;
import com.example.diplom.service.ClientService;
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
@RequestMapping("/client")
public class ClientController {
    private OrderService orderService;
    private ClientService clientService;


    @GetMapping
    public ResponseEntity<?> getCurrentsUser(Principal principal){
        return ResponseEntity.ok(Map.of("user", principal != null ? principal.getName() : "Guest"));
    }

    //Просмотр своего профиля
    @GetMapping("/{id}")
    public ResponseEntity<ClientsDtoResponse> userInfo(@PathVariable("id") Long userId, Principal principal) {
        return clientService.clientInfo(userId, principal);
    }

    @GetMapping("/cart")
    public ResponseEntity<?> viewCart(Principal principal){
        return clientService.getCart(principal);
    }

    //Просмотр и отслеживание заказов пользователя
    @GetMapping("/my/orders")
    public ResponseEntity<List<OrderClientDtoResponse>> getClientOrders(Principal principal){
        List<OrderClientDtoResponse> responses = orderService.getOrdersByClient(principal);
        return ResponseEntity.ok(responses);
    }

    //Просмотр содержимого заказа
    @GetMapping("/my/orders/{id}")
    public ResponseEntity<List<OrderItemClientDtoResponse>> ordersInfo(@PathVariable("id") Long id, Principal principal){
        List<OrderItemClientDtoResponse> responses = orderService.ordersInfo(id, principal);
        return ResponseEntity.ok(responses);
    }
}
