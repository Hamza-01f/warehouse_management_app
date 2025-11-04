package com.brief.demo.dto.response;

import lombok.Data;

@Data
public class SalesOrderLineResponseDTO {
    private Long id;
    private ProductResponseDTO product;
    private Integer quantity;
    private Integer quantityReserved;
    private Integer quantityOrdered;
}
