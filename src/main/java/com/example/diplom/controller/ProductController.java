package com.example.diplom.controller;

import com.example.diplom.dto.request.CreateProductDtoRequest;
import com.example.diplom.dto.response.MainDtoResponse;
import com.example.diplom.dto.response.ProductInfoMainDtoResponse;
import com.example.diplom.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Tag(name = "Product", description = "Операции по управлению продуктами")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/product")
public class ProductController {
    private final ProductService productService;

    @Operation(
            summary     = "Список всех продуктов",
            description = "Возвращает список всех продуктов текущего пользователя"
    )
    @GetMapping
    public ResponseEntity<List<MainDtoResponse>> product(Principal principal) {
        List<MainDtoResponse> responses = productService.getAllProduct();
        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary     = "Создание продукта",
            description = "Создаёт новый продукт с указанными параметрами и загружает изображения"
    )
    @PostMapping("/create")
    public ResponseEntity<String> createProduct(
            @RequestParam("title") String title,
            @RequestParam("quantity") int quantity,
            @RequestParam("price") BigDecimal price,
            @RequestParam("sellingPrice") BigDecimal sellingPrice,
            @RequestParam("images") List<MultipartFile> images,
            Principal principal
    ) throws IOException {
        CreateProductDtoRequest request = new CreateProductDtoRequest();
        request.setTitle(title);
        request.setQuantity(quantity);
        request.setPrice(price);
        request.setSellingPrice(sellingPrice);
        request.setImages(images);

        productService.createProduct(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Объявление создано!");
    }

    @Operation(
            summary     = "Информация о продукте",
            description = "Возвращает подробную информацию о продукте по его ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<ProductInfoMainDtoResponse> productInfo(@PathVariable Long id) {
        ProductInfoMainDtoResponse response = productService.productInfo(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary     = "Удаление продукта",
            description = "Удаляет продукт по указанному ID текущего пользователя"
    )
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(
            @PathVariable("id") Long id,
            Principal principal
    ) {
        productService.deleteProduct(id, principal);
        return ResponseEntity.ok("Продукт успешно удален");
    }
}
