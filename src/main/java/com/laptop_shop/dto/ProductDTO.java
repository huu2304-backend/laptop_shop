package com.laptop_shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    @NotNull(message = "Giá sản phẩm không được để trống")
    @Min(value = 0, message = "Giá sản phẩm phải lớn hơn hoặc bằng 0")
    private BigDecimal price;

    @Min(value = 0, message = "Số lượng sản phẩm phải lớn hơn hoặc bằng 0")
    private int quantity;

    @NotNull(message = "Vui lòng chọn danh mục cho sản phẩm")
    private Long categoryId;

    @NotBlank(message = "Mô tả sản phẩm không được để trống")
    private String description;

    private String imageUrl;

    private String categoryName;
}
