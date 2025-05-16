package com.example.diplom.controller;


import com.example.diplom.component.JwtUtil;
import com.example.diplom.dto.request.AuthenticationRequest;
import com.example.diplom.dto.request.RegisterAdminDtoRequest;
import com.example.diplom.dto.request.RegisterClientDtoRequest;
import com.example.diplom.dto.request.RegisterSupplierDtoRequest;
import com.example.diplom.dto.response.AdminInfoDtoResponse;
import com.example.diplom.dto.response.AuthenticationResponse;
import com.example.diplom.dto.response.UserInfoDtoResponse;
import com.example.diplom.repository.ClientRepository;
import com.example.diplom.repository.SupplierRepository;
import com.example.diplom.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private AuthService authService;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;


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
    public ResponseEntity<?> login(@Valid @RequestBody AuthenticationRequest request) {
        try {
            var auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.login(), request.password()
                    )
            );
            UserDetails ud = (UserDetails) auth.getPrincipal();
            String token = jwtUtil.generateToken(ud);
            return ResponseEntity.ok(new AuthenticationResponse(token));
        } catch (BadCredentialsException ex) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Неверные учётные данные");
        }
    }
}