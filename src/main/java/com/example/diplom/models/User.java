package com.example.diplom.models;


import com.example.diplom.models.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@Table(name="users")
public class User {
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
