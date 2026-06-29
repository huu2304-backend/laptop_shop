package com.laptop_shop.repository;

import com.laptop_shop.entity.Category;
import com.laptop_shop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByName(String name);
    boolean existsByCategory(Category category);
    @Query("select p from Product p where lower(p.name) like lower(concat('%', :name, '%') ) ")
    Page<Product> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
}

