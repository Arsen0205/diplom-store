package com.example.diplom.controller;


import com.example.diplom.models.*;
import com.example.diplom.repository.*;
import com.example.diplom.service.ProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@AllArgsConstructor
public class SupplierController {

    private ProductRepository productRepository;
    private SupplierRepository supplierRepository;
    private ProductService productService;
    private ClientRepository clientRepository;
    private CartRepository cartRepository;
    private OrderRepository orderRepository;

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


    @GetMapping("/supplier/orders/{id}")
    public String supplierOrders(@PathVariable("id") Long userId, Model model, Principal principal){
        Object currentUser = productService.getUserByPrincipal(principal);
        model.addAttribute("currentUser", currentUser); // Добавляем текущего пользователя в модель
        Optional<Supplier> supplierOpt = supplierRepository.findById(userId);

        if (supplierOpt.isPresent()){
            Supplier supplier = supplierOpt.get();
            List<Order> orders = orderRepository.findBySupplier(supplier);

            model.addAttribute("user", supplier);
            model.addAttribute("orders", orders);
            model.addAttribute("role", "supplier");
            return "/suppliers-orders";
        }

        return "redirect:/error";
    }
}
