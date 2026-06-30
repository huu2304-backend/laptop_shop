package com.laptop_shop.mapper;

import com.laptop_shop.dto.ProductDTO;
import com.laptop_shop.entity.Category;
import com.laptop_shop.entity.Product;
import com.laptop_shop.exception.ResourceNotFoundException;
import com.laptop_shop.repository.CategoryRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ProductMapper {

    @Autowired
    protected CategoryRepository categoryRepository;

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    public abstract ProductDTO toDTO(Product product);

    @Mapping(target = "category", ignore = true)
    public abstract Product toEntity(ProductDTO dto);

    @AfterMapping
    protected void linkCategory(ProductDTO dto, @MappingTarget Product product) {
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));
            product.setCategory(category);
        }
    }
}
