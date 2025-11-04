package com.brief.demo.dto.response;

import lombok.Data;

@Data
public class SupplierResponseDTO {
    private Long id;
    private String name;
    private String contact;
    private Boolean isActive;
}