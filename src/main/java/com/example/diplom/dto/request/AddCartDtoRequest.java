package com.example.diplom.dto.request;

import lombok.Data;

@Data
public class AddCartDtoRequest {
    private Long productId;
    private int quantity;
}
