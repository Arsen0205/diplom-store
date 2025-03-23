package com.example.diplom.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class OrderItemClientDtoResponse {
    private String title;
    private int quantity;
    private BigDecimal sellingPrice;
    private Long productSku;
    private BigDecimal totalPrice;
}
