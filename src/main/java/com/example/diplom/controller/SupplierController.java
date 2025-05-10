package com.example.diplom.controller;


import com.example.diplom.dto.response.OrderItemSupplierDtoResponse;
import com.example.diplom.dto.response.OrderSupplierDtoResponse;
import com.example.diplom.dto.response.ProductDtoResponse;
import com.example.diplom.dto.response.SuppliersDtoResponse;
import com.example.diplom.service.SupplierService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/supplier")
public class SupplierController {
    private SupplierService supplierService;


    //Просмотр своего профиля
    @GetMapping("/me")
    public ResponseEntity<SuppliersDtoResponse> supplierOrders(Principal principal){
        return supplierService.supplierInfo(principal);
    }

    //Просмотр своих продуктов
    @GetMapping("/my/products")
    public ResponseEntity<List<ProductDtoResponse>> getProductSupplier(Principal principal){
        List<ProductDtoResponse> responses = supplierService.getProductSupplier(principal);
        return ResponseEntity.ok(responses);
    }

    //Просмотр своих заказов
    @GetMapping("/my/orders")
    public ResponseEntity<List<OrderSupplierDtoResponse>> getOrderSupplier(Principal principal){
        List<OrderSupplierDtoResponse> responses = supplierService.getSupplierOrders(principal);
        return ResponseEntity.ok(responses);
    }

    //Просмотр деталей заказа
    @GetMapping("/my/orders/{id}")
    public ResponseEntity<List<OrderItemSupplierDtoResponse>> orderInfo(@PathVariable Long id, Principal principal){
        List<OrderItemSupplierDtoResponse> responses = supplierService.orderInfo(id, principal);
        return ResponseEntity.ok(responses);
    }
}
