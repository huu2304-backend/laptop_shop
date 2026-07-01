package com.laptop_shop.service.impl;

import com.laptop_shop.dto.CartItemDTO;
import com.laptop_shop.entity.Order;
import com.laptop_shop.entity.OrderDetail;
import com.laptop_shop.entity.Product;
import com.laptop_shop.entity.User;
import com.laptop_shop.exception.ResourceNotFoundException;
import com.laptop_shop.repository.OrderRepository;
import com.laptop_shop.repository.ProductRepository;
import com.laptop_shop.repository.UserRepository;
import com.laptop_shop.service.CartService;
import com.laptop_shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartService cartService;

    @Override
    @Transactional
    public Order createOrder(String shippingAddress, String phoneNumber, String username) {
        Collection<CartItemDTO> cartItems = cartService.getItems();
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng đang trống!");
        }

        User user = null;
        if (username != null) {
            user = userRepository.findByUsername(username).orElse(null);
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(cartService.getAmount());
        order.setStatus("PENDING");
        order.setShippingAddress(shippingAddress);
        order.setPhoneNumber(phoneNumber);

        for (CartItemDTO item : cartItems) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại: " + item.getProductId()));

            if (product.getQuantity() < item.getQuantity()) {
                throw new RuntimeException("Số lượng sản phẩm " + product.getName() + " không đủ. Tồn kho: " + product.getQuantity());
            }

            // Decrease quantity
            product.setQuantity(product.getQuantity() - item.getQuantity());
            productRepository.save(product);

            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(product);
            detail.setPrice(item.getPrice());
            detail.setQuantity(item.getQuantity());

            order.getOrderDetails().add(detail);
        }

        Order savedOrder = orderRepository.save(order);
        cartService.clear();

        return savedOrder;
    }

    @Override
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<Order> getAllOrders(org.springframework.data.domain.Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại: " + id));
    }

    @Override
    @Transactional
    public void updateOrderStatus(Long id, String status) {
        Order order = getOrderById(id);

        // If changing to CANCELLED and it wasn't CANCELLED before, restore inventory
        if ("CANCELLED".equals(status) && !"CANCELLED".equals(order.getStatus())) {
            for (OrderDetail detail : order.getOrderDetails()) {
                Product product = detail.getProduct();
                product.setQuantity(product.getQuantity() + detail.getQuantity());
                productRepository.save(product);
            }
        }

        order.setStatus(status);
        orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> getOrdersByUsername(String username, Pageable pageable) {
        return orderRepository.findByUserUsernameOrderByOrderDateDesc(username, pageable);
    }
}
