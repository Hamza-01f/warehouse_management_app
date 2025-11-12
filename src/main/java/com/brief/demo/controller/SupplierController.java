package com.brief.demo.controller;

//import com.brief.demo.aop.RequiresAdmin;
import com.brief.demo.aop.RequiresAdmin;
import com.brief.demo.dto.request.SupplierRequestDTO;
import com.brief.demo.dto.response.ApiResponseDTO;
import com.brief.demo.dto.response.SupplierResponseDTO;
import com.brief.demo.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    @RequiresAdmin
    public ResponseEntity<SupplierResponseDTO> createSupplier(@RequestBody SupplierRequestDTO request) {
        SupplierResponseDTO response = supplierService.createSupplier(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<SupplierResponseDTO>> getAllSuppliers() {
        List<SupplierResponseDTO> suppliers = supplierService.getAllSuppliers();
        return ResponseEntity.ok(suppliers);
    }

    @GetMapping("/active")
    public ResponseEntity<List<SupplierResponseDTO>> getActiveSuppliers() {
        List<SupplierResponseDTO> suppliers = supplierService.getActiveSuppliers();
        return ResponseEntity.ok(suppliers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponseDTO> getSupplierById(@PathVariable Long id) {
        SupplierResponseDTO supplier = supplierService.getSupplierById(id);
        return ResponseEntity.ok(supplier);
    }

    @PutMapping("/{id}")
    @RequiresAdmin
    public ResponseEntity<SupplierResponseDTO> updateSupplier(
            @PathVariable Long id,
            @RequestBody SupplierRequestDTO request) {
        SupplierResponseDTO supplier = supplierService.updateSupplier(id, request);
        return ResponseEntity.ok(supplier);
    }

    @DeleteMapping("/{id}")
    @RequiresAdmin
    public ResponseEntity<ApiResponseDTO> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.ok(new ApiResponseDTO("Supplier deleted successfully", true));
    }

    @PatchMapping("/{id}/activate")
    @RequiresAdmin
    public ResponseEntity<ApiResponseDTO> activateSupplier(@PathVariable Long id) {
        supplierService.activateSupplier(id);
        return ResponseEntity.ok(new ApiResponseDTO("Supplier activated successfully", true));
    }
}