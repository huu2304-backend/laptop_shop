package com.laptop_shop.repository;

import com.laptop_shop.entity.Category;
import com.laptop_shop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByName(String name);
    boolean existsByCategory(Category category);
}

