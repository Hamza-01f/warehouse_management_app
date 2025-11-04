package com.brief.demo.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class SalesOrderRequestDTO {
    private Long warehouseId;
    private String country;
    private String city;
    private String street;
    private String zipCode;
    private List<SalesOrderLineRequestDTO> orderLines;

    @Data
    public  static class SalesOrderLineRequestDTO {
        private Long productId;
        private Integer quantity;
    }
}

