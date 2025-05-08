package com.example.diplom.models;

import com.example.diplom.models.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

    //Инн клиента
    @Column(name="inn", unique = true)
    private String inn;

    //ОГРНИП клиента
    @Column(name = "OGRNIP", unique = true)
    private String ogrnip;

    //Платежный счет клиента
    @Column(name="payment_account", unique = true)
    private String paymentAccount;

    //Почтовый адрес клиента
    @Column(name="email", unique = true)
    private String email;

    //Номер телефона клиента
    @Column(name="phone_number", unique = true)
    private String phoneNumber;

    //ФИО клиента
    @Column(name="fio")
    private String fio;

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
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    public boolean isSupplier(){return role.equals(Role.SUPPLIER);}

    public boolean isClient(){return role.equals(Role.SOLE_TRADER);}

    @Override
    public String getUsername() {
        return login;
    }

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
