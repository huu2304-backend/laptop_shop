package com.laptop_shop.controller;

import com.laptop_shop.entity.Order;
import com.laptop_shop.service.CartService;
import com.laptop_shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;

    // Chỉ USER mới được đặt hàng (ADMIN không có nghiệp vụ mua hàng)
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/checkout")
    public String showCheckoutForm(Model model) {
        if (cartService.getItems().isEmpty()) {
            return "redirect:/cart";
        }
        model.addAttribute("totalAmount", cartService.getAmount());
        model.addAttribute("cartItems", cartService.getItems());
        return "order/checkout";
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/checkout")
    public String processCheckout(@RequestParam("shippingAddress") String shippingAddress,
                                  @RequestParam("phoneNumber") String phoneNumber,
                                  Authentication authentication,
                                  Model model) {
        try {
            String username = (authentication != null) ? authentication.getName() : null;
            orderService.createOrder(shippingAddress, phoneNumber, username);
            return "redirect:/checkout/success";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("totalAmount", cartService.getAmount());
            model.addAttribute("cartItems", cartService.getItems());
            return "order/checkout";
        }
    }

    @GetMapping("/checkout/success")
    @PreAuthorize("hasRole('USER')")
    public String showSuccessPage() {
        return "order/success";
    }

    // Lịch sử đơn hàng của User đang đăng nhập
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/my-orders")
    public String myOrders(Authentication authentication,
                           @RequestParam(defaultValue = "0") int page,
                           Model model) {
        String username = authentication.getName();
        Pageable pageable = PageRequest.of(page, 10, Sort.by("orderDate").descending());
        Page<Order> orders = orderService.getOrdersByUsername(username, pageable);
        model.addAttribute("orders", orders);
        return "order/my-orders";
    }
}
