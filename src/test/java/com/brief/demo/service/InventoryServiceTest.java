package com.brief.demo.service;

import com.brief.demo.dto.request.InventoryRequestDTO;
import com.brief.demo.dto.response.InventoryResponseDTO;
import com.brief.demo.enums.MovementType;
import com.brief.demo.exception.ResourceNotFoundException;
import com.brief.demo.mappers.InventoryMapper;
import com.brief.demo.model.*;
import com.brief.demo.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock private InventoryRepository inventoryRepository;
    @Mock private InventoryMovementRepository movementRepository;
    @Mock private ProductRepository productRepository;
    @Mock private WarehouseRepository warehouseRepository;
    @Mock private InventoryMapper inventoryMapper;

    @InjectMocks private InventoryService inventoryService;

    @Test
    void createInventory_Success() {
        // Given
        InventoryRequestDTO request = new InventoryRequestDTO();
        request.setProductId(1L);
        request.setWarehouseId(1L);
        request.setQuantityOnHand(100);

        Product product = new Product();
        product.setId(1L);
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        Inventory inventory = new Inventory();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(inventoryRepository.findByWarehouseIdAndProductId(1L, 1L)).thenReturn(Optional.empty());
        when(inventoryMapper.toEntity(request)).thenReturn(inventory);
        when(inventoryRepository.save(any())).thenReturn(inventory);
        when(inventoryMapper.toResponse(inventory)).thenReturn(new InventoryResponseDTO());

        // When
        InventoryResponseDTO result = inventoryService.createInventory(request);

        // Then
        assertNotNull(result);
        verify(inventoryRepository).save(inventory);
    }

    @Test
    void processInboundMovement_NewInventory() {
        // When
        inventoryService.processInboundMovement(1L, 1L, 50);

        // Then
        verify(inventoryRepository).save(any(Inventory.class));
        verify(movementRepository).save(any(InventoryMovement.class));
    }

    @Test
    void checkAvailability_ProductAvailable() {
        // Given
        when(inventoryRepository.getTotalAvailableQuantityByProductId(1L)).thenReturn(100);

        // When
        boolean result = inventoryService.checkAvailability(1L, 50);

        // Then
        assertTrue(result);
    }
}