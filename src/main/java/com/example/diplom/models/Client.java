package com.example.diplom.models;

import com.example.diplom.models.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="clients")
@ToString(exclude = "orders")
public class Client implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Логин клиента
    @Column(name = "login", unique = true)
    private String login;

    //Пароль клиента
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

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Order> orders;

    @Enumerated(EnumType.STRING)
    private Role role;


    @PrePersist
    private void init(){dateOfCreated=LocalDateTime.now();}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    public boolean isSupplier(){return role.equals(Role.SUPPLIER);}

    public boolean isClient(){return role.equals(Role.SOLE_TRADER);}

    @Override
    public String getUsername() {
        return login;
    }
}
