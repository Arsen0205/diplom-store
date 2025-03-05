package com.example.diplom.service;

import com.example.diplom.dto.request.LoginDtoRequest;
import com.example.diplom.dto.request.RegisterDtoRequest;
import com.example.diplom.dto.response.Response;
import com.example.diplom.models.Client;
import com.example.diplom.models.Supplier;
import com.example.diplom.repository.ClientRepository;
import com.example.diplom.repository.SupplierRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class AuthService {
    private final SupplierRepository supplierRepository;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterDtoRequest request){
        if(supplierRepository.findByLogin(request.getLogin()).isPresent() || clientRepository.findByLogin(request.getLogin()).isPresent()) {
            throw new RuntimeException("Пользователь уже зарегистрирован");
        }
        switch (request.getRole()){
            case SUPPLIER:
                Supplier supplier = new Supplier();
                supplier.setLogin(request.getLogin());
                supplier.setPassword(passwordEncoder.encode(request.getPassword()));
                supplier.setActive(true);
                supplier.setLoginTelegram(request.getLoginTelegram());
                supplier.setChatId(request.getChatId());
                supplier.setRole(request.getRole());
                supplierRepository.save(supplier);
                break;

            case SOLE_TRADER:
                Client client = new Client();
                client.setChatId(request.getChatId());
                client.setLogin(request.getLogin());
                client.setPassword(passwordEncoder.encode(request.getPassword()));
                client.setLoginTelegram(request.getLoginTelegram());
                client.setRole(request.getRole());
                client.setActive(true);
                log.info("ROLE: {}", client.getRole());
                clientRepository.save(client);
                break;

            default:
                throw new IllegalArgumentException("Недопустимая роль: " + request.getRole());
        }
    }


    public void login(LoginDtoRequest request){
        Optional<Supplier> supplierOptional = supplierRepository.findByLogin(request.getLogin());

        if (supplierOptional.isPresent()){
            Supplier supplier = supplierOptional.get();

            if(!passwordEncoder.matches(request.getPassword(), supplier.getPassword())){
                throw new RuntimeException("Неверный пароль!");
            } else if (!supplier.isActive()) {
                throw new RuntimeException("Пользователь заблокирован");
            }

        }

        Optional<Client> clientOptional = clientRepository.findByLogin(request.getLogin());

        if(clientOptional.isPresent()) {
            Client client = clientOptional.get();
            if (!passwordEncoder.matches(request.getPassword(), client.getPassword())) {
                throw new RuntimeException("Неверный пароль!");
            }
            if (!client.isActive()) {
                throw new RuntimeException("Пользователь заблокирован");
            }
        }
    }
}
