package com.laptop_shop.service;

import com.laptop_shop.dto.CartItemDTO;

import java.math.BigDecimal;
import java.util.Collection;

public interface CartService {
    void add(CartItemDTO item);
    void remove(Long productId);
    void update(Long productId, int quantity);
    void clear();
    Collection<CartItemDTO> getItems();
    int getCount();
    BigDecimal getAmount();
}
