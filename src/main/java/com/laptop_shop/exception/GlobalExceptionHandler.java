package com.laptop_shop.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFoundException(ResourceNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/404";
    }

    // CategoryNotEmptyException được xử lý trực tiếp trong CategoryController với RedirectAttributes.
    // Không xử lý ở đây để tránh nuốt lỗi và hiển thị trang 500 sai.

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, Model model) {
        model.addAttribute("errorMessage", "Đã có lỗi xảy ra trong quá trình xử lý yêu cầu của bạn.");
        ex.printStackTrace();
        return "error/500";
    }
}
