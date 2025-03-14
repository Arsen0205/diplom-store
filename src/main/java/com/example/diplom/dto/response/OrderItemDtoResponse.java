package com.example.diplom.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
public class OrderItemDtoResponse {
    private String productName;
    private int quantity;
    private BigDecimal sellingPrice;
    private BigDecimal totalPrice;
}
