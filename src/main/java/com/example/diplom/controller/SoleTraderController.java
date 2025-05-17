package com.example.diplom.controller;

import com.example.diplom.dto.response.ClientsDtoResponse;
import com.example.diplom.dto.response.OrderClientDtoResponse;
import com.example.diplom.dto.response.OrderItemClientDtoResponse;
import com.example.diplom.service.ClientService;
import com.example.diplom.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(
        name = "Client",
        description = "Операции для клиента (Sole Trader): просмотр профиля, корзины и заказов"
)
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/client")
public class SoleTraderController {
    private final OrderService orderService;
    private final ClientService clientService;

    @Operation(
            summary     = "Просмотр своего профиля",
            description = "Возвращает информацию о профиле текущего клиента"
    )
    @GetMapping("/me")
    public ResponseEntity<ClientsDtoResponse> userInfo(Principal principal) {
        return clientService.clientInfo(principal);
    }

    @Operation(
            summary     = "Просмотр корзины",
            description = "Возвращает содержимое корзины текущего клиента"
    )
    @GetMapping("/cart")
    public ResponseEntity<?> viewCart(Principal principal) {
        return clientService.getCart(principal);
    }

    @Operation(
            summary     = "Просмотр своих заказов",
            description = "Возвращает список всех заказов текущего клиента"
    )
    @GetMapping("/my/orders")
    public ResponseEntity<List<OrderClientDtoResponse>> getClientOrders(Principal principal) {
        List<OrderClientDtoResponse> responses = orderService.getOrdersByClient(principal);
        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary     = "Просмотр содержимого заказа",
            description = "Возвращает список позиций конкретного заказа текущего клиента"
    )
    @GetMapping("/my/orders/{id}")
    public ResponseEntity<List<OrderItemClientDtoResponse>> ordersInfo(
            @PathVariable("id") Long id,
            Principal principal
    ) {
        List<OrderItemClientDtoResponse> responses = orderService.ordersInfo(id, principal);
        return ResponseEntity.ok(responses);
    }
}
