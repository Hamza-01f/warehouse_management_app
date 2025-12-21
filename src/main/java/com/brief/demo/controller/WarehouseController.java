package com.brief.demo.controller;


import com.brief.demo.aop.RequiresAdmin;
import com.brief.demo.dto.request.WarehouseRequestDTO;
import com.brief.demo.dto.response.ApiResponseDTO;
import com.brief.demo.dto.response.WarehouseResponseDTO;
import com.brief.demo.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    @PreAuthorize("hasAuthority('WAREHOUSE_CREATE')")
    public ResponseEntity<WarehouseResponseDTO> createWarehouse(@RequestBody WarehouseRequestDTO request) {
        WarehouseResponseDTO response = warehouseService.createWarehouse(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('WAREHOUSE_READ')")
    public ResponseEntity<List<WarehouseResponseDTO>> getAllWarehouses() {
        List<WarehouseResponseDTO> warehouses = warehouseService.getAllWarehouses();
        return ResponseEntity.ok(warehouses);
    }

    @GetMapping("/active")
    @PreAuthorize("hasAuthority('WAREHOUSE_READ')")
    public ResponseEntity<List<WarehouseResponseDTO>> getActiveWarehouses() {
        List<WarehouseResponseDTO> warehouses = warehouseService.getActiveWarehouses();
        return ResponseEntity.ok(warehouses);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('WAREHOUSE_READ')")
    public ResponseEntity<WarehouseResponseDTO> getWarehouseById(@PathVariable Long id) {
        WarehouseResponseDTO warehouse = warehouseService.getWarehouseById(id);
        return ResponseEntity.ok(warehouse);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('WAREHOUSE_UPDATE')")
    public ResponseEntity<WarehouseResponseDTO> updateWarehouse(
            @PathVariable Long id,
            @RequestBody WarehouseRequestDTO request) {
        WarehouseResponseDTO warehouse = warehouseService.updateWarehouse(id, request);
        return ResponseEntity.ok(warehouse);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('WAREHOUSE_DELETE')")
    public ResponseEntity<ApiResponseDTO> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.ok(new ApiResponseDTO("Warehouse deleted successfully", true));
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('WAREHOUSE_UPDATE')")
    public ResponseEntity<ApiResponseDTO> activateWarehouse(@PathVariable Long id) {
        warehouseService.activateWarehouse(id);
        return ResponseEntity.ok(new ApiResponseDTO("Warehouse activated successfully", true));
    }
}