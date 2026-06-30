package com.laptop_shop.repository;

import com.laptop_shop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
   Optional<User>findByUsername(String username);
}
