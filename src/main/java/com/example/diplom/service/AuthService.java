package com.example.diplom.service;

import com.example.diplom.component.JwtUtil;
import com.example.diplom.dto.request.LoginDtoRequest;
import com.example.diplom.dto.request.RegisterAdminDtoRequest;
import com.example.diplom.dto.request.RegisterClientDtoRequest;
import com.example.diplom.dto.request.RegisterSupplierDtoRequest;
import com.example.diplom.dto.response.AdminInfoDtoResponse;
import com.example.diplom.dto.response.LoginResponse;
import com.example.diplom.dto.response.UserInfoDtoResponse;
import com.example.diplom.models.Admin;
import com.example.diplom.models.Client;
import com.example.diplom.models.Supplier;
import com.example.diplom.repository.AdminRepository;
import com.example.diplom.repository.ClientRepository;
import com.example.diplom.repository.SupplierRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService; // или UserService
    private final SupplierRepository supplierRepo;
    private final ClientRepository clientRepo;
    private final AdminRepository adminRepo;

    public UserInfoDtoResponse registerSupplier(RegisterSupplierDtoRequest request) {
        if (supplierRepository.findByLogin(request.getLogin()).isPresent()
                || clientRepository.findByLogin(request.getLogin()).isPresent() || adminRepository.findByLogin(request.getLogin()).isPresent()) {
            throw new RuntimeException("Пользователь уже зарегистрирован");
        }

        Supplier supplier = new Supplier();
        supplier.setLogin(request.getLogin());
        supplier.setPassword(passwordEncoder.encode(request.getPassword()));
        supplier.setActive(true);
        supplier.setLoginTelegram(request.getLoginTelegram());
        supplier.setChatId(request.getChatId());
        supplier.setRole(request.getRole());
        supplier.setEmail(request.getEmail());
        supplier.setFio(request.getFio());
        supplier.setPhoneNumber(request.getPhoneNumber());

        Supplier s = supplierRepository.save(supplier);

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

    public UserInfoDtoResponse registerClient(RegisterClientDtoRequest request) {
        if (supplierRepository.findByLogin(request.getLogin()).isPresent()
                || clientRepository.findByLogin(request.getLogin()).isPresent() || adminRepository.findByLogin(request.getLogin()).isPresent()) {
            throw new RuntimeException("Пользователь c таким логином уже зарегистрирован");
        }

        Client client = new Client();
        client.setLogin(request.getLogin());
        client.setFio(request.getFio());
        client.setInn(request.getInn());
        client.setEmail(request.getEmail());
        client.setLoginTelegram(request.getLoginTelegram());
        client.setChatId(request.getChatId());
        client.setActive(true);
        client.setOgrnip(request.getOgrnip());
        client.setPhoneNumber(request.getPhoneNumber());
        client.setPassword(passwordEncoder.encode(request.getPassword()));
        client.setRole(request.getRole());

        Client s = clientRepository.save(client);

        return new UserInfoDtoResponse(
                s.getId(),
                s.getLogin(),
                s.getLoginTelegram(),
                s.getInn(),
                s.getChatId(),
                s.getOgrnip(),
                s.getEmail(),
                s.getPhoneNumber(),
                s.getFio(),
                s.getPassword(),
                s.getRole()
        );

    }

    public AdminInfoDtoResponse registerAdmin(RegisterAdminDtoRequest request){
        if (supplierRepository.findByLogin(request.getLogin()).isPresent()
                || clientRepository.findByLogin(request.getLogin()).isPresent() || adminRepository.findByLogin(request.getLogin()).isPresent()) {
            throw new RuntimeException("Пользователь c таким логином уже зарегистрирован");
        }

        Admin admin = new Admin();
        admin.setRole(request.getRole());
        admin.setLogin(request.getLogin());
        admin.setActive(true);
        admin.setPassword(passwordEncoder.encode(request.getPassword()));

        Admin saved = adminRepository.save(admin);

        return new AdminInfoDtoResponse(
                saved.getId(),
                saved.getLogin(),
                saved.getPassword(),
                saved.getRole()
                );

    }

//    public LoginResponse login(String login, String password) {
//        var auth = authManager.authenticate(
//                new UsernamePasswordAuthenticationToken(login, password)
//        );
//        UserDetails ud = (UserDetails) auth.getPrincipal();
//        String token = jwtUtil.generateToken(ud);
//
//        // Предположим, что userLogin уникален во всех трёх репах
//        User userEntity =
//                supplierRepo.findByLogin(login)
//                        .or(() -> clientRepo.findByLogin(login))
//                        .or(() -> adminRepo.findByLogin(login))
//                        .orElseThrow();
//
//        // Маппим в DTO
//        UserInfoDtoResponse userInfo = new UserInfoDtoResponse(
//                userEntity.getId(),
//                userEntity.getLogin(),
//                userEntity.getEmail(),
//                userEntity.getRole()
//        );
//
//        return new LoginResponse(token, userInfo);
//    }
}
