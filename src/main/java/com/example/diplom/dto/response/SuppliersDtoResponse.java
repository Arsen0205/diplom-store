package com.example.diplom.dto.response;

import com.example.diplom.models.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class SuppliersDtoResponse {
    private Long id;
    private String login;
    private String fio;
    private String email;
    private String phoneNumber;
    private String loginTelegram;
    private String chatId;
    private boolean active;
    private Role role;

}
