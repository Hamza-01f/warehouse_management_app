package com.brief.demo.controller;

//import com.brief.demo.aop.RequiresWarehouseManager;
import com.brief.demo.aop.RequiresWarehouseManager;
import com.brief.demo.dto.request.InventoryRequestDTO;
import com.brief.demo.dto.request.InventoryUpdateDTO;
import com.brief.demo.dto.request.InventoryMovementRequestDTO;
import com.brief.demo.dto.response.ApiResponseDTO;
import com.brief.demo.dto.response.InventoryResponseDTO;
import com.brief.demo.dto.response.InventoryMovementResponseDTO;
import com.brief.demo.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    @RequiresWarehouseManager
    public ResponseEntity<InventoryResponseDTO> createInventory(@RequestBody InventoryRequestDTO request) {
        InventoryResponseDTO response = inventoryService.createInventory(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<InventoryResponseDTO>> getAllInventory() {
        List<InventoryResponseDTO> inventory = inventoryService.getAllInventory();
        return ResponseEntity.ok(inventory);
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<InventoryResponseDTO>> getInventoryByWarehouse(@PathVariable Long warehouseId) {
        List<InventoryResponseDTO> inventory = inventoryService.getInventoryByWarehouse(warehouseId);
        return ResponseEntity.ok(inventory);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<InventoryResponseDTO>> getInventoryByProduct(@PathVariable Long productId) {
        List<InventoryResponseDTO> inventory = inventoryService.getInventoryByProduct(productId);
        return ResponseEntity.ok(inventory);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponseDTO> getInventoryById(@PathVariable Long id) {
        InventoryResponseDTO inventory = inventoryService.getInventoryById(id);
        return ResponseEntity.ok(inventory);
    }

    @PutMapping("/{id}")
    @RequiresWarehouseManager
    public ResponseEntity<InventoryResponseDTO> updateInventory(
            @PathVariable Long id,
            @RequestBody InventoryUpdateDTO request) {
        InventoryResponseDTO inventory = inventoryService.updateInventory(id, request);
        return ResponseEntity.ok(inventory);
    }

    @PostMapping("/movements")
    @RequiresWarehouseManager
    public ResponseEntity<InventoryMovementResponseDTO> createMovement(@RequestBody InventoryMovementRequestDTO request) {
        InventoryMovementResponseDTO movement = inventoryService.createMovement(request);
        return ResponseEntity.ok(movement);
    }

    @GetMapping("/movements/inventory/{inventoryId}")
    public ResponseEntity<List<InventoryMovementResponseDTO>> getMovementsByInventory(@PathVariable Long inventoryId) {
        List<InventoryMovementResponseDTO> movements = inventoryService.getMovementsByInventory(inventoryId);
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/movements/product/{productId}")
    public ResponseEntity<List<InventoryMovementResponseDTO>> getMovementsByProduct(@PathVariable Long productId) {
        List<InventoryMovementResponseDTO> movements = inventoryService.getMovementsByProduct(productId);
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/availability/{productId}")
    public ResponseEntity<ApiResponseDTO> checkAvailability(
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        boolean available = inventoryService.checkAvailability(productId, quantity);
        String message = available ? "Product is available" : "Insufficient stock";
        return ResponseEntity.ok(new ApiResponseDTO(message, available));
    }
}