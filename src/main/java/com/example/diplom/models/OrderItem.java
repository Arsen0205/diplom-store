package com.example.diplom.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="order_items")
@ToString(exclude = "order")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="quantity")
    private int quantity;

    @ManyToOne
    @JoinColumn(name="product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name="order_id", nullable = false)
    @JsonBackReference
    private Order order;

    @Column(name="cost_price", nullable = false)
    private BigDecimal costPrice;

    @Column(name="selling_price", nullable = false)
    private BigDecimal sellingPrice;

    @Column(name="total_cost")
    private BigDecimal totalCost;

    @Column(name="total_price")
    private BigDecimal totalPrice;
}
