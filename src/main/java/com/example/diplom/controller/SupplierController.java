package com.example.diplom.controller;


import com.example.diplom.models.*;
import com.example.diplom.repository.*;
import com.example.diplom.service.ProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@AllArgsConstructor
@RequestMapping("/supplier")
public class SupplierController {
    private SupplierRepository supplierRepository;
    private ProductService productService;
    private OrderRepository orderRepository;


    @GetMapping("/orders/{id}")
    public ResponseEntity<?> supplierOrders(@PathVariable("id") Long userId, Principal principal){
        Supplier currentUser = (Supplier)productService.getUserByPrincipal(principal);
        Optional<Supplier> supplierOpt = supplierRepository.findById(userId);

        if (supplierOpt.isPresent()){
            if (!currentUser.getId().equals(supplierOpt.get().getId())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Вы не можете посмотреть заказы чужого поставщика!");
            }

            Supplier supplier = supplierOpt.get();
            List<Order> orders = orderRepository.findBySupplier(supplier);

            Map<String, Object> response = new HashMap<>();
            response.put("supplier", supplier);
            response.put("orders", orders);
            response.put("role","supplier");

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Поставщик не найден");
    }
}
