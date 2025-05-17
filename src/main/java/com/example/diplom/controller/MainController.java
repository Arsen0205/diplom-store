package com.example.diplom.controller;

import com.example.diplom.dto.response.MainDtoResponse;
import com.example.diplom.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.*;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Main", description = "Получение списка всех продуктов")
@RestController
@AllArgsConstructor
@RequestMapping("/")
public class MainController {
    private final ProductService productService;

    @Operation(
            summary     = "Получить все продукты",
            description = "Возвращает список всех продуктов с подробной информацией о каждом"
    )
    @GetMapping
    public ResponseEntity<List<MainDtoResponse>> getAllProduct() {
        return ResponseEntity.ok(productService.getAllProduct());
    }
}
