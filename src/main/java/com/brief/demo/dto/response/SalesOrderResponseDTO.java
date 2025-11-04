package com.brief.demo.dto.response;

import com.brief.demo.enums.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SalesOrderResponseDTO {
    private Long id;
    private AuthResponseDTO client;
    private WarehouseResponseDTO warehouse;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private String country;
    private String city;
    private String street;
    private String zipCode;
    private List<SalesOrderLineResponseDTO> orderLines;
}
