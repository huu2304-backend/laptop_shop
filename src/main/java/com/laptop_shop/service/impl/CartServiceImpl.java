package com.laptop_shop.service.impl;

import com.laptop_shop.dto.CartItemDTO;
import com.laptop_shop.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.context.annotation.ScopedProxyMode;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SessionScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@Service
public class CartServiceImpl implements CartService {
    private final Map<Long, CartItemDTO> map = new HashMap<>();

    @Override
    public void add(CartItemDTO item) {
        CartItemDTO existingItem = map.get(item.getProductId());
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
        } else {
            map.put(item.getProductId(), item);
        }
    }

    @Override
    public void remove(Long productId) {
        map.remove(productId);
    }

    @Override
    public void update(Long productId, int quantity) {
        CartItemDTO item = map.get(productId);
        if (item != null) {
            item.setQuantity(quantity);
            if (item.getQuantity() <= 0) {
                map.remove(productId);
            }
        }
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Collection<CartItemDTO> getItems() {
        return map.values();
    }

    @Override
    public int getCount() {
        return map.values().stream().mapToInt(CartItemDTO::getQuantity).sum();
    }

    @Override
    public BigDecimal getAmount() {
        return map.values().stream()
                .map(CartItemDTO::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
