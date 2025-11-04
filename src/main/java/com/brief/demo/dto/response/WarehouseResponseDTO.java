package com.brief.demo.dto.response;

import lombok.Data;

@Data
public class WarehouseResponseDTO {
    private Long id;
    private String name;
    private String address;
    private Boolean isActive;
}