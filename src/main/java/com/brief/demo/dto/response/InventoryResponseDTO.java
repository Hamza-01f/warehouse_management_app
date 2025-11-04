package com.brief.demo.dto.response;

import lombok.Data;

@Data
public class InventoryResponseDTO {
    private Long id;
    private WarehouseResponseDTO warehouse;
    private ProductResponseDTO product;
    private Integer quantityOnHand;
    private Integer quantityReserved;
    private Integer availableQuantity;
}