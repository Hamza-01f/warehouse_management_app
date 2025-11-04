package com.brief.demo.service;

import com.brief.demo.dto.request.InventoryRequestDTO;
import com.brief.demo.dto.request.InventoryUpdateDTO;
import com.brief.demo.dto.request.InventoryMovementRequestDTO;
import com.brief.demo.dto.response.InventoryResponseDTO;
import com.brief.demo.dto.response.InventoryMovementResponseDTO;
import com.brief.demo.enums.MovementType;
import com.brief.demo.exception.ResourceNotFoundException;
import com.brief.demo.mappers.InventoryMapper;
import com.brief.demo.model.*;
import com.brief.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryMapper inventoryMapper;

    @Transactional
    public InventoryResponseDTO createInventory(InventoryRequestDTO request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));

        // Check if inventory already exists for this product and warehouse
        inventoryRepository.findByWarehouseIdAndProductId(warehouse.getId(), product.getId())
                .ifPresent(inv -> {
                    throw new ResourceNotFoundException("Inventory already exists for this product in the warehouse");
                });

        Inventory inventory = inventoryMapper.toEntity(request);
        inventory.setProduct(product);
        inventory.setWarehouse(warehouse);

        Inventory savedInventory = inventoryRepository.save(inventory);

        // Create initial movement
        if (request.getQuantityOnHand() != null && request.getQuantityOnHand() > 0) {
            createMovement(savedInventory, MovementType.INBOUND, request.getQuantityOnHand(), "Initial stock");
        }

        return inventoryMapper.toResponse(savedInventory);
    }

    public InventoryResponseDTO getInventoryById(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));
        return inventoryMapper.toResponse(inventory);
    }

    public List<InventoryResponseDTO> getAllInventory() {
        return inventoryRepository.findAll().stream()
                .map(inventoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<InventoryResponseDTO> getInventoryByWarehouse(Long warehouseId) {
        return inventoryRepository.findByWarehouseId(warehouseId).stream()
                .map(inventoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<InventoryResponseDTO> getInventoryByProduct(Long productId) {
        return inventoryRepository.findByProductId(productId).stream()
                .map(inventoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public InventoryResponseDTO updateInventory(Long id, InventoryUpdateDTO request) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

        Integer oldQuantity = inventory.getQuantityOnHand();
        inventoryMapper.updateEntity(inventory, request);
        Inventory updatedInventory = inventoryRepository.save(inventory);

        // Create movement if quantity changed
        if (request.getQuantityOnHand() != null && !request.getQuantityOnHand().equals(oldQuantity)) {
            int quantityDiff = request.getQuantityOnHand() - oldQuantity;
            MovementType movementType = quantityDiff > 0 ? MovementType.INBOUND : MovementType.OUTBOUND;
            createMovement(updatedInventory, movementType, Math.abs(quantityDiff), "Manual adjustment");
        }

        return inventoryMapper.toResponse(updatedInventory);
    }

    @Transactional
    public InventoryMovementResponseDTO createMovement(InventoryMovementRequestDTO request) {
        Inventory inventory = inventoryRepository.findById(request.getInventoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

        // Update inventory based on movement type
        switch (request.getType()) {
            case INBOUND:
                inventory.setQuantityOnHand(inventory.getQuantityOnHand() + request.getQuantity());
                break;
            case OUTBOUND:
                if (inventory.getAvailableQuantity() < request.getQuantity()) {
                    throw new IllegalStateException("Insufficient available quantity");
                }
                inventory.setQuantityOnHand(inventory.getQuantityOnHand() - request.getQuantity());
                break;
            case ADJUSTMENT:
                inventory.setQuantityOnHand(request.getQuantity());
                break;
        }

        inventoryRepository.save(inventory);
        InventoryMovement movement = createMovement(inventory, request.getType(), request.getQuantity(), request.getReason());

        return mapToMovementResponse(movement);
    }

    private InventoryMovement createMovement(Inventory inventory, MovementType type, Integer quantity, String reason) {
        InventoryMovement movement = InventoryMovement.builder()
                .inventory(inventory)
                .type(type)
                .quantity(quantity)
//                .reason(reason)
                .build();
        return inventoryMovementRepository.save(movement);
    }

    private InventoryMovementResponseDTO mapToMovementResponse(InventoryMovement movement) {
        InventoryMovementResponseDTO response = new InventoryMovementResponseDTO();
        response.setId(movement.getId());
        response.setType(movement.getType());
        response.setQuantity(movement.getQuantity());
//        response.setReason(movement.getReason());
        response.setOccurredAt(movement.getOccurredAt());
        response.setInventory(inventoryMapper.toResponse(movement.getInventory()));
        return response;
    }

    public List<InventoryMovementResponseDTO> getMovementsByInventory(Long inventoryId) {
        return inventoryMovementRepository.findByInventoryIdOrderByOccurredAtDesc(inventoryId).stream()
                .map(this::mapToMovementResponse)
                .collect(Collectors.toList());
    }

    public List<InventoryMovementResponseDTO> getMovementsByProduct(Long productId) {
        return inventoryMovementRepository.findByInventoryProductIdOrderByOccurredAtDesc(productId).stream()
                .map(this::mapToMovementResponse)
                .collect(Collectors.toList());
    }

    // Method to check availability
    public boolean checkAvailability(Long productId, Integer quantity) {
        Integer available = inventoryRepository.getTotalAvailableQuantityByProductId(productId);
        return available != null && available >= quantity;
    }
}