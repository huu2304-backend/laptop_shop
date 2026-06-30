package com.laptop_shop.controller;

import com.laptop_shop.dto.CategoryDTO;
import com.laptop_shop.dto.ProductDTO;
import com.laptop_shop.service.CategoryService;
import com.laptop_shop.service.FileStorageService;
import com.laptop_shop.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final FileStorageService fileStorageService;

    @ModelAttribute("categories")
    public List<CategoryDTO> populateCategories() {
        return categoryService.findAll();
    }

    @GetMapping
    public String listProducts(Model model, @RequestParam(defaultValue = "") String name,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        model.addAttribute("products", productService.search(name, pageable));
        return "products/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new ProductDTO());
        return "products/add";
    }

    @PostMapping("/add")
    public String addProduct(@Valid @ModelAttribute("product") ProductDTO productDTO,
                             BindingResult result,
                             @RequestParam("imageFile") MultipartFile imageFile) {

        if (imageFile.isEmpty()) {
            result.rejectValue("imageUrl", "NotBlank", "Vui lòng chọn file ảnh cho sản phẩm");
        }

        if (result.hasErrors()) {
            return "products/add";
        }

        String imageUrl = fileStorageService.storeFile(imageFile);
        productDTO.setImageUrl(imageUrl);
        productService.save(productDTO);
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        ProductDTO productDTO = productService.findById(id);
        model.addAttribute("product", productDTO);
        return "products/edit";
    }

    @PostMapping("/edit/{id}")
    public String editProduct(@PathVariable("id") Long id,
                              @Valid @ModelAttribute("product") ProductDTO productDTO,
                              BindingResult result,
                              @RequestParam("imageFile") MultipartFile imageFile) {
        if (result.hasErrors()) {
            return "products/edit";
        }

        if (!imageFile.isEmpty()) {
            String imageUrl = fileStorageService.storeFile(imageFile);
            productDTO.setImageUrl(imageUrl);
        } else {
            ProductDTO existingProduct = productService.findById(id);
            productDTO.setImageUrl(existingProduct.getImageUrl());
        }

        productDTO.setId(id);
        productService.save(productDTO);
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id) {
        productService.deleteById(id);
        return "redirect:/products";
    }
}
