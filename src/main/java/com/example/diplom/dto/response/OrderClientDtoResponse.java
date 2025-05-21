package com.example.diplom.dto.response;

import com.example.diplom.models.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class OrderClientDtoResponse {
    private Long id;
    private OrderStatus status;
    private BigDecimal totalCost;
    private LocalDateTime dateTime;
    private OrderItemClientDtoResponse items;
}
