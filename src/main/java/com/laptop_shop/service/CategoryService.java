package com.laptop_shop.service;

import com.laptop_shop.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {
    List<CategoryDTO> findAll();
    CategoryDTO save(CategoryDTO categoryDTO);
    void deleteById(Long id);
    CategoryDTO findById(Long id);
}
