package com.brief.demo.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class PurchaseOrderRequestDTO {
    private Long supplierId;
    private List<POLineRequestDTO> orderLines;

    @Data
    public static class POLineRequestDTO {
        private Long productId;
        private Integer quantity;
        private java.math.BigDecimal price;
    }
}