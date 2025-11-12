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

import java.util.Comparator;
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

        // Check if inventory already exists
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
            createMovement(savedInventory, MovementType.IN, request.getQuantityOnHand());
        }

        return inventoryMapper.toResponse(savedInventory);
    }

    @Transactional
    public void processInboundMovement(Long productId, Long warehouseId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByWarehouseIdAndProductId(warehouseId, productId)
                .orElseGet(() -> {
                    Product product = productRepository.findById(productId)
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
                    Warehouse warehouse = warehouseRepository.findById(warehouseId)
                            .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));
                    return Inventory.builder()
                            .product(product)
                            .warehouse(warehouse)
                            .quantityOnHand(0)
                            .quantityReserved(0)
                            .build();
                });

        inventory.setQuantityOnHand(inventory.getQuantityOnHand() + quantity);
        Inventory savedInventory = inventoryRepository.save(inventory);

        createMovement(savedInventory, MovementType.IN, quantity);
    }

    @Transactional
    public void processOutboundMovement(Long productId, Long warehouseId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByWarehouseIdAndProductId(warehouseId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

        if (inventory.getAvailable_Quantity() < quantity) {
            throw new IllegalStateException("Insufficient available quantity for outbound movement");
        }

        inventory.setQuantityOnHand(inventory.getQuantityOnHand() - quantity);
        Inventory savedInventory = inventoryRepository.save(inventory);

        createMovement(savedInventory, MovementType.OUT, quantity);
    }

    @Transactional
    public void adjustInventory(Long inventoryId, Integer newQuantity) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

        if (newQuantity < inventory.getQuantityReserved()) {
            throw new IllegalStateException("New quantity cannot be less than reserved quantity");
        }

        int quantityDiff = newQuantity - inventory.getQuantityOnHand();
        inventory.setQuantityOnHand(newQuantity);
        Inventory savedInventory = inventoryRepository.save(inventory);

        createMovement(savedInventory, MovementType.ADJ, Math.abs(quantityDiff));
    }

    InventoryMovement createMovement(Inventory inventory, MovementType type, Integer quantity) {
        InventoryMovement movement = InventoryMovement.builder()
                .inventory(inventory)
                .type(type)
                .quantity(quantity)
                .build();
        return inventoryMovementRepository.save(movement);
    }

    public List<Inventory> findAvailableInventoryForProduct(Long productId, Integer quantity) {
        return inventoryRepository.findByProductId(productId).stream()
                .filter(inv -> inv.getAvailable_Quantity() > 0)
                .sorted(Comparator.comparing(Inventory::getAvailable_Quantity).reversed())
                .collect(Collectors.toList());
    }

    public boolean reserveStockForOrder(SalesOrderLine orderLine, Warehouse preferredWarehouse) {
        Integer quantityToReserve = orderLine.getQuantity() - orderLine.getQuantityReserved();
        if (quantityToReserve <= 0) return true;

        boolean reserved = tryReserveInWarehouse(orderLine, preferredWarehouse, quantityToReserve);
        if (reserved) return true;

        List<Inventory> availableInventories = findAvailableInventoryForProduct(
                orderLine.getProduct().getId(), quantityToReserve);

        for (Inventory inventory : availableInventories) {
            if (inventory.getWarehouse().getId().equals(preferredWarehouse.getId())) {
                continue;
            }

            Integer available = inventory.getAvailable_Quantity();
            if (available > 0) {
                Integer toReserve = Math.min(available, quantityToReserve);
                inventory.reserveQuantity(toReserve);
                inventoryRepository.save(inventory);

                orderLine.setQuantityReserved(orderLine.getQuantityReserved() + toReserve);
                quantityToReserve -= toReserve;

                createMovement(inventory, MovementType.OUT, toReserve);

                if (quantityToReserve <= 0) {
                    return true;
                }
            }
        }

        return quantityToReserve <= 0;
    }

    private boolean tryReserveInWarehouse(SalesOrderLine orderLine, Warehouse warehouse, Integer quantityToReserve) {
        Inventory inventory = inventoryRepository.findByWarehouseIdAndProductId(
                warehouse.getId(), orderLine.getProduct().getId()).orElse(null);

        if (inventory != null && inventory.getAvailable_Quantity() >= quantityToReserve) {
            inventory.reserveQuantity(quantityToReserve);
            inventoryRepository.save(inventory);
            orderLine.setQuantityReserved(orderLine.getQuantityReserved() + quantityToReserve);

            createMovement(inventory, MovementType.OUT, quantityToReserve);
            return true;
        }
        return false;
    }

    public void releaseReservedStock(SalesOrderLine orderLine) {
        if (orderLine.getQuantityReserved() <= 0) return;

        List<Inventory> inventories = inventoryRepository.findByProductId(orderLine.getProduct().getId());
        Integer quantityToRelease = orderLine.getQuantityReserved();

        for (Inventory inventory : inventories) {
            if (quantityToRelease <= 0) break;

            if (inventory.getQuantityReserved() > 0) {
                Integer toRelease = Math.min(inventory.getQuantityReserved(), quantityToRelease);
                inventory.releaseReservedQuantity(toRelease);
                inventoryRepository.save(inventory);
                quantityToRelease -= toRelease;

                createMovement(inventory, MovementType.IN, toRelease);
            }
        }

        orderLine.setQuantityReserved(0);
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

        if (request.getQuantityOnHand() != null && !request.getQuantityOnHand().equals(oldQuantity)) {
            int quantityDiff = request.getQuantityOnHand() - oldQuantity;
            MovementType movementType = quantityDiff > 0 ? MovementType.IN : MovementType.OUT;
            createMovement(updatedInventory, movementType, Math.abs(quantityDiff));
        }

        return inventoryMapper.toResponse(updatedInventory);
    }

    @Transactional
    public InventoryMovementResponseDTO createMovement(InventoryMovementRequestDTO request) {
        Inventory inventory = inventoryRepository.findById(request.getInventoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

        switch (request.getType()) {
            case IN:
                inventory.setQuantityOnHand(inventory.getQuantityOnHand() + request.getQuantity());
                break;
            case OUT:
                if (inventory.getAvailable_Quantity() < request.getQuantity()) {
                    throw new IllegalStateException("Insufficient available quantity");
                }
                inventory.setQuantityOnHand(inventory.getQuantityOnHand() - request.getQuantity());
                break;
            case ADJ:
                inventory.setQuantityOnHand(request.getQuantity());
                break;
        }

        inventoryRepository.save(inventory);
        InventoryMovement movement = createMovement(inventory, request.getType(), request.getQuantity());

        return mapToMovementResponse(movement);
    }

    private InventoryMovementResponseDTO mapToMovementResponse(InventoryMovement movement) {
        InventoryMovementResponseDTO response = new InventoryMovementResponseDTO();
        response.setId(movement.getId());
        response.setType(movement.getType());
        response.setQuantity(movement.getQuantity());
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

    public boolean checkAvailability(Long productId, Integer quantity) {
        Integer available = inventoryRepository.getTotalAvailableQuantityByProductId(productId);
        return available != null && available >= quantity;
    }
}