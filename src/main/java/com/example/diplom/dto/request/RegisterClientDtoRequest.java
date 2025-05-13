package com.example.diplom.dto.request;

import com.example.diplom.models.enums.Role;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterClientDtoRequest {
    @NotNull(message = "Данное поле не может быть пустым")
    private String login;
    @NotNull(message = "Данное поле не может быть пустым")
    @Size(min = 8, max = 50, message = "Имя должно быть длиной от 2 до 50 символов")
    private String password;
    @NotNull(message = "Данное поле не может быть пустым")
    private String loginTelegram;
    @NotNull(message = "Данное поле не может быть пустым")
    private String chatId;
    @NotNull(message = "Данное поле не может быть пустым")
    private String inn;
    @NotNull(message = "Данное поле не может быть пустым")
    private String ogrnip;
    @NotNull(message = "Данное поле не может быть пустым")
    private String email;
    @NotNull(message = "Данное поле не может быть пустым")
    private String phoneNumber;
    @NotNull(message = "Данное поле не может быть пустым")
    private String fio;
    @NotNull(message = "Данное поле не может быть пустым")
    private Role role;
}
