package com.brief.demo.controller;

import com.brief.demo.dto.request.PurchaseOrderRequestDTO;
import com.brief.demo.dto.request.PurchaseOrderUpdateDTO;
import com.brief.demo.dto.response.PurchaseOrderResponseDTO;
import com.brief.demo.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @PostMapping
    public ResponseEntity<PurchaseOrderResponseDTO> createPurchaseOrder(@RequestBody PurchaseOrderRequestDTO request) {
        PurchaseOrderResponseDTO response = purchaseOrderService.createPurchaseOrder(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PurchaseOrderResponseDTO>> getAllPurchaseOrders() {
        List<PurchaseOrderResponseDTO> purchaseOrders = purchaseOrderService.getAllPurchaseOrders();
        return ResponseEntity.ok(purchaseOrders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderResponseDTO> getPurchaseOrderById(@PathVariable Long id) {
        PurchaseOrderResponseDTO purchaseOrder = purchaseOrderService.getPurchaseOrderById(id);
        return ResponseEntity.ok(purchaseOrder);
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<PurchaseOrderResponseDTO>> getPurchaseOrdersBySupplier(@PathVariable Long supplierId) {
        List<PurchaseOrderResponseDTO> purchaseOrders = purchaseOrderService.getPurchaseOrdersBySupplier(supplierId);
        return ResponseEntity.ok(purchaseOrders);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PurchaseOrderResponseDTO> updatePurchaseOrderStatus(
            @PathVariable Long id,
            @RequestBody PurchaseOrderUpdateDTO request) {
        PurchaseOrderResponseDTO purchaseOrder = purchaseOrderService.updatePurchaseOrderStatus(id, request);
        return ResponseEntity.ok(purchaseOrder);
    }

    @PostMapping("/{id}/receive")
    public ResponseEntity<PurchaseOrderResponseDTO> receivePurchaseOrder(@PathVariable Long id) {
        // This would typically have more complex receiving logic
        PurchaseOrderUpdateDTO updateRequest = new PurchaseOrderUpdateDTO();
        updateRequest.setStatus(com.brief.demo.enums.POStatus.RECEIVED);

        PurchaseOrderResponseDTO purchaseOrder = purchaseOrderService.updatePurchaseOrderStatus(id, updateRequest);
        return ResponseEntity.ok(purchaseOrder);
    }
}