package com.brief.demo.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequestUpdateDTO {
    private String sku;
    private String name;
    private String image;
    private BigDecimal price;
    private String unit;
    private Boolean isActive;
}
