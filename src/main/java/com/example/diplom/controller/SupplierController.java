package com.example.diplom.controller;


import com.example.diplom.dto.response.ProductDtoResponse;
import com.example.diplom.dto.response.SuppliersDtoResponse;
import com.example.diplom.repository.*;
import com.example.diplom.service.ProductService;
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
@RequestMapping("/supplier")
public class SupplierController {
    private SupplierService supplierService;


    //Просмотр своего профиля
    @GetMapping("/{id}")
    public ResponseEntity<SuppliersDtoResponse> supplierOrders(@PathVariable("id") Long userId, Principal principal){
        return supplierService.supplierInfo(userId, principal);
    }

    @GetMapping("/my/product")
    public ResponseEntity<List<ProductDtoResponse>> getProductSupplier(Principal principal){
        List<ProductDtoResponse> responses = supplierService.getProductSupplier(principal);
        return ResponseEntity.ok(responses);
    }

}
