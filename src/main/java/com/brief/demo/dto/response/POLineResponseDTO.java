package com.brief.demo.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class POLineResponseDTO {
    private Long id;
    private ProductResponseDTO product;
    private Integer quantity;
    private BigDecimal price;
}