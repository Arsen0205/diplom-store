package com.example.diplom.service;

import com.example.diplom.dto.response.ProductDtoResponse;
import com.example.diplom.dto.response.SuppliersDtoResponse;
import com.example.diplom.models.Product;
import com.example.diplom.models.Supplier;
import com.example.diplom.repository.ProductRepository;
import com.example.diplom.repository.SupplierRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;


@Service
@AllArgsConstructor
public class SupplierService {
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;


    public ResponseEntity<SuppliersDtoResponse> supplierInfo(Long id, Principal principal){
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Пользователь не найден"));

        Supplier currentUser = getUserByPrincipal(principal);

        if (!supplier.getId().equals(currentUser.getId())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Вы не можете посмотреть личную информацию чужого пользователя");
        }

        SuppliersDtoResponse suppliersDtoResponse = new SuppliersDtoResponse(
                supplier.getId(),
                supplier.getLogin(),
                supplier.getLoginTelegram(),
                supplier.getChatId(),
                supplier.isActive(),
                supplier.getRole()
        );

        return ResponseEntity.ok(suppliersDtoResponse);
    }

    @Transactional
    public List<ProductDtoResponse> getProductSupplier(Principal principal){
        Supplier supplier = getUserByPrincipal(principal);

        List<Product> products = productRepository.findBySupplier(supplier);

        return products.stream()
                .map(product -> new ProductDtoResponse(
                        product.getId(),
                        product.getTitle(),
                        product.getQuantity(),
                        product.getPrice(),
                        product.getSellingPrice()
                ))
                .toList();
    }


    public Supplier getUserByPrincipal(Principal principal) {
        if (principal == null) {
            return null;
        }
        return supplierRepository.findByLogin(principal.getName()).orElse(new Supplier());
    }
}
