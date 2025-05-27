package com.example.diplom.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class CartDtoResponse {
    private List<CartItemDtoResponse> items;
    private BigDecimal price;
}
