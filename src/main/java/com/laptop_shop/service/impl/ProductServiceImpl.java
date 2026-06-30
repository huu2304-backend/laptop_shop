package com.laptop_shop.service.impl;

import com.laptop_shop.dto.ProductDTO;
import com.laptop_shop.entity.Product;
import com.laptop_shop.exception.ResourceNotFoundException;
import com.laptop_shop.mapper.ProductMapper;
import com.laptop_shop.repository.ProductRepository;
import com.laptop_shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> findAll() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductDTO save(ProductDTO productDTO) {
        Product product = productMapper.toEntity(productDTO);
        Product savedProduct = productRepository.save(product);
        return productMapper.toDTO(savedProduct);
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
    @Transactional(readOnly = true)
    public Page<ProductDTO> search(String name, Pageable pageable) {
        if (name == null || name.isBlank()) {
            return productRepository.findAll(pageable)
                    .map(productMapper::toDTO);
        }
        return productRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(productMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> advancedSearch(String name, Long categoryId, String priceRange, Pageable pageable) {
        BigDecimal minPrice = null;
        BigDecimal maxPrice = null;
        
        if (priceRange != null && !priceRange.isBlank()) {
            String[] parts = priceRange.split("-");
            if (parts.length > 0 && !parts[0].isEmpty()) {
                minPrice = new BigDecimal(parts[0]);
            }
            if (parts.length > 1 && !parts[1].isEmpty()) {
                maxPrice = new BigDecimal(parts[1]);
            }
        }
        
        return productRepository.advancedSearch(name, categoryId, minPrice, maxPrice, pageable)
                .map(productMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }
}
