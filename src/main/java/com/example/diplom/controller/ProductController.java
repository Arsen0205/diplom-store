package com.example.diplom.controller;


import com.example.diplom.dto.request.CreateProductDtoRequest;
import com.example.diplom.dto.response.ProductDtoResponse;
import com.example.diplom.models.Product;
import com.example.diplom.models.Supplier;
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
    public ResponseEntity<Product> createProduct(
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
        Product product = productService.createProduct(request, principal);
        System.out.println(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    //Информация о продукте
    @GetMapping("/{id}")
    public ResponseEntity<ProductDtoResponse> productInfo(@PathVariable Long id, Principal principal){
        Product product = productRepository.findById(id).orElseThrow(()-> new RuntimeException("Товара с таким id нет: " + id));

       ProductDtoResponse response = new ProductDtoResponse(
               product.getId(),
               product.getTitle(),
               product.getQuantity(),
               product.getPrice(),
               product.getSellingPrice(),
               product.getSupplier().getId()
       );

       return ResponseEntity.ok(response);
    }

    //Удаление продукта
    @PostMapping("/delete/{id}")
    private ResponseEntity<String> deleteProduct(@PathVariable("id") Long id, Principal principal){
        Product product = productRepository.findById(id).orElseThrow(()->new RuntimeException("Продукта нет"));
        Supplier currentUser = (Supplier) productService.getUserByPrincipal(principal);
        if(!product.getSupplier().getId().equals(currentUser.getId())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Вы не можете удалить этот продукт");
        }
        productRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Продукт успешно удален");
    }
}
