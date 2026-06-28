package com.laptop_shop.controller;

import com.laptop_shop.dto.CategoryDTO;
import com.laptop_shop.exception.CategoryNotEmptyException;
import com.laptop_shop.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public String getAllCategories(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "categories/list";
    }

    @GetMapping("/add")
    public String showAddCategoryForm(Model model) {
        model.addAttribute("category", new CategoryDTO());
        return "categories/add";
    }

    @PostMapping("/add")
    public String addCategory(@Valid @ModelAttribute("category") CategoryDTO categoryDTO,
                              BindingResult result) {
        if (result.hasErrors()) {
            return "categories/add";
        }
        categoryService.save(categoryDTO);
        return "redirect:/categories";
    }

    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id,
                                 RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa hãng sản xuất thành công.");
        } catch (CategoryNotEmptyException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi không xác định khi xóa hãng.");
        }
        return "redirect:/categories";
    }

    @GetMapping("/edit/{id}")
    public String showEditCategoryForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("category", categoryService.findById(id));
        return "categories/edit";
    }

    @PostMapping("/edit/{id}")
    public String editCategory(@PathVariable("id") Long id,
                               @Valid @ModelAttribute("category") CategoryDTO categoryDTO,
                               BindingResult result) {
        if (result.hasErrors()) {
            return "categories/edit";
        }
        categoryDTO.setId(id);
        categoryService.save(categoryDTO);
        return "redirect:/categories";
    }
}
