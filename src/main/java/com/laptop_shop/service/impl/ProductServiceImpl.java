package com.laptop_shop.service.impl;

import com.laptop_shop.dto.ProductDTO;
import com.laptop_shop.entity.Category;
import com.laptop_shop.entity.Product;
import com.laptop_shop.exception.ResourceNotFoundException;
import com.laptop_shop.repository.CategoryRepository;
import com.laptop_shop.repository.ProductRepository;
import com.laptop_shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public List<ProductDTO> findAll() {
        // Dùng Stream API thay vòng lặp for thủ công — ngắn gọn và dễ đọc hơn
        return productRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductDTO save(ProductDTO productDTO) {
        Product product = this.convertToEntity(productDTO);
        Product savedProduct = productRepository.save(product);
        return this.convertToDTO(savedProduct);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public Page<ProductDTO> search(String name, Pageable pageable) {
        if (name == null|| name.isBlank()) {
            return productRepository.findAll( pageable)
                    .map(this::convertToDTO);
        }
        return productRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(this::convertToDTO);
    }

    @Override
    public ProductDTO findById(Long id) {
        return productRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    // ---------- Helper methods ----------

    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        dto.setDescription(product.getDescription());
        dto.setImageUrl(product.getImageUrl());

        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }

        return dto;
    }

    // con co the dung mapstruct de chuyen doi giua entity va dto
    private Product convertToEntity(ProductDTO dto) {
        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setDescription(dto.getDescription());
        product.setImageUrl(dto.getImageUrl());

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));
            product.setCategory(category);
        }

        return product;
    }
}
