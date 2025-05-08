package com.example.diplom.controller;


import com.example.diplom.component.JwtUtil;
import com.example.diplom.dto.request.AuthenticationRequest;
import com.example.diplom.dto.request.LoginDtoRequest;
import com.example.diplom.dto.request.RegisterDtoRequest;
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
public class AuthController {
    private AuthService authService;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final ClientRepository clientRepo;
    private final SupplierRepository supplierRepo;
    private final PasswordEncoder encoder;

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

    @PostMapping("/api/auth/login")
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