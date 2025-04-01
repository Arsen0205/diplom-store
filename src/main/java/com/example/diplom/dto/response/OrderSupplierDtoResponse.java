package com.example.diplom.dto.response;


import com.example.diplom.models.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class OrderSupplierDtoResponse {
    private Long id;
    private String address;
    private String city;
    private BigDecimal profit;
    private OrderStatus status;
    private BigDecimal totalCost;
    private BigDecimal totalPrice;
    private String loginClient;
}
