package com.example.diplom.service;

import com.example.diplom.dto.response.ProductDtoResponse;
import com.example.diplom.dto.response.UserDtoResponse;
import com.example.diplom.dto.response.UserInfoDtoResponse;
import com.example.diplom.models.Client;
import com.example.diplom.models.Image;
import com.example.diplom.models.Product;
import com.example.diplom.models.Supplier;
import com.example.diplom.repository.ClientRepository;
import com.example.diplom.repository.ProductRepository;
import com.example.diplom.repository.SupplierRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final ClientRepository clientRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;

    public UserDtoResponse userInfo(String login){
        Supplier supplier = supplierRepository.findByLogin(login)
                .orElseThrow(()-> new UsernameNotFoundException("Пользователя с такими логином не найден"));

        List<Product> products = productRepository.findAllBySupplier(supplier);

        List<ProductDtoResponse> productDtoResponses = products.stream()
                .map(p->{
                    String url = p.getImages().stream()
                            .filter(Image::isPreviewImage)
                            .map(Image::getUrl)
                            .findFirst()
                            .orElse("/images/placeholder.png");
                    return new ProductDtoResponse(
                            p.getId(),
                            p.getTitle(),
                            p.getQuantity(),
                            p.getPrice(),
                            p.getSellingPrice(),
                            url
                    );
                })
                .toList();

        return new UserDtoResponse(
                supplier.getId(),
                supplier.getLogin(),
                supplier.getFio(),
                supplier.getEmail(),
                supplier.getPhoneNumber(),
                supplier.getLoginTelegram(),
                supplier.getChatId(),
                supplier.isActive(),
                supplier.getRole(),
                productDtoResponses
        );

    }
}
