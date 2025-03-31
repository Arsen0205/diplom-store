package com.example.diplom.service;

import com.example.diplom.dto.response.UserInfoDtoResponse;
import com.example.diplom.models.Client;
import com.example.diplom.models.Supplier;
import com.example.diplom.repository.ClientRepository;
import com.example.diplom.repository.SupplierRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final ClientRepository clientRepository;
    private final SupplierRepository supplierRepository;

    public UserInfoDtoResponse userInfo(Long id){
        Optional<Client> clientOptional = clientRepository.findById(id);
        Optional<Supplier> supplierOptional = supplierRepository.findById(id);

        if (clientOptional.isPresent()){
            Client client = clientOptional.get();

            return new UserInfoDtoResponse(
                    client.getId(),
                    client.getLogin(),
                    client.getLoginTelegram(),
                    client.getChatId()
            );
        } else if (supplierOptional.isPresent()) {
            Supplier supplier = supplierOptional.get();

            return new UserInfoDtoResponse(
                    supplier.getId(),
                    supplier.getLogin(),
                    supplier.getLoginTelegram(),
                    supplier.getChatId()
            );
        }

        throw  new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
    }
}
