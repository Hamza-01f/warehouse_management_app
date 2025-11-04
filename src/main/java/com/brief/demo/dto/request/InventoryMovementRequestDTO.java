package com.brief.demo.dto.request;

import com.brief.demo.enums.MovementType;
import lombok.Data;

@Data
public class InventoryMovementRequestDTO {
    private Long inventoryId;
    private MovementType type;
    private Integer quantity;
    private String reason;
}