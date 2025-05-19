package com.example.diplom.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CartItemDtoResponse {
    private Long id;
    private Long productId;
    private String supplierLogin;
    private String title;
    private int quantity;
    private String imageUrl;
    private BigDecimal price;
    private BigDecimal total;
}
