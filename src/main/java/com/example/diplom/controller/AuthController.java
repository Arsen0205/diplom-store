package com.example.diplom.controller;


import com.example.diplom.component.JwtUtil;
import com.example.diplom.dto.request.AuthenticationRequest;
import com.example.diplom.dto.request.RegisterAdminDtoRequest;
import com.example.diplom.dto.request.RegisterClientDtoRequest;
import com.example.diplom.dto.request.RegisterSupplierDtoRequest;
import com.example.diplom.dto.response.AdminInfoDtoResponse;
import com.example.diplom.dto.response.LoginResponse;
import com.example.diplom.dto.response.UserInfoDtoResponse;
import com.example.diplom.service.AuthService;
import com.example.diplom.service.UserInfoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private AuthService authService;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserInfoService userInfoService;


    //Регистрация нового поставщика
    @PostMapping("/register/supplier")
    public ResponseEntity<UserInfoDtoResponse> registerSupplier(@Valid @RequestBody RegisterSupplierDtoRequest request){
        UserInfoDtoResponse response = authService.registerSupplier(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/client")
    public ResponseEntity<UserInfoDtoResponse> registerClient(@Valid @RequestBody RegisterClientDtoRequest request){
        UserInfoDtoResponse response = authService.registerClient(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/admin")
    public ResponseEntity<AdminInfoDtoResponse> registerAdmin(@Valid @RequestBody RegisterAdminDtoRequest request){
        AdminInfoDtoResponse response = authService.registerAdmin(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody AuthenticationRequest request) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getLogin(), request.getPassword()
                    )
            );
            // 1) Генерируем JWT
            String token = jwtUtil.generateToken((UserDetails) auth.getPrincipal());

            // 2) Получаем DTO с данными пользователя
            UserInfoDtoResponse userDto = userInfoService.getByLogin(request.getLogin());

            // 3) Возвращаем вместе
            return ResponseEntity.ok(new LoginResponse(token, userDto));
        } catch (BadCredentialsException ex) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(null);
        }
    }
}