package com.example.diplom.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class MainDtoResponse {
    private Long id;
    private String title;
    private BigDecimal price;
    private int quantity;
    private String imageUrl;
}
