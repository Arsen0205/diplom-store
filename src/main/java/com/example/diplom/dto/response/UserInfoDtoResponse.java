package com.example.diplom.dto.response;

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
    private String chatId;
}
