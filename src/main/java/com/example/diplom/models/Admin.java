package com.example.diplom.models;


import com.example.diplom.models.enums.Role;
import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
@Table(name="admins")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="login", unique = true)
    private String login;

    @Column(name="password")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
}
