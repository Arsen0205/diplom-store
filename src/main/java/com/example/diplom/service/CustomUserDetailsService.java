package com.example.diplom.service;


import com.example.diplom.repository.AdminRepository;
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
    private final AdminRepository adminRepository;


    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return supplierRepository.findByLogin(login).map(s -> (UserDetails) s)
                .or(() -> clientRepository.findByLogin(login).map(c -> (UserDetails) c))
                .or(() -> adminRepository.findByLogin(login).map(a -> (UserDetails) a))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + login));
    }
}