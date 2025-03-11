package com.example.diplom.repository;

import com.example.diplom.models.Client;
import com.example.diplom.models.Order;
import com.example.diplom.models.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findBySupplierId(Long id);
    List<Order> findBySupplier(Supplier supplier);
    List<Order> findByClient(Client client);
}
