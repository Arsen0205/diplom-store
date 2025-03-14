package com.example.diplom.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
public class ReceiptDtoResponse {
    private Long orderId;
    private String clientLogin;
    private String clientTelegram;
    private String address;
    private String city;
    private LocalDateTime paymentDate;
    private BigDecimal totalCost;
    private BigDecimal totalPrice;
    private BigDecimal profit;
    private List<OrderItemDtoResponse> items;
}
