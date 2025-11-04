package com.brief.demo.dto.request;

import lombok.Data;

@Data
public class InventoryUpdateDTO {
    private Integer quantityOnHand;
    private Integer quantityReserved;
}