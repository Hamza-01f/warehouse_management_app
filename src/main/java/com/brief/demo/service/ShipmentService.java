package com.brief.demo.service;

import com.brief.demo.dto.request.ShipmentRequestDTO;
import com.brief.demo.dto.response.ShipmentResponseDTO;
import com.brief.demo.enums.OrderStatus;
import com.brief.demo.enums.ShipmentStatus;
import com.brief.demo.exception.ResourceNotFoundException;
import com.brief.demo.model.SalesOrder;
import com.brief.demo.model.Shipment;
import com.brief.demo.repository.SalesOrderRepository;
import com.brief.demo.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final InventoryService inventoryService;

    @Transactional
    public ShipmentResponseDTO createShipment(ShipmentRequestDTO request) {
        SalesOrder salesOrder = salesOrderRepository.findById(request.getSalesOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Sales order not found"));

        if (salesOrder.getStatus() != OrderStatus.RESERVED) {
            throw new IllegalStateException("Cannot create shipment for order in status: " + salesOrder.getStatus());
        }

        // Check if shipment already exists
        shipmentRepository.findBySalesOrderId(salesOrder.getId())
                .ifPresent(s -> {
                    throw new IllegalStateException("Shipment already exists for this order");
                });

        Shipment shipment = Shipment.builder()
                .salesOrder(salesOrder)
                .carrier(request.getCarrier())
                .trackingNumber(request.getTrackingNumber())
                .status(ShipmentStatus.PLANNED)
                .build();

        Shipment savedShipment = shipmentRepository.save(shipment);

        // Update order status
        salesOrder.setStatus(OrderStatus.SHIPPED);
        salesOrderRepository.save(salesOrder);

        return mapToResponse(savedShipment);
    }

    @Transactional
    public ShipmentResponseDTO updateShipmentStatus(Long shipmentId, ShipmentStatus status) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found"));

        shipment.setStatus(status);
        Shipment updatedShipment = shipmentRepository.save(shipment);

        // If delivered, update order status and reduce inventory
        if (status == ShipmentStatus.DELIVERED) {
            SalesOrder salesOrder = shipment.getSalesOrder();
            salesOrder.setStatus(OrderStatus.DELIVERED);
            salesOrderRepository.save(salesOrder);

            // Reduce inventory
            reduceInventoryOnDelivery(salesOrder.getId());
        }

        return mapToResponse(updatedShipment);
    }

    private void reduceInventoryOnDelivery(Long orderId) {
        SalesOrder salesOrder = salesOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sales order not found"));

        // This would typically involve reducing the actual inventory quantities
        // For now, we'll just log this action
        System.out.println("Reducing inventory for order: " + orderId);
    }

    public ShipmentResponseDTO getShipmentById(Long id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found"));
        return mapToResponse(shipment);
    }

    public List<ShipmentResponseDTO> getShipmentsByStatus(ShipmentStatus status) {
        return shipmentRepository.findByStatus(status.name()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ShipmentResponseDTO getShipmentByOrder(Long orderId) {
        Shipment shipment = shipmentRepository.findBySalesOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found for order"));
        return mapToResponse(shipment);
    }

    private ShipmentResponseDTO mapToResponse(Shipment shipment) {
        ShipmentResponseDTO response = new ShipmentResponseDTO();
        response.setId(shipment.getId());
        response.setCarrier(shipment.getCarrier());
        response.setTrackingNumber(shipment.getTrackingNumber());
        response.setStatus(shipment.getStatus());
        // You would need to map sales order here if needed
        return response;
    }
}