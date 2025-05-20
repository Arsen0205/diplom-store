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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "Регистрация и аутентификация пользователей")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserInfoService userInfoService;

    @Operation(
            summary     = "Регистрация поставщика",
            description = "Регистрирует нового поставщика с ролью SUPPLIER"
    )
    @ApiResponse(responseCode = "200", description = "Поставщик зарегистрирован",
            content = @Content(
                    mediaType = "application/json",
                    schema    = @Schema(implementation = UserInfoDtoResponse.class)
            )
    )
    @PostMapping("/register/supplier")
    public ResponseEntity<UserInfoDtoResponse> registerSupplier(
            @Valid @RequestBody RegisterSupplierDtoRequest request
    ) {
        return ResponseEntity.ok(authService.registerSupplier(request));
    }

    @Operation(
            summary     = "Регистрация клиента",
            description = "Регистрирует нового клиента с ролью SOLE_TRADER"
    )
    @ApiResponse(responseCode = "200", description = "Клиент зарегистрирован",
            content = @Content(
                    mediaType = "application/json",
                    schema    = @Schema(implementation = UserInfoDtoResponse.class)
            )
    )
    @PostMapping("/register/client")
    public ResponseEntity<UserInfoDtoResponse> registerClient(
            @Valid @RequestBody RegisterClientDtoRequest request
    ) {
        return ResponseEntity.ok(authService.registerClient(request));
    }

    @Operation(
            summary     = "Регистрация администратора",
            description = "Регистрирует нового администратора с ролью ADMIN"
    )
    @ApiResponse(responseCode = "200", description = "Администратор зарегистрирован",
            content = @Content(
                    mediaType = "application/json",
                    schema    = @Schema(implementation = AdminInfoDtoResponse.class)
            )
    )
    @PostMapping("/register/admin")
    public ResponseEntity<AdminInfoDtoResponse> registerAdmin(
            @Valid @RequestBody RegisterAdminDtoRequest request
    ) {
        return ResponseEntity.ok(authService.registerAdmin(request));
    }

    @Operation(
            summary     = "Аутентификация (логин)",
            description = "Проверяет логин и пароль, возвращает JWT и данные пользователя"
    )
    @ApiResponse(responseCode = "200", description = "Успешная аутентификация",
            content = @Content(
                    mediaType = "application/json",
                    schema    = @Schema(implementation = LoginResponse.class)
            )
    )
    @ApiResponse(responseCode = "401", description = "Неверные учётные данные")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody AuthenticationRequest request
    ) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getLogin(), request.getPassword()
                    )
            );
            String token = jwtUtil.generateToken((UserDetails) auth.getPrincipal());
            UserInfoDtoResponse userDto = userInfoService.getByLogin(request.getLogin());
            return ResponseEntity.ok(new LoginResponse(token, userDto));
        }catch (DisabledException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(null);
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
