package com.brief.demo.dto.request;

import lombok.Data;

@Data
public class InventoryRequestDTO {
    private Long warehouseId;
    private Long productId;
    private Integer quantityOnHand;
    private Integer quantityReserved;
}