package com.brief.demo.dto.response;

import com.brief.demo.enums.BackOrderStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BackOrderResponseDTO {
    private Long id;
    private SalesOrderLineResponseDTO salesOrderLine;
    private ProductResponseDTO product;
    private Integer quantity;
    private LocalDateTime createdAt;
    private LocalDateTime fulfilledAt;
    private BackOrderStatus status;
}