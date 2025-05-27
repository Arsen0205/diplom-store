package com.example.diplom.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name="products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name="quantity", nullable = false)
    private int quantity;

    @Column(name="selling_price", nullable = false)
    private BigDecimal sellingPrice; // Себестоимость товара, чтобы было удобнее считать прибыль

    @Column(name = "price", nullable = false)
    private BigDecimal price;// За сколько товар будет продан

    @Column(name = "previewImageId")
    private Long previewImageId;

    @ManyToOne
    @JoinColumn(name="supplier_id", nullable = false)
    @JsonIgnore
    private Supplier supplier;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Image> images = new ArrayList<>();
}
