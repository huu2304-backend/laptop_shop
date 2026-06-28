package com.laptop_shop.service;

import com.laptop_shop.dto.ProductDTO;
import java.util.List;

public interface ProductService {
    List<ProductDTO> findAll();

    ProductDTO findById(Long id);

    ProductDTO save(ProductDTO productDTO);

    void deleteById(Long id);
}
