package com.example.diplom.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.example.diplom.dto.response.UserInfoDtoResponse;
import com.example.diplom.models.Admin;
import com.example.diplom.models.Client;
import com.example.diplom.models.Supplier;
import com.example.diplom.repository.AdminRepository;
import com.example.diplom.repository.ClientRepository;
import com.example.diplom.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInfoService {
    private final SupplierRepository supplierRepo;
    private final ClientRepository clientRepo;
    private final AdminRepository adminRepo;

    public UserInfoDtoResponse getByLogin(String login) {
        // 1) Пытаемся найти в поставщиках
        var supOpt = supplierRepo.findByLogin(login);
        if (supOpt.isPresent()) {
            Supplier s = supOpt.get();
            return new UserInfoDtoResponse(
                    s.getId(),
                    s.getLogin(),
                    s.getLoginTelegram(),
                    null,
                    s.getChatId(),
                    null,
                    s.getEmail(),
                    s.getPhoneNumber(),
                    s.getFio(),
                    s.getPassword(),
                    s.getRole()
            );
        }
        // 2) Пытаемся найти в клиентах
        var cliOpt = clientRepo.findByLogin(login);
        if (cliOpt.isPresent()) {
            Client c = cliOpt.get();
            return new UserInfoDtoResponse(
                    c.getId(),
                    c.getLogin(),
                    c.getLoginTelegram(),
                    c.getInn(),
                    c.getChatId(),
                    c.getOgrnip(),
                    c.getEmail(),
                    c.getPhoneNumber(),
                    c.getFio(),
                    c.getPassword(),
                    c.getRole()
            );
        }
        // 3) Наконец, админ
        var admOpt = adminRepo.findByLogin(login);
        if (admOpt.isPresent()) {
            Admin a = admOpt.get();
            return new UserInfoDtoResponse(
                    a.getId(),
                    a.getLogin(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    a.getPassword(),
                    a.getRole()
            );
        }
        throw new UsernameNotFoundException("User not found: " + login);
    }
}


