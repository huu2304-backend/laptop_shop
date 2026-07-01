package com.laptop_shop.controller;

import com.laptop_shop.service.CategoryService;
import com.laptop_shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("latestProducts", productService.getTop8LatestProducts());
        return "index";
    }

    @GetMapping("/shop")
    public String shop(Model model,
                       @RequestParam(defaultValue = "") String name,
                       @RequestParam(required = false) Long categoryId,
                       @RequestParam(required = false) String priceRange,
                       @RequestParam(defaultValue = "id") String sortField,
                       @RequestParam(defaultValue = "desc") String sortDir,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "12") int size) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        model.addAttribute("products", productService.advancedSearch(name, categoryId, priceRange, pageable));
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("name", name);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("priceRange", priceRange);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);

        return "shop";
    }

    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable("id") Long id, Model model) {
        model.addAttribute("product", productService.findById(id));
        return "product-detail";
    }
}
