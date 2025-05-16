package com.example.diplom.dto.response;


import com.example.diplom.models.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminInfoDtoResponse {
    private Long id;
    private String login;
    private String password;
    private Role role;
}
