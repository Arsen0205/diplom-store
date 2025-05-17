package com.example.diplom.controller;

import com.example.diplom.dto.response.ClientsDtoResponse;
import com.example.diplom.dto.response.MainDtoResponse;
import com.example.diplom.dto.response.SuppliersDtoResponse;
import com.example.diplom.service.AdminService;
import com.example.diplom.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin", description = "Административные операции: управление пользователями и продуктами")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final AdminService adminService;
    private final ProductService productService;

    @Operation(
            summary     = "Получить всех клиентов",
            description = "Возвращает список всех зарегистрированных клиентов"
    )
    @GetMapping("/clients")
    public ResponseEntity<List<ClientsDtoResponse>> getAllClients() {
        return ResponseEntity.ok(adminService.getAllClients());
    }

    @Operation(
            summary     = "Получить всех поставщиков",
            description = "Возвращает список всех зарегистрированных поставщиков"
    )
    @GetMapping("/suppliers")
    public ResponseEntity<List<SuppliersDtoResponse>> getAllSuppliers() {
        return ResponseEntity.ok(adminService.getAllSuppliers());
    }

    @Operation(
            summary     = "Получить все продукты",
            description = "Возвращает список всех продуктов вместе с данными поставщиков"
    )
    @GetMapping("/products")
    public ResponseEntity<List<MainDtoResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProduct());
    }

    @Operation(
            summary     = "Заблокировать пользователя",
            description = "Блокирует доступ пользователя по указанному логину"
    )
    @PostMapping("/ban/{login}")
    public ResponseEntity<?> banUser(@PathVariable("login") String login) {
        return ResponseEntity.ok(adminService.banUser(login));
    }

    @Operation(
            summary     = "Разблокировать пользователя",
            description = "Снимает блокировку с пользователя по указанному логину"
    )
    @PostMapping("/anBan/{login}")
    public ResponseEntity<?> anBanUser(@PathVariable("login") String login) {
        return ResponseEntity.ok(adminService.anBanUser(login));
    }

    @Operation(
            summary     = "Удалить продукт",
            description = "Удаляет продукт по указанному ID"
    )
    @PostMapping("/products/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") Long id) {
        return ResponseEntity.ok(adminService.deleteProduct(id));
    }
}
