package com.example.diplom.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class OrderItemSupplierDtoResponse {
    private String title;
    private int quantity;
    private BigDecimal sellingPrice;
    private Long productSku;
    private BigDecimal totalPrice;
    private BigDecimal totalCost;
    private BigDecimal costPrice;
    private BigDecimal profit;
    private String imageUrl;
}
