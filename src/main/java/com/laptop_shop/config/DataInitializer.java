package com.laptop_shop.config;

import com.laptop_shop.entity.User;
import com.laptop_shop.enums.Role;
import com.laptop_shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole(Role.ADMIN);
            admin.setEmail("admin@example.com");
            userRepository.save(admin);
            System.out.println("====== Admin account created: admin / admin ======");
        }
    }
}
