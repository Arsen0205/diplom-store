package com.example.diplom.controller;


import com.example.diplom.dto.request.LoginDtoRequest;
import com.example.diplom.dto.request.RegisterDtoRequest;
import com.example.diplom.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class AuthController {
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterDtoRequest request){
        authService.register(request);
        return ResponseEntity.ok("Вы успешно зарегистрировались!");
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@Valid @RequestBody LoginDtoRequest request){
        authService.login(request);
        return ResponseEntity.ok("Вы успешно вошли");
    }
}
