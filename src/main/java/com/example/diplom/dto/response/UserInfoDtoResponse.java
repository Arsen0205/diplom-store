package com.example.diplom.dto.response;

import com.example.diplom.models.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDtoResponse {
    private Long id;
    private String login;
    private String loginTelegram;
    private String inn;
    private String chatId;
    private String ogrnip;
    private String email;
    private String phoneNumber;
    private String fio;
    private String password;
    private Role role;
}
