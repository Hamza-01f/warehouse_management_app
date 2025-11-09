package com.brief.demo.controller;

import com.brief.demo.dto.request.InventoryRequestDTO;
import com.brief.demo.dto.request.InventoryUpdateDTO;
import com.brief.demo.dto.request.InventoryMovementRequestDTO;
import com.brief.demo.dto.response.ApiResponseDTO;
import com.brief.demo.dto.response.InventoryResponseDTO;
import com.brief.demo.dto.response.InventoryMovementResponseDTO;
import com.brief.demo.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Inventory management and movement tracking")
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(summary = "Create inventory record", description = "Creates a new inventory record for a product in a warehouse")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory created successfully"),
            @ApiResponse(responseCode = "404", description = "Product or warehouse not found"),
            @ApiResponse(responseCode = "409", description = "Inventory already exists for this product in warehouse")
    })
    @PostMapping
    public ResponseEntity<InventoryResponseDTO> createInventory(@RequestBody InventoryRequestDTO request) {
        InventoryResponseDTO response = inventoryService.createInventory(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all inventory", description = "Retrieves all inventory records")
    @ApiResponse(responseCode = "200", description = "Inventory records retrieved successfully")
    @GetMapping
    public ResponseEntity<List<InventoryResponseDTO>> getAllInventory() {
        List<InventoryResponseDTO> inventory = inventoryService.getAllInventory();
        return ResponseEntity.ok(inventory);
    }

    @Operation(summary = "Get inventory by warehouse", description = "Retrieves inventory records for a specific warehouse")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory records retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Warehouse not found")
    })
    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<InventoryResponseDTO>> getInventoryByWarehouse(
            @Parameter(description = "Warehouse ID", required = true, example = "1")
            @PathVariable Long warehouseId) {
        List<InventoryResponseDTO> inventory = inventoryService.getInventoryByWarehouse(warehouseId);
        return ResponseEntity.ok(inventory);
    }

    @Operation(summary = "Get inventory by product", description = "Retrieves inventory records for a specific product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory records retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<InventoryResponseDTO>> getInventoryByProduct(
            @Parameter(description = "Product ID", required = true, example = "1")
            @PathVariable Long productId) {
        List<InventoryResponseDTO> inventory = inventoryService.getInventoryByProduct(productId);
        return ResponseEntity.ok(inventory);
    }

    @Operation(summary = "Get inventory by ID", description = "Retrieves a specific inventory record by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory record found"),
            @ApiResponse(responseCode = "404", description = "Inventory record not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponseDTO> getInventoryById(
            @Parameter(description = "Inventory ID", required = true, example = "1")
            @PathVariable Long id) {
        InventoryResponseDTO inventory = inventoryService.getInventoryById(id);
        return ResponseEntity.ok(inventory);
    }

    @Operation(summary = "Update inventory", description = "Updates inventory quantities")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory updated successfully"),
            @ApiResponse(responseCode = "404", description = "Inventory record not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<InventoryResponseDTO> updateInventory(
            @Parameter(description = "Inventory ID", required = true, example = "1")
            @PathVariable Long id,
            @RequestBody InventoryUpdateDTO request) {
        InventoryResponseDTO inventory = inventoryService.updateInventory(id, request);
        return ResponseEntity.ok(inventory);
    }

    @Operation(summary = "Create inventory movement", description = "Records inventory movements (IN, OUT, ADJ)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movement recorded successfully"),
            @ApiResponse(responseCode = "404", description = "Inventory record not found"),
            @ApiResponse(responseCode = "400", description = "Insufficient available quantity")
    })
    @PostMapping("/movements")
    public ResponseEntity<InventoryMovementResponseDTO> createMovement(@RequestBody InventoryMovementRequestDTO request) {
        InventoryMovementResponseDTO movement = inventoryService.createMovement(request);
        return ResponseEntity.ok(movement);
    }

    @Operation(summary = "Get movements by inventory", description = "Retrieves movement history for a specific inventory record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movements retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Inventory record not found")
    })
    @GetMapping("/movements/inventory/{inventoryId}")
    public ResponseEntity<List<InventoryMovementResponseDTO>> getMovementsByInventory(
            @Parameter(description = "Inventory ID", required = true, example = "1")
            @PathVariable Long inventoryId) {
        List<InventoryMovementResponseDTO> movements = inventoryService.getMovementsByInventory(inventoryId);
        return ResponseEntity.ok(movements);
    }

    @Operation(summary = "Get movements by product", description = "Retrieves movement history for a specific product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movements retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/movements/product/{productId}")
    public ResponseEntity<List<InventoryMovementResponseDTO>> getMovementsByProduct(
            @Parameter(description = "Product ID", required = true, example = "1")
            @PathVariable Long productId) {
        List<InventoryMovementResponseDTO> movements = inventoryService.getMovementsByProduct(productId);
        return ResponseEntity.ok(movements);
    }

    @Operation(summary = "Check product availability", description = "Checks if sufficient quantity of a product is available")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Availability check completed"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/availability/{productId}")
    public ResponseEntity<ApiResponseDTO> checkAvailability(
            @Parameter(description = "Product ID", required = true, example = "1")
            @PathVariable Long productId,
            @Parameter(description = "Required quantity", required = true, example = "10")
            @RequestParam Integer quantity) {
        boolean available = inventoryService.checkAvailability(productId, quantity);
        String message = available ? "Product is available" : "Insufficient stock";
        return ResponseEntity.ok(new ApiResponseDTO(message, available));
    }
}