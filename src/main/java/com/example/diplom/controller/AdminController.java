package com.example.diplom.controller;

import com.example.diplom.dto.response.ClientsDtoResponse;
import com.example.diplom.dto.response.MainDtoResponse;
import com.example.diplom.dto.response.SuppliersDtoResponse;
import com.example.diplom.service.AdminService;
import com.example.diplom.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {
    private AdminService adminService;
    private ProductService productService;

    @GetMapping("/clients")
    public ResponseEntity<List<ClientsDtoResponse>> getAllClients(){
        List<ClientsDtoResponse> responses = adminService.getAllClients();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/suppliers")
    public ResponseEntity<List<SuppliersDtoResponse>> getAllSuppliers(){
        List<SuppliersDtoResponse> responses = adminService.getAllSuppliers();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/products")
    public ResponseEntity<List<MainDtoResponse>> getAllProducts(){
        List<MainDtoResponse> responses = productService.getAllProduct();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/ban/{login}")
    public ResponseEntity<?> banUser(@PathVariable("login") String login){
        return ResponseEntity.ok(adminService.banUser(login));
    }

    @PostMapping("/anBan/{login}")
    public ResponseEntity<?> anBanUser(@PathVariable("login") String login){
        return ResponseEntity.ok(adminService.anBanUser(login));
    }

    @PostMapping("/products/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") Long id){
        return ResponseEntity.ok(adminService.deleteProduct(id));
    }
}
