package com.brief.demo.dto.response;

import com.brief.demo.enums.ShipmentStatus;
import lombok.Data;

@Data
public class ShipmentResponseDTO {
    private Long id;
    private SalesOrderResponseDTO salesOrder;
    private String carrier;
    private String trackingNumber;
    private ShipmentStatus status;
}