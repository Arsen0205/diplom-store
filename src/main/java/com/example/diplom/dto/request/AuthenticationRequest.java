package com.example.diplom.dto.request;

public record AuthenticationRequest(
        String login,
        String password
) {
}
