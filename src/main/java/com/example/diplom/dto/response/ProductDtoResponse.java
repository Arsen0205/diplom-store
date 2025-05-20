package com.example.diplom.dto.response;

import com.example.diplom.models.Image;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class ProductDtoResponse {
    private Long id;
    private String title;
    private int quantity;
    private BigDecimal price;
    private BigDecimal sellingPrice;
    private String productUrl;
}
