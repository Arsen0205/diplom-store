package com.example.diplom.controller;

import com.example.diplom.models.Client;
import com.example.diplom.models.Supplier;
import com.example.diplom.repository.ClientRepository;
import com.example.diplom.repository.SupplierRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {
    private ClientRepository clientRepository;
    private SupplierRepository supplierRepository;


    //Просмотр профиля пользователя
    @GetMapping("/{id}")
    public ResponseEntity<?> userInfo(@PathVariable("id") Long id){
        Optional<Client> clientOptional = clientRepository.findById(id);
        Map<String, Object> response = new HashMap<>();
        if(clientOptional.isPresent()){
            Client client = clientOptional.get();
            response.put("client", client);
            return ResponseEntity.ok(response);
        }
        Optional<Supplier> supplierOptional = supplierRepository.findById(id);
        if (supplierOptional.isPresent()){
            Supplier supplier = supplierOptional.get();
            response.put("supplier", supplier);
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Пользователя не существует!");
    }
}
