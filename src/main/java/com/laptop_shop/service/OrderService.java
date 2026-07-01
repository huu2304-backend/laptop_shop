package com.laptop_shop.service;

import com.laptop_shop.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Order createOrder(String shippingAddress, String phoneNumber, String username);

    // Admin methods
    Page<Order> getAllOrders(Pageable pageable);
    Order getOrderById(Long id);
    void updateOrderStatus(Long id, String status);

    // User methods
    Page<Order> getOrdersByUsername(String username, Pageable pageable);
}
