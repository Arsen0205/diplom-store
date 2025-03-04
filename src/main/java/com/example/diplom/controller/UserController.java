package com.example.diplom.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(){
        return ResponseEntity.ok("Вы успешно зарегистрировались");
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(){
        return ResponseEntity.ok("Вы успешно зарегистрировались");
    }
}
