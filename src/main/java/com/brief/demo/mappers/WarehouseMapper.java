package com.brief.demo.mappers;

import com.brief.demo.dto.request.WarehouseRequestDTO;
import com.brief.demo.dto.response.WarehouseResponseDTO;
import com.brief.demo.model.Warehouse;
import org.springframework.stereotype.Component;

@Component
public class WarehouseMapper {

    public Warehouse toEntity(WarehouseRequestDTO request) {
        return Warehouse.builder()
                .name(request.getName())
                .address(request.getAddress())
                .build();
    }

    public WarehouseResponseDTO toResponse(Warehouse warehouse) {
        WarehouseResponseDTO response = new WarehouseResponseDTO();
        response.setId(warehouse.getId());
        response.setName(warehouse.getName());
        response.setAddress(warehouse.getAddress());
        response.setIsActive(warehouse.getIsActive());
        return response;
    }
}