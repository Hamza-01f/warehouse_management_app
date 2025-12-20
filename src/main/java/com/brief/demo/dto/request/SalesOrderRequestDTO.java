package com.brief.demo.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class    SalesOrderRequestDTO {
    @NotNull(message = "Warehouse ID is required")
    private Long warehouseId;

    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country cannot exceed 100 characters")
    private String country;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City cannot exceed 100 characters")
    private String city;

    @NotBlank(message = "Street is required")
    @Size(max = 200, message = "Street cannot exceed 200 characters")
    private String street;

    @NotBlank(message = "ZIP code is required")
    @Size(max = 20, message = "ZIP code cannot exceed 20 characters")
    private String zipCode;

    @Valid
    @NotNull(message = "Order lines are required")
    @Size(min = 1, message = "At least one order line is required")
    private List<SalesOrderLineRequestDTO> orderLines;

    @Data
    public static class SalesOrderLineRequestDTO {

        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;
    }
}
