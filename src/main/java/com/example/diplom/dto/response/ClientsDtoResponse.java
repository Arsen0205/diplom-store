package com.example.diplom.dto.response;

import com.example.diplom.models.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientsDtoResponse {
    private Long id;
    private String login;
    private String loginTelegram;
    private String chatId;
    private boolean active;
    private String inn;
    private String ogrnip;
    private String email;
    private String phoneNumber;
    private String fio;
    private Role role;
}
