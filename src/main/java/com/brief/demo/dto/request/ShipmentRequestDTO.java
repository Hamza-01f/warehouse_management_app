package com.brief.demo.dto.request;

import lombok.Data;

@Data
public class ShipmentRequestDTO {
    private Long salesOrderId;
    private String carrier;
    private String trackingNumber;
}