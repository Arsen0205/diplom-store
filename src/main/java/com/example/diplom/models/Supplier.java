package com.example.diplom.models;

import com.example.diplom.models.enums.Role;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@Table(name="suppliers")
public class Supplier implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Логин поставщика
    @Column(name = "login", unique = true)
    private String login;

    //Пароль поставщика
    @Column(name = "password")
    private String password;

    //Дата создания профиля
    @Column(name = "date_of_created")
    private LocalDateTime dateOfCreated;

    //Логин в телеграмме
    @Column(name="login_telegram", unique = true)
    private String loginTelegram;

    //Чат-айди телеграмма
    @Column(name = "chat_id", unique = true)
    private String chatId;

    //Разрешен ли доступ или нет
    @Column(name="active")
    private boolean active;

    @Column(name="fio")
    private String fio;

    @Column(name="email", unique = true)
    private String email;

    @Column(name="phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Role role;

    @PrePersist
    private void init(){dateOfCreated=LocalDateTime.now();}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return login;
    }

    public boolean isSupplier(){return role.equals(Role.SUPPLIER);}

    public boolean isClient(){return role.equals(Role.SOLE_TRADER);}

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
