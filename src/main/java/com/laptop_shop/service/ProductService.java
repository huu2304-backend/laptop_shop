package com.laptop_shop.service;

import com.laptop_shop.dto.ProductDTO;
import com.laptop_shop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    List<ProductDTO> findAll();

    ProductDTO findById(Long id);

    ProductDTO save(ProductDTO productDTO);

    void deleteById(Long id);
    Page<ProductDTO> search(String name, Pageable pageable);
}
