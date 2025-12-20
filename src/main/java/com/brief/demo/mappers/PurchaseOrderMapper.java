package com.brief.demo.mappers;

import com.brief.demo.dto.request.PurchaseOrderRequestDTO;
import com.brief.demo.dto.response.POLineResponseDTO;
import com.brief.demo.dto.response.PurchaseOrderResponseDTO;
import com.brief.demo.dto.response.SupplierResponseDTO;
import com.brief.demo.model.POLine;
import com.brief.demo.model.PurchaseOrder;
import com.brief.demo.model.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PurchaseOrderMapper {

    private final SupplierMapper supplierMapper;
    private final ProductMapper productMapper;
    private final BackOrderMapper backOrderMapper;

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
        response.setApprovedAt(purchaseOrder.getApprovedAt());
        response.setReceivedAt(purchaseOrder.getReceivedAt());

        // Map supplier
        if (purchaseOrder.getSupplier() != null) {
            SupplierResponseDTO supplierResponse = supplierMapper.toResponse(purchaseOrder.getSupplier());
            response.setSupplier(supplierResponse);
        }

        // Map backorder if exists
        if (purchaseOrder.getBackOrder() != null) {
            response.setBackOrder(backOrderMapper.toResponse(purchaseOrder.getBackOrder()));
        }

        // Map order lines
        if (purchaseOrder.getOrderLines() != null) {
            response.setOrderLines(purchaseOrder.getOrderLines().stream()
                    .map(this::toLineResponse)
                    .collect(Collectors.toList()));
        }

        return response;
    }

    private POLineResponseDTO toLineResponse(POLine line) {
        POLineResponseDTO response = new POLineResponseDTO();
        response.setId(line.getId());
        response.setQuantity(line.getQuantity());
        response.setQuantityReceived(line.getQuantityReceived());
        response.setPrice(line.getPrice());

        if (line.getProduct() != null) {
            response.setProduct(productMapper.toResponse(line.getProduct()));
        }

        return response;
    }
}