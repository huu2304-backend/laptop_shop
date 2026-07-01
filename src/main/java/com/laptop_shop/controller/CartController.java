package com.laptop_shop.controller;

import com.laptop_shop.dto.CartItemDTO;
import com.laptop_shop.dto.ProductDTO;
import com.laptop_shop.service.CartService;
import com.laptop_shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final ProductService productService;

    @GetMapping
    public String viewCart(Model model) {
        model.addAttribute("cartItems", cartService.getItems());
        model.addAttribute("totalAmount", cartService.getAmount());
        model.addAttribute("cartCount", cartService.getCount());
        return "cart/cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") Long productId,
                            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
                            jakarta.servlet.http.HttpServletRequest request,
                            RedirectAttributes redirectAttributes) {
        ProductDTO product = productService.findById(productId);
        if (product != null) {
            int existingQty = cartService.getItems().stream()
                    .filter(i -> i.getProductId().equals(productId))
                    .mapToInt(CartItemDTO::getQuantity)
                    .findFirst().orElse(0);
            
            if (existingQty + quantity > product.getQuantity()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Vượt quá số lượng tồn kho! Chỉ còn " + product.getQuantity() + " sản phẩm.");
            } else {
                CartItemDTO item = new CartItemDTO();
                item.setProductId(product.getId());
                item.setProductName(product.getName());
                item.setPrice(product.getPrice());
                item.setQuantity(quantity);
                item.setImageUrl(product.getImageUrl());
                cartService.add(item);
                redirectAttributes.addFlashAttribute("successMessage", "Đã thêm " + product.getName() + " vào giỏ hàng!");
            }
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/cart");
    }

    @PostMapping("/update")
    public String updateCart(@RequestParam("productId") Long productId,
                             @RequestParam("quantity") int quantity) {
        cartService.update(productId, quantity);
        return "redirect:/cart";
    }

    @GetMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable("productId") Long productId) {
        cartService.remove(productId);
        return "redirect:/cart";
    }

    @GetMapping("/clear")
    public String clearCart() {
        cartService.clear();
        return "redirect:/cart";
    }
}
