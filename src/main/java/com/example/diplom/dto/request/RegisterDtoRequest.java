package com.example.diplom.dto.request;

import com.example.diplom.models.enums.Role;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDtoRequest {
    @NotNull(message = "Данное поле не может быть пустым")
    private String login;
    @NotNull(message = "Данное поле не может быть пустым")
    @Size(min = 8, max = 50, message = "Имя должно быть длиной от 2 до 50 символов")
    private String password;
    private String loginTelegram;
    private String chatId;
    @NotNull(message = "Данное поле не может быть пустым")
    private Role role;
}
