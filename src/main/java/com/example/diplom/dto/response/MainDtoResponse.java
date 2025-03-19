package com.example.diplom.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class MainDtoResponse {
    private Long id;
    private String title;
    private BigDecimal price;
}
