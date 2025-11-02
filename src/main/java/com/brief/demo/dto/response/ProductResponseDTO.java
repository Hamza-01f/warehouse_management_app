package com.brief.demo.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResponseDTO {
    private Long id;
    private String sku;
    private String name;
    private String image;
    private BigDecimal price;
    private String unit;
    private Boolean isActive;
}
