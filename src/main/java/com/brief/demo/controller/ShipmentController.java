package com.brief.demo.controller;

//import com.brief.demo.aop.RequiresWarehouseManager;
import com.brief.demo.dto.request.ShipmentRequestDTO;
import com.brief.demo.dto.response.ApiResponseDTO;
import com.brief.demo.dto.response.ShipmentResponseDTO;
import com.brief.demo.enums.ShipmentStatus;
import com.brief.demo.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;

    @PostMapping
//    @RequiresWarehouseManager
    public ResponseEntity<ShipmentResponseDTO> createShipment(@RequestBody ShipmentRequestDTO request) {
        ShipmentResponseDTO response = shipmentService.createShipment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
//    @RequiresWarehouseManager
    public ResponseEntity<List<ShipmentResponseDTO>> getAllShipments() {
        List<ShipmentResponseDTO> shipments = shipmentService.getShipmentsByStatus(null);
        return ResponseEntity.ok(shipments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShipmentResponseDTO> getShipmentById(@PathVariable Long id) {
        ShipmentResponseDTO shipment = shipmentService.getShipmentById(id);
        return ResponseEntity.ok(shipment);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ShipmentResponseDTO> getShipmentByOrder(@PathVariable Long orderId) {
        ShipmentResponseDTO shipment = shipmentService.getShipmentByOrder(orderId);
        return ResponseEntity.ok(shipment);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ShipmentResponseDTO>> getShipmentsByStatus(@PathVariable ShipmentStatus status) {
        List<ShipmentResponseDTO> shipments = shipmentService.getShipmentsByStatus(status);
        return ResponseEntity.ok(shipments);
    }

    @PatchMapping("/{id}/status")
//    @RequiresWarehouseManager
    public ResponseEntity<ShipmentResponseDTO> updateShipmentStatus(
            @PathVariable Long id,
            @RequestParam ShipmentStatus status) {
        ShipmentResponseDTO shipment = shipmentService.updateShipmentStatus(id, status);
        return ResponseEntity.ok(shipment);
    }
}