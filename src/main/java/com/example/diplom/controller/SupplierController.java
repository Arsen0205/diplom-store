package com.example.diplom.controller;

import com.example.diplom.dto.response.OrderItemSupplierDtoResponse;
import com.example.diplom.dto.response.OrderSupplierDtoResponse;
import com.example.diplom.dto.response.ProductDtoResponse;
import com.example.diplom.dto.response.SuppliersDtoResponse;
import com.example.diplom.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "Supplier", description = "Операции для поставщика: просмотр профиля, продуктов и заказов")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/supplier")
public class SupplierController {
    private final SupplierService supplierService;

    @Operation(
            summary     = "Просмотр профиля поставщика",
            description = "Возвращает информацию о текущем поставщике"
    )
    @GetMapping("/me")
    public ResponseEntity<SuppliersDtoResponse> supplierOrders(Principal principal) {
        return supplierService.supplierInfo(principal);
    }

    @Operation(
            summary     = "Просмотр продуктов поставщика",
            description = "Возвращает список продуктов, созданных текущим поставщиком"
    )
    @GetMapping("/my/products")
    public ResponseEntity<List<ProductDtoResponse>> getProductSupplier(Principal principal) {
        List<ProductDtoResponse> responses = supplierService.getProductSupplier(principal);
        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary     = "Просмотр заказов поставщика",
            description = "Возвращает список всех заказов, в которых участвует текущий поставщик"
    )
    @GetMapping("/my/orders")
    public ResponseEntity<List<OrderSupplierDtoResponse>> getOrderSupplier(Principal principal) {
        List<OrderSupplierDtoResponse> responses = supplierService.getSupplierOrders(principal);
        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary     = "Просмотр деталей заказа поставщика",
            description = "Возвращает содержимое указанного заказа для текущего поставщика"
    )
    @GetMapping("/my/orders/{id}")
    public ResponseEntity<List<OrderItemSupplierDtoResponse>> orderInfo(
            @PathVariable Long id,
            Principal principal
    ) {
        List<OrderItemSupplierDtoResponse> responses = supplierService.orderInfo(id, principal);
        return ResponseEntity.ok(responses);
    }
}
