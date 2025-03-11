package com.example.diplom.models;


import com.example.diplom.models.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="orders")
@Builder
@ToString(exclude = {"orderItems", "client", "supplier"})
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="total_cost", nullable = false)
    private BigDecimal totalCost;

    @Column(name="total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(name="profit", nullable = false)
    private BigDecimal profit;

    @Column(name="address", nullable = false)
    private String address;

    @Column(name="city")
    private String city;

    @Column(name="created_at", nullable = false)
    private LocalDateTime dateTime;

    @PrePersist
    private void init(){dateTime= LocalDateTime.now();}

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name="supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
}
