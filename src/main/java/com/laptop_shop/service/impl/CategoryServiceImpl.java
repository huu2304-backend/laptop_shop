package com.laptop_shop.service.impl;

import com.laptop_shop.dto.CategoryDTO;
import com.laptop_shop.entity.Category;
import com.laptop_shop.exception.CategoryNotEmptyException;
import com.laptop_shop.exception.ResourceNotFoundException;
import com.laptop_shop.repository.CategoryRepository;
import com.laptop_shop.repository.ProductRepository;
import com.laptop_shop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public List<CategoryDTO> findAll() {
        return categoryRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryDTO save(CategoryDTO categoryDTO) {
        Category category = convertToEntity(categoryDTO);
        Category saved = categoryRepository.save(category);
        return convertToDTO(saved);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        // findById sẽ tự ném ResourceNotFoundException nếu không tìm thấy
        Category category = findEntityById(id);
        if (productRepository.existsByCategory(category)) {
            throw new CategoryNotEmptyException("Không thể xóa hãng sản xuất này vì vẫn còn sản phẩm thuộc hãng này.");
        }
        categoryRepository.delete(category);
    }

    @Override
    public CategoryDTO findById(Long id) {
        return convertToDTO(findEntityById(id));
    }

    // ---------- Helper methods ----------

    private Category findEntityById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }

    private Category convertToEntity(CategoryDTO dto) {
        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        return category;
    }
}
