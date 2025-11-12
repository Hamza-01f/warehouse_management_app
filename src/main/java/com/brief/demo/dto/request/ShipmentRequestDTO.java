package com.brief.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShipmentRequestDTO {

    @NotNull(message = "Sales order ID is required")
    private Long salesOrderId;

    @NotBlank(message = "Tracking number is required")
    private String trackingNumber;
}
