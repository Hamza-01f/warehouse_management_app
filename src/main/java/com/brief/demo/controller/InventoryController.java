package com.brief.demo.controller;

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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    @PreAuthorize("hasAuthority('INVENTORY_CREATE') or hasRole('ADMIN')")
    public ResponseEntity<InventoryResponseDTO> createInventory(@RequestBody InventoryRequestDTO request) {
        InventoryResponseDTO response = inventoryService.createInventory(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('INVENTORY_READ')")
    public ResponseEntity<List<InventoryResponseDTO>> getAllInventory() {
        List<InventoryResponseDTO> inventory = inventoryService.getAllInventory();
        return ResponseEntity.ok(inventory);
    }

    @GetMapping("/warehouse/{warehouseId}")
    @PreAuthorize("hasAuthority('INVENTORY_READ')")
    public ResponseEntity<List<InventoryResponseDTO>> getInventoryByWarehouse(@PathVariable Long warehouseId) {
        List<InventoryResponseDTO> inventory = inventoryService.getInventoryByWarehouse(warehouseId);
        return ResponseEntity.ok(inventory);
    }

    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAuthority('INVENTORY_READ')")
    public ResponseEntity<List<InventoryResponseDTO>> getInventoryByProduct(@PathVariable Long productId) {
        List<InventoryResponseDTO> inventory = inventoryService.getInventoryByProduct(productId);
        return ResponseEntity.ok(inventory);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('INVENTORY_READ')")
    public ResponseEntity<InventoryResponseDTO> getInventoryById(@PathVariable Long id) {
        InventoryResponseDTO inventory = inventoryService.getInventoryById(id);
        return ResponseEntity.ok(inventory);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('INVENTORY_UPDATE') or hasRole('ADMIN')")
    public ResponseEntity<InventoryResponseDTO> updateInventory(
            @PathVariable Long id,
            @RequestBody InventoryUpdateDTO request) {
        InventoryResponseDTO inventory = inventoryService.updateInventory(id, request);
        return ResponseEntity.ok(inventory);
    }

    @PostMapping("/movements")
    @PreAuthorize("hasAuthority('STOCK_MOVEMENT_CREATE') or hasRole('ADMIN')")
    public ResponseEntity<InventoryMovementResponseDTO> createMovement(@RequestBody InventoryMovementRequestDTO request) {
        InventoryMovementResponseDTO movement = inventoryService.createMovement(request);
        return ResponseEntity.ok(movement);
    }

    @GetMapping("/movements/inventory/{inventoryId}")
    @PreAuthorize("hasAuthority('STOCK_MOVEMENT_READ') or hasRole('ADMIN')")
    public ResponseEntity<List<InventoryMovementResponseDTO>> getMovementsByInventory(@PathVariable Long inventoryId) {
        List<InventoryMovementResponseDTO> movements = inventoryService.getMovementsByInventory(inventoryId);
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/movements/product/{productId}")
    @PreAuthorize("hasAuthority('STOCK_MOVEMENT_READ') or hasRole('ADMIN')")
    public ResponseEntity<List<InventoryMovementResponseDTO>> getMovementsByProduct(@PathVariable Long productId) {
        List<InventoryMovementResponseDTO> movements = inventoryService.getMovementsByProduct(productId);
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/availability/{productId}")
    @PreAuthorize("hasAuthority('INVENTORY_READ')")
    public ResponseEntity<ApiResponseDTO> checkAvailability(
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        boolean available = inventoryService.checkAvailability(productId, quantity);
        String message = available ? "Product is available" : "Insufficient stock";
        return ResponseEntity.ok(new ApiResponseDTO(message, available));
    }
}