package com.brief.demo.mappers;

import com.brief.demo.dto.request.InventoryRequestDTO;
import com.brief.demo.dto.request.InventoryUpdateDTO;
import com.brief.demo.dto.response.InventoryResponseDTO;
import com.brief.demo.model.Inventory;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public Inventory toEntity(InventoryRequestDTO request) {
        return Inventory.builder()
                .quantityOnHand(request.getQuantityOnHand() != null ? request.getQuantityOnHand() : 0)
                .quantityReserved(request.getQuantityReserved() != null ? request.getQuantityReserved() : 0)
                .build();
    }

    public InventoryResponseDTO toResponse(Inventory inventory) {
        InventoryResponseDTO response = new InventoryResponseDTO();
        response.setId(inventory.getId());
        response.setQuantityOnHand(inventory.getQuantityOnHand());
        response.setQuantityReserved(inventory.getQuantityReserved());
        response.setAvailableQuantity(inventory.getAvailable_quantity());
        return response;
    }

    public void updateEntity(Inventory inventory, InventoryUpdateDTO request) {
        if (request.getQuantityOnHand() != null) {
            inventory.setQuantityOnHand(request.getQuantityOnHand());
        }
        if (request.getQuantityReserved() != null) {
            inventory.setQuantityReserved(request.getQuantityReserved());
        }
    }
}