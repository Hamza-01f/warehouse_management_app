package com.brief.demo.mappers;

import com.brief.demo.dto.request.PurchaseOrderRequestDTO;
import com.brief.demo.dto.response.POLineResponseDTO;
import com.brief.demo.dto.response.PurchaseOrderResponseDTO;
import com.brief.demo.model.POLine;
import com.brief.demo.model.PurchaseOrder;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class PurchaseOrderMapper {

    public PurchaseOrder toEntity(PurchaseOrderRequestDTO request) {
        return PurchaseOrder.builder()
                .status(com.brief.demo.enums.POStatus.CREATED)
                .build();
    }

    public PurchaseOrderResponseDTO toResponse(PurchaseOrder purchaseOrder) {
        PurchaseOrderResponseDTO response = new PurchaseOrderResponseDTO();
        response.setId(purchaseOrder.getId());
        response.setStatus(purchaseOrder.getStatus());
        response.setCreatedAt(purchaseOrder.getCreatedAt());

//        if (purchaseOrder.getSupplier() != null) {
//            // Map supplier if needed
//        }
//
//        if (purchaseOrder.getOrderLines() != null) {
//            response.setOrderLines(purchaseOrder.getOrderLines().stream()
//                    .map(this::toLineResponse)
//                    .collect(Collectors.toList()));
//        }

        return response;
    }

    private POLineResponseDTO toLineResponse(POLine line) {
        POLineResponseDTO response = new POLineResponseDTO();
        response.setId(line.getId());
        response.setQuantity(line.getQuantity());
        response.setPrice(line.getPrice());

        if (line.getProduct() != null) {
            // Map product if needed
        }

        return response;
    }
}