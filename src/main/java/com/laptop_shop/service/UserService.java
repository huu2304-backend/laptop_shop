package com.laptop_shop.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.laptop_shop.entity.User;

public interface UserService extends UserDetailsService {
    void register(String username, String email, String password);
}
