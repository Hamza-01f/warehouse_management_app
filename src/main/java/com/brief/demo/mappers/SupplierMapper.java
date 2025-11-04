package com.brief.demo.mappers;

import com.brief.demo.dto.request.SupplierRequestDTO;
import com.brief.demo.dto.response.SupplierResponseDTO;
import com.brief.demo.model.Supplier;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {

    public Supplier toEntity(SupplierRequestDTO request) {
        return Supplier.builder()
                .name(request.getName())
                .contact(request.getContact())
                .build();
    }

    public SupplierResponseDTO toResponse(Supplier supplier) {
        SupplierResponseDTO response = new SupplierResponseDTO();
        response.setId(supplier.getId());
        response.setName(supplier.getName());
        response.setContact(supplier.getContact());
        response.setIsActive(supplier.getIsActive());
        return response;
    }
}