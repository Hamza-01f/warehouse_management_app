package com.brief.demo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventoryUpdateDTO {

    @NotNull(message = "Quantity on hand is required")
    @Min(value = 0, message = "Quantity on hand cannot be negative")
    private Integer quantityOnHand;

    @NotNull(message = "Quantity reserved is required")
    @Min(value = 0, message = "Quantity reserved cannot be negative")
    private Integer quantityReserved;
}
