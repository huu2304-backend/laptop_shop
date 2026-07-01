package com.laptop_shop.controller;

import com.laptop_shop.dto.CategoryDTO;
import com.laptop_shop.exception.CategoryNotEmptyException;
import com.laptop_shop.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categories")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public String getAllCategories(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "admin/categories/list";
    }

    @GetMapping("/add")
    public String showAddCategoryForm(Model model) {
        model.addAttribute("category", new CategoryDTO());
        return "admin/categories/add";
    }

    @PostMapping("/add")
    public String addCategory(@Valid @ModelAttribute("category") CategoryDTO categoryDTO,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/categories/add";
        }
        categoryService.save(categoryDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Thêm danh mục thành công!");
        return "redirect:/admin/categories";
    }

    @GetMapping("/edit/{id}")
    public String showEditCategoryForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("category", categoryService.findById(id));
        return "admin/categories/edit";
    }

    @PostMapping("/edit/{id}")
    public String editCategory(@PathVariable("id") Long id,
                               @Valid @ModelAttribute("category") CategoryDTO categoryDTO,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/categories/edit";
        }
        categoryDTO.setId(id);
        categoryService.save(categoryDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật danh mục thành công!");
        return "redirect:/admin/categories";
    }

    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id,
                                 RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa danh mục thành công.");
        } catch (CategoryNotEmptyException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi không xác định khi xóa danh mục.");
        }
        return "redirect:/admin/categories";
    }
}
