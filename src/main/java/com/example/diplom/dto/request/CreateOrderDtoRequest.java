package com.example.diplom.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateOrderDtoRequest {
    private String address;
    private String city;
}
