package com.example.diplom.controller;


import com.example.diplom.dto.response.MainDtoResponse;
import com.example.diplom.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/")
public class MainController {
    private ProductService productService;


    @GetMapping
    public ResponseEntity<List<MainDtoResponse>> getAllProduct(){
        List<MainDtoResponse> responses = productService.getAllProduct();
        return ResponseEntity.ok(responses);
    }
}
