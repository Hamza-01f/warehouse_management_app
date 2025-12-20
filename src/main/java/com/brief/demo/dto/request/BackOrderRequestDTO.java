package com.brief.demo.dto.request;

import lombok.Data;

@Data
public class BackOrderRequestDTO {
    private Long salesOrderLineId;
    private Long productId;
    private Integer quantity;
}