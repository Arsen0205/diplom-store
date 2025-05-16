package com.example.diplom.dto.request;


import com.example.diplom.models.enums.Role;
import lombok.Data;

@Data
public class RegisterAdminDtoRequest {
    private String login;
    private String password;
    private Role role;
}
