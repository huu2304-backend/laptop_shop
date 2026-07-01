package com.laptop_shop.repository;

import com.laptop_shop.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);

    Page<Order> findByUserUsernameOrderByOrderDateDesc(String username, Pageable pageable);
}
