package com.example.diplom.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name="images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name="originalFileName")
    private String originalFileName;

    @Column(name="size")
    private Long size;

    @Column(name="contentType")
    private String contentType;

    @Column(name="isPreviewImage")
    private boolean isPreviewImage;

    @Lob
    private byte[] bytes;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
