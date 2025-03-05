package com.example.diplom.service;


import com.example.diplom.repository.ClientRepository;
import com.example.diplom.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final SupplierRepository supplierRepository;
    private final ClientRepository clientRepository;


    @Override
    public UserDetails loadUserByUsername(String userLogin) throws UsernameNotFoundException {
        return supplierRepository.findByLogin(userLogin)
                .map(supplier -> (UserDetails) supplier)
                .orElseGet(() -> clientRepository.findByLogin(userLogin)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userLogin)));
    }
}