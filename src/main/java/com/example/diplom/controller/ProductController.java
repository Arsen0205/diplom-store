package com.example.diplom.controller;


import com.example.diplom.dto.request.CreateProductDtoRequest;
import com.example.diplom.dto.response.ProductInfoMainDtoResponse;
import com.example.diplom.models.Product;
import com.example.diplom.repository.ProductRepository;
import com.example.diplom.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/product")
public class ProductController {
    private ProductService productService;
    private ProductRepository productRepository;

    //Список всех продуктов
    @GetMapping
    public ResponseEntity<List<Product>> product(Principal principal){
        List<Product> products = productRepository.findAll();
        return ResponseEntity.ok(products);
    }

    //Создание продукта
    @PostMapping("/create")
    public ResponseEntity<String> createProduct(
            @RequestParam("title") String title,
            @RequestParam("quantity") int quantity,
            @RequestParam("price") BigDecimal price,
            @RequestParam("sellingPrice") BigDecimal sellingPrice,
            @RequestParam("images") List<MultipartFile> images,
            Principal principal) throws IOException {

        CreateProductDtoRequest request = new CreateProductDtoRequest();
        request.setTitle(title);
        request.setQuantity(quantity);
        request.setPrice(price);
        request.setSellingPrice(sellingPrice);
        request.setImages(images);

        productService.createProduct(request, principal);

        return ResponseEntity.status(HttpStatus.CREATED).body("Объявление создано!");
    }

    //Информация о продукте
    @GetMapping("/{id}")
    public ResponseEntity<ProductInfoMainDtoResponse> productInfo(@PathVariable Long id){
        ProductInfoMainDtoResponse response = productService.productInfo(id);
        return ResponseEntity.ok(response);
    }

    //Удаление продукта
    @DeleteMapping("/delete/{id}")
    private ResponseEntity<String> deleteProduct(@PathVariable("id") Long id, Principal principal){
        productService.deleteProduct(id, principal);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Продукт успешно удален");
    }
}
