package com.laptop_shop.repository;

import com.laptop_shop.entity.Category;
import com.laptop_shop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByName(String name);

    List<Product> findTop8ByOrderByIdDesc();

    boolean existsByCategory(Category category);

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE " +
           "(:name IS NULL OR :name = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> advancedSearch(
            @Param("name") String name, 
            @Param("categoryId") Long categoryId, 
            @Param("minPrice") BigDecimal minPrice, 
            @Param("maxPrice") BigDecimal maxPrice, 
            Pageable pageable);
}
