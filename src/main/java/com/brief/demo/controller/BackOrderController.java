package com.brief.demo.controller;

import com.brief.demo.dto.response.BackOrderResponseDTO;
import com.brief.demo.service.BackOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/backorders")
@RequiredArgsConstructor
public class BackOrderController {

    private final BackOrderService backOrderService;

    @GetMapping
    public ResponseEntity<List<BackOrderResponseDTO>> getAllBackOrders() {
        List<BackOrderResponseDTO> backOrders = backOrderService.getAllBackOrders();
        return ResponseEntity.ok(backOrders);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<BackOrderResponseDTO>> getBackOrdersByStatus(@PathVariable String status) {
        List<BackOrderResponseDTO> backOrders = backOrderService.getBackOrdersByStatus(
                com.brief.demo.enums.BackOrderStatus.valueOf(status)
        );
        return ResponseEntity.ok(backOrders);
    }

    @PostMapping("/{id}/fulfill")
    public ResponseEntity<BackOrderResponseDTO> fulfillBackOrder(@PathVariable Long id) {
        BackOrderResponseDTO backOrder = backOrderService.fulfillBackOrder(id);
        return ResponseEntity.ok(backOrder);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<BackOrderResponseDTO> cancelBackOrder(@PathVariable Long id) {
        BackOrderResponseDTO backOrder = backOrderService.cancelBackOrder(id);
        return ResponseEntity.ok(backOrder);
    }

    @PostMapping("/process-pending")
    public ResponseEntity<String> processPendingBackOrders() {
        backOrderService.processPendingBackOrders();
        return ResponseEntity.ok("Pending backorders processed successfully");
    }
}