package com.brief.demo.mappers;

import com.brief.demo.dto.request.ShipmentRequestDTO;
import com.brief.demo.dto.response.ShipmentResponseDTO;
import com.brief.demo.enums.ShipmentStatus;
import com.brief.demo.model.SalesOrder;
import com.brief.demo.model.Shipment;
import com.brief.demo.repository.SalesOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShipmentMapper {

    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderMapper salesOrderMapper;

    public Shipment toEntity(ShipmentRequestDTO request) {
        SalesOrder salesOrder = salesOrderRepository.findById(request.getSalesOrderId())
                .orElseThrow(() -> new RuntimeException("Sales order not found"));

        return Shipment.builder()
                .salesOrder(salesOrder)
                .trackingNumber(request.getTrackingNumber())
                .status(ShipmentStatus.PLANNED)
                .build();
    }

    public ShipmentResponseDTO toResponse(Shipment shipment) {
        ShipmentResponseDTO response = new ShipmentResponseDTO();
        response.setId(shipment.getId());
        response.setTrackingNumber(shipment.getTrackingNumber());
        response.setStatus(shipment.getStatus());
        response.setSalesOrder(salesOrderMapper.toResponse(shipment.getSalesOrder()));
        return response;
    }
}