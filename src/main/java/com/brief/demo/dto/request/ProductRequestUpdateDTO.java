package com.brief.demo.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequestUpdateDTO {

    @NotBlank(message = "SKU is required")
    private String sku;

    @NotBlank(message = "Product name is required")
    private String name;

    private String image;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    private BigDecimal price;

    @NotBlank(message = "Unit is required")
    private String unit;

    @NotNull(message = "isActive status is required")
    private Boolean isActive;
}
