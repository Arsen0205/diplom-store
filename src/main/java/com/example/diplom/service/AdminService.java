package com.example.diplom.service;


import com.example.diplom.dto.response.ClientsDtoResponse;
import com.example.diplom.dto.response.SuppliersDtoResponse;
import com.example.diplom.models.Client;
import com.example.diplom.models.Product;
import com.example.diplom.models.Supplier;
import com.example.diplom.repository.AdminRepository;
import com.example.diplom.repository.ClientRepository;
import com.example.diplom.repository.ProductRepository;
import com.example.diplom.repository.SupplierRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    private final SupplierRepository supplierRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;

    public List<ClientsDtoResponse> getAllClients(){
        List<Client> clients = clientRepository.findAll();

        return clients.stream()
                .map(client -> new ClientsDtoResponse(
                        client.getId(),
                        client.getLogin(),
                        client.getLoginTelegram(),
                        client.getChatId(),
                        client.isActive(),
                        client.getRole()
                ))
                .toList();
    }

    public List<SuppliersDtoResponse> getAllSuppliers(){
        List<Supplier> suppliers = supplierRepository.findAll();

        return suppliers.stream()
                .map(supplier -> new SuppliersDtoResponse(
                        supplier.getId(),
                        supplier.getLogin(),
                        supplier.getLoginTelegram(),
                        supplier.getChatId(),
                        supplier.isActive(),
                        supplier.getRole()
                ))
                .toList();
    }

    public ResponseEntity<String> banUser(String login){
        Optional<Client> clientOptional =  clientRepository.findByLogin(login);
        Optional<Supplier> supplierOptional = supplierRepository.findByLogin(login);
        if (clientOptional.isPresent()){
            Client client = clientOptional.get();
            client.setActive(false);
            clientRepository.save(client);

            return ResponseEntity.ok("Вы забанили пользователя");
        } else if (supplierOptional.isPresent()) {
            Supplier supplier = supplierOptional.get();
            supplier.setActive(false);
            supplierRepository.save(supplier);

            return ResponseEntity.ok("Вы забанили пользователя");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Ошибка: неизвестный пользователь");
    }

    public ResponseEntity<String> anBanUser(String login){
        Optional<Client> clientOptional =  clientRepository.findByLogin(login);
        Optional<Supplier> supplierOptional = supplierRepository.findByLogin(login);
        if (clientOptional.isPresent()){
            Client client = clientOptional.get();
            client.setActive(true);
            clientRepository.save(client);

            return ResponseEntity.ok("Вы разбанили пользователя");
        } else if (supplierOptional.isPresent()) {
            Supplier supplier = supplierOptional.get();
            supplier.setActive(true);
            supplierRepository.save(supplier);

            return ResponseEntity.ok("Вы разбанили пользователя");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Ошибка: неизвестный пользователь");
    }

    public ResponseEntity<String> deleteProduct(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Такого товара не существует"));

        productRepository.delete(product);

        return ResponseEntity.ok("Вы удалили объявление");
    }
}
