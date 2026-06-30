package com.laptop_shop.controller;

import com.laptop_shop.dto.RegisterDTO;
import com.laptop_shop.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String showLoginForm(){
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model){
        model.addAttribute("registerDTO", new RegisterDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerDTO") RegisterDTO registerDTO,
                           BindingResult result,
                           Model model){
        
        if (result.hasErrors()) {
            return "auth/register";
        }
        
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.registerDTO", "Mật khẩu xác nhận không khớp!");
            return "auth/register";
        }
        
        try {
            userService.register(registerDTO.getUsername(), registerDTO.getEmail(), registerDTO.getPassword());
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
        
        return "redirect:/login";
    }
}
