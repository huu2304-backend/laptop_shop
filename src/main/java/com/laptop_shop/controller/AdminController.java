package com.laptop_shop.controller;

import com.laptop_shop.entity.Order;
import com.laptop_shop.service.CategoryService;
import com.laptop_shop.service.OrderService;
import com.laptop_shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final OrderService orderService;

    @GetMapping
    public String adminRedirect() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Thống kê tổng quan
        long totalProducts = productService.findAll().size();
        long totalCategories = categoryService.findAll().size();

        Pageable allOrders = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("id").descending());
        Page<Order> ordersPage = orderService.getAllOrders(allOrders);
        long totalOrders = ordersPage.getTotalElements();

        long pendingOrders = ordersPage.getContent().stream()
                .filter(o -> "PENDING".equals(o.getStatus())).count();

        BigDecimal totalRevenue = ordersPage.getContent().stream()
                .filter(o -> !"CANCELLED".equals(o.getStatus()))
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 10 đơn hàng mới nhất
        Pageable recentPageable = PageRequest.of(0, 10, Sort.by("orderDate").descending());
        Page<Order> recentOrders = orderService.getAllOrders(recentPageable);

        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalCategories", totalCategories);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("recentOrders", recentOrders.getContent());

        return "admin/dashboard";
    }
}
