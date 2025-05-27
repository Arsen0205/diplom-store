package com.example.diplom.repository;

import com.example.diplom.models.Cart;
import com.example.diplom.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByClientId(Long id);
    Optional<Cart> findByClient(Client client);
}
