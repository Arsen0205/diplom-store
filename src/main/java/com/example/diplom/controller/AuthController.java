package com.example.diplom.controller;


import com.example.diplom.dto.request.LoginDtoRequest;
import com.example.diplom.dto.request.RegisterDtoRequest;
import com.example.diplom.dto.response.UserInfoDtoResponse;
import com.example.diplom.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class AuthController {
    private AuthService authService;

    //Регистрация нового пользователя
    @PostMapping("/register")
    public ResponseEntity<UserInfoDtoResponse> registerUser(@Valid @RequestBody RegisterDtoRequest request){
        UserInfoDtoResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    //Авторизация пользователя
    @PostMapping("/login")
    public ResponseEntity<UserInfoDtoResponse> loginUser(@Valid @RequestBody LoginDtoRequest request){
        UserInfoDtoResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}