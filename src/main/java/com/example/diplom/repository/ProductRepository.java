package com.example.diplom.repository;


import com.example.diplom.dto.response.MainDtoResponse;
import com.example.diplom.models.Product;
import com.example.diplom.models.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findById(Long id);
    List<Product> findBySupplier(Supplier supplier);
    List<Product> findAllBySupplier(Supplier supplier);

}
