package com.example.diplom.controller;


import com.example.diplom.dto.request.LoginDtoRequest;
import com.example.diplom.dto.request.RegisterDtoRequest;
import com.example.diplom.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
public class AuthController {
    private AuthService authService;

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute RegisterDtoRequest request){
        authService.register(request);
        System.out.println("Переданные данные: " + request);
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String register(){return "register";}

    @GetMapping("/login")
    public String login(){return "login";}

    @PostMapping("/login")
    public String loginUser(@Valid @RequestBody LoginDtoRequest request){
        authService.login(request);
        return "/product";
    }
}
