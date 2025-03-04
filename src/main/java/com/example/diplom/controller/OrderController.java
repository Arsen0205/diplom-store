package com.example.diplom.controller;


import com.example.diplom.models.Cart;
import com.example.diplom.models.Client;
import com.example.diplom.models.Order;
import com.example.diplom.models.Supplier;
import com.example.diplom.repository.CartRepository;
import com.example.diplom.repository.OrderRepository;
import com.example.diplom.repository.SupplierRepository;
import com.example.diplom.service.OrderService;
import com.example.diplom.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@AllArgsConstructor
public class OrderController {
    private OrderService orderService;
    private CartRepository cartRepository;
    private ProductService productService;
    private SupplierRepository supplierRepository;
    private OrderRepository orderRepository;

    @GetMapping("/order/checkout")
    public String checkoutPage(Model model, Principal principal) {
        Client client = orderService.getUserByPrincipal(principal);
        Cart cart = cartRepository.findByClientId(client.getId()).orElse(new Cart());

        if (cart.getItems().isEmpty()) {
            return "redirect:/user/" + client.getId();
        }

        model.addAttribute("cart", cart);
        return "create-order"; // Название Thymeleaf-шаблона оформления заказа
    }

    @PostMapping("/order/create")
    public String orderCreate(@RequestParam String address, @RequestParam String city, Principal principal){
        orderService.createdOrder(principal,address,city);

        return "redirect:/user/" + orderService.getUserByPrincipal(principal).getId();
    }

    @GetMapping("/order/info/{id}")
    public String orderInfo(@PathVariable("id") Long id, Model model, Principal principal){
        Object currentUser = productService.getUserByPrincipal(principal);
        Supplier supplier = supplierRepository.findByLogin(principal.getName()).orElseThrow(()-> new IllegalArgumentException("Пользователя не существует"));
        Order order = orderRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("Такого заказа не существует"));

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("user", supplier);
        model.addAttribute("order", order);

        return "order-info";
    }
}
