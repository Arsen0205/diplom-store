package com.example.diplom.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @GetMapping
    public ResponseEntity<?> getCurrentsUser(Principal principal){
        return ResponseEntity.ok(Map.of("user", principal != null ? principal.getName() : "Guest"));
    }
}
