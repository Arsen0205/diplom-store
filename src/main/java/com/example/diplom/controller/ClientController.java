package com.example.diplom.controller;


import com.example.diplom.dto.response.ClientsDtoResponse;
import com.example.diplom.dto.response.OrderClientDtoResponse;
import com.example.diplom.dto.response.OrderItemClientDtoResponse;
import com.example.diplom.service.ClientService;
import com.example.diplom.service.OrderService;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/client")
public class ClientController {
    private OrderService orderService;
    private ClientService clientService;

    //Просмотр своего профиля
    @GetMapping("/me")
    public ResponseEntity<ClientsDtoResponse> userInfo(Principal principal) {
        return clientService.clientInfo(principal);
    }

    //Просмотр корзины
    @GetMapping("/cart")
    public ResponseEntity<?> viewCart(Principal principal){
        return clientService.getCart(principal);
    }

    //Просмотр и отслеживание своих заказов
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
