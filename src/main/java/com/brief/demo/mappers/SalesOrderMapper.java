package com.brief.demo.mappers;

import com.brief.demo.dto.request.SalesOrderRequestDTO;
import com.brief.demo.dto.response.SalesOrderLineResponseDTO;
import com.brief.demo.dto.response.SalesOrderResponseDTO;
import com.brief.demo.model.SalesOrder;
import com.brief.demo.model.SalesOrderLine;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class SalesOrderMapper {

    public SalesOrderResponseDTO toResponse(SalesOrder salesOrder) {
        SalesOrderResponseDTO response = new SalesOrderResponseDTO();
        response.setId(salesOrder.getId());
        response.setStatus(salesOrder.getStatus());
        response.setCreatedAt(salesOrder.getCreatedAt());
        response.setReservedAt(salesOrder.getReservedAt());
        response.setShippedAt(salesOrder.getShippedAt());
        response.setDeliveredAt(salesOrder.getDeliveredAt());
        response.setCountry(salesOrder.getCountry());
        response.setCity(salesOrder.getCity());
        response.setStreet(salesOrder.getStreet());
        response.setZipCode(salesOrder.getZipCode());

        // Map order lines
        if (salesOrder.getOrderLines() != null) {
            response.setOrderLines(salesOrder.getOrderLines().stream()
                    .map(this::toLineResponse)
                    .collect(Collectors.toList()));
        }

        return response;
    }

    private SalesOrderLineResponseDTO toLineResponse(SalesOrderLine line) {
        SalesOrderLineResponseDTO lineResponse = new SalesOrderLineResponseDTO();
        lineResponse.setId(line.getId());
        lineResponse.setQuantity(line.getQuantity());
        lineResponse.setQuantityReserved(line.getQuantityReserved());
        lineResponse.setQuantityFulfilled(line.getQuantityFulfilled());
        lineResponse.setBackorderQuantity(line.getBackorderQuantity());
        return lineResponse;
    }
}