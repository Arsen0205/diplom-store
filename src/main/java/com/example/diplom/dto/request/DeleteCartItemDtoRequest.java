package com.example.diplom.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteCartItemDtoRequest {
    private Long id;
    private int quantity;
}
