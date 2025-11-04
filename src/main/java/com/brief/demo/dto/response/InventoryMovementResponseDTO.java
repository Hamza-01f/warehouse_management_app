package com.brief.demo.dto.response;

import com.brief.demo.enums.MovementType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InventoryMovementResponseDTO {
    private Long id;
    private InventoryResponseDTO inventory;
    private MovementType type;
    private Integer quantity;
    private String reason;
    private LocalDateTime occurredAt;
}