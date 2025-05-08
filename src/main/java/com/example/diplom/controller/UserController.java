package com.example.diplom.controller;

import com.example.diplom.dto.response.UserInfoDtoResponse;
import com.example.diplom.repository.ClientRepository;
import com.example.diplom.repository.SupplierRepository;
import com.example.diplom.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {
    private ClientRepository clientRepository;
    private SupplierRepository supplierRepository;
    private UserService  userService;

    //Просмотр профиля пользователя
    @GetMapping("/{id}")
    public UserInfoDtoResponse userInfo(@PathVariable("id") Long id){
        return userService.userInfo(id);
    }
}