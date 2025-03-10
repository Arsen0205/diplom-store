package com.example.diplom.controller;


import com.example.diplom.models.Cart;
import com.example.diplom.models.Client;
import com.example.diplom.models.Product;
import com.example.diplom.models.Supplier;
import com.example.diplom.repository.CartRepository;
import com.example.diplom.repository.ClientRepository;
import com.example.diplom.repository.ProductRepository;
import com.example.diplom.repository.SupplierRepository;
import com.example.diplom.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    private ProductService productService;
    private SupplierRepository supplierRepository;
    private ProductRepository productRepository;
    private ClientRepository clientRepository;
    private CartRepository cartRepository;


    @GetMapping
    public ResponseEntity<?> getCurrentsUser(Principal principal){
        return ResponseEntity.ok(Map.of("user", principal != null ? principal.getName() : "Guest"));
    }

    @GetMapping("/user/{id}")
    public String userInfo(@PathVariable("id") Long userId, Model model, Principal principal) {
        Object currentUser = productService.getUserByPrincipal(principal);
        model.addAttribute("currentUser", currentUser); // Добавляем текущего пользователя в модель

        Optional<Supplier> supplierOpt = supplierRepository.findById(userId);
        if (supplierOpt.isPresent()) { // Проверяем, найден ли поставщик
            Supplier supplier = supplierOpt.get();
            List<Product> products = productRepository.findBySupplier(supplier);

            model.addAttribute("user", supplier);
            model.addAttribute("products", products);
            model.addAttribute("role", "supplier");
            return "user-info";
        }

        Optional<Client> clientOpt = clientRepository.findById(userId);
        if (clientOpt.isPresent()) { // Проверяем, найден ли клиент
            Client client = clientOpt.get();
            Cart cart =  cartRepository.findByClient(client).orElse(new Cart());

            model.addAttribute("cart", cart);
            model.addAttribute("user", client);
            model.addAttribute("role", "client");
            return "user-info";
        }

        // Если пользователь не найден, перенаправляем на страницу ошибки
        return "redirect:/error";
    }
}
