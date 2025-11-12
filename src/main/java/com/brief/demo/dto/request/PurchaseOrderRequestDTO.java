package com.brief.demo.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PurchaseOrderRequestDTO {

    @NotNull(message = "Supplier ID is required")
    private Long supplierId;

    @NotEmpty(message = "Order must contain at least one line item")
    @Valid
    private List<POLineRequestDTO> orderLines;

    @Data
    public static class POLineRequestDTO {

        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "Quantity is required")
        @jakarta.validation.constraints.Positive(message = "Quantity must be greater than zero")
        private Integer quantity;

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
        private BigDecimal price;
    }
}
