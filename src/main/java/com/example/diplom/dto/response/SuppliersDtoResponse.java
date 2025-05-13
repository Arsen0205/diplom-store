package com.example.diplom.dto.response;

import com.example.diplom.models.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SuppliersDtoResponse {
    private Long id;
    private String login;
    private String loginTelegram;
    private String chatId;
    private boolean active;
    private String phoneNumber;
    private String fio;
    private String email;
    private Role role;
}
