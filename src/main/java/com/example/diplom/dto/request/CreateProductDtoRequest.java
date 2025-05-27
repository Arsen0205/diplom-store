package com.example.diplom.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateProductDtoRequest {
    private String title;
    private int quantity;
    private BigDecimal price;
    private BigDecimal sellingPrice;
    private List<MultipartFile> images;
}
