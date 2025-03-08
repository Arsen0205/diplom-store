package com.example.diplom.controller;


import com.example.diplom.dto.request.CreateProductDtoRequest;
import com.example.diplom.models.Product;
import com.example.diplom.models.Supplier;
import com.example.diplom.repository.ProductRepository;
import com.example.diplom.repository.SupplierRepository;
import com.example.diplom.service.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/product")
public class ProductController {
    private ProductService productService;
    private SupplierRepository supplierRepository;
    private ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<List<Product>> product(Principal principal){
        List<Product> products = productRepository.findAll();
        return ResponseEntity.ok(products);
    }

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

    @GetMapping("/product/{id}")
    public String productInfo(@PathVariable Long id, Model model, Principal principal){
        Product product = productRepository.findById(id).orElseThrow(()-> new RuntimeException("Товара с таким id нет: " + id));

        model.addAttribute("user", productService.getUserByPrincipal(principal));
        model.addAttribute("product", product);
        model.addAttribute("images",product.getImages());

        return "product-info";
    }

    @PostMapping("/product/delete/{id}")
    private String deleteProduct(@PathVariable("id") Long id){
        Product product = productRepository.findById(id).orElseThrow(()->new RuntimeException("Продукта нет"));
        Supplier currentUser = product.getSupplier();
        productRepository.deleteById(id);
        return "redirect:/user/" + currentUser.getId();
    }
}
