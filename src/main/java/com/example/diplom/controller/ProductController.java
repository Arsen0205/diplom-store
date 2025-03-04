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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Slf4j
@Controller
@AllArgsConstructor
public class ProductController {
    private ProductService productService;
    private SupplierRepository supplierRepository;
    private ProductRepository productRepository;

    @GetMapping("/product")
    public String product(Model model, Principal principal){
        List<Product> products = productRepository.findAll();

        model.addAttribute("user", productService.getUserByPrincipal(principal));
        model.addAttribute("products", products);

        return "product";
    }

    @PostMapping("/product/create")
    public String createProduct(@Valid @ModelAttribute CreateProductDtoRequest request, Principal principal) throws IOException{
        Product product = productService.createProduct(request, principal);
        Supplier currentUser = product.getSupplier();
        return "redirect:/user/" + currentUser.getId();
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
