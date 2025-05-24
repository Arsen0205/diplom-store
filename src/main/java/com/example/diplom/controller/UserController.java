package com.example.diplom.controller;

import com.example.diplom.dto.response.UserDtoResponse;
import com.example.diplom.dto.response.UserInfoDtoResponse;
import com.example.diplom.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "Просмотр профиля других пользователей")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    @Operation(
            summary     = "Просмотр профиля пользователя",
            description = "Возвращает информацию о пользователе по его уникальному ID"
    )
    @GetMapping("/{login}")
    public ResponseEntity<UserDtoResponse> userInfo(
            @Parameter(description = "ID пользователя", required = true, example = "1")
            @PathVariable("login") String login
    ) {
        return ResponseEntity.ok(userService.userInfo(login));
    }
}
