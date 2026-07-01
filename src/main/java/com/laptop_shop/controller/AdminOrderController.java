package com.laptop_shop.controller;

import com.laptop_shop.entity.Order;
import com.laptop_shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public String listOrders(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "id") String sortField,
                             @RequestParam(defaultValue = "desc") String sortDir,
                             Model model) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, 10, sort);
        
        Page<Order> orders = orderService.getAllOrders(pageable);
        
        model.addAttribute("orders", orders);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        
        return "admin/orders/list";
    }

    @GetMapping("/{id}")
    public String orderDetails(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderById(id);
        model.addAttribute("order", order);
        return "admin/orders/detail";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, 
                               @RequestParam String status, 
                               RedirectAttributes redirectAttributes) {
        try {
            orderService.updateOrderStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái đơn hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi cập nhật: " + e.getMessage());
        }
        return "redirect:/admin/orders/" + id;
    }
}
