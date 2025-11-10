package com.brief.demo.mappers;

import com.brief.demo.dto.response.BackOrderResponseDTO;
import com.brief.demo.model.BackOrder;
import org.springframework.stereotype.Component;

@Component
public class BackOrderMapper {

    public BackOrderResponseDTO toResponse(BackOrder backOrder) {
        BackOrderResponseDTO response = new BackOrderResponseDTO();
        response.setId(backOrder.getId());
        response.setQuantity(backOrder.getQuantity());
        response.setCreatedAt(backOrder.getCreatedAt());
        response.setFulfilledAt(backOrder.getFulfilledAt());
        response.setStatus(backOrder.getStatus());

        // Map sales order line and product if needed

        return response;
    }
}