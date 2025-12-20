package com.brief.demo.service;

import com.brief.demo.dto.request.InventoryRequestDTO;
import com.brief.demo.dto.response.InventoryResponseDTO;
import com.brief.demo.enums.MovementType;
import com.brief.demo.exception.ResourceNotFoundException;
import com.brief.demo.mappers.InventoryMapper;
import com.brief.demo.model.*;
import com.brief.demo.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryMovementRepository inventoryMovementRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private InventoryMapper inventoryMapper;

    @InjectMocks
    private InventoryService inventoryService;

    private Inventory inventory;
    private Product product;
    private Warehouse warehouse;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .build();

        warehouse = Warehouse.builder()
                .id(1L)
                .name("Test Warehouse")
                .build();

        inventory = Inventory.builder()
                .id(1L)
                .product(product)
                .warehouse(warehouse)
                .quantityOnHand(100)
                .quantityReserved(20)
                .build();
    }

    @Test
    void createInventory_ShouldCreateNewInventory() {
        // Arrange
        InventoryRequestDTO request = new InventoryRequestDTO();
        request.setProductId(1L);
        request.setWarehouseId(1L);
        request.setQuantityOnHand(50);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(inventoryRepository.findByWarehouseIdAndProductId(1L, 1L)).thenReturn(Optional.empty());
        when(inventoryMapper.toEntity(any(InventoryRequestDTO.class))).thenReturn(inventory);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
        when(inventoryMapper.toResponse(any(Inventory.class))).thenReturn(new InventoryResponseDTO());

        // Act
        InventoryResponseDTO result = inventoryService.createInventory(request);

        // Assert
        assertNotNull(result);
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    void createInventory_WhenAlreadyExists_ShouldThrowException() {
        // Arrange
        InventoryRequestDTO request = new InventoryRequestDTO();
        request.setProductId(1L);
        request.setWarehouseId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(inventoryRepository.findByWarehouseIdAndProductId(1L, 1L)).thenReturn(Optional.of(inventory));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                inventoryService.createInventory(request));
    }

//    @Test
//    void processInboundMovement_ShouldUpdateQuantity() {
//        // Arrange
//        when(inventoryRepository.findByWarehouseIdAndProductId(1L, 1L))
//                .thenReturn(Optional.of(inventory));
//        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
//        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
//        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
//
//        // Act
//        inventoryService.processInboundMovement(1L, 1L, 50);
//
//        // Assert
//        verify(inventoryRepository, times(1)).save(any(Inventory.class));
//    }
//
//    @Test
//    void processOutboundMovement_WhenInsufficientQuantity_ShouldThrowException() {
//        // Arrange
//        inventory.setQuantityOnHand(10);
//        when(inventoryRepository.findByWarehouseIdAndProductId(1L, 1L))
//                .thenReturn(Optional.of(inventory));
//
//        // Act & Assert
//        assertThrows(IllegalStateException.class, () ->
//                inventoryService.processOutboundMovement(1L, 1L, 50));
//    }

    @Test
    void adjustInventory_ShouldUpdateQuantity() {
        // Arrange
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        // Act
        inventoryService.adjustInventory(1L, 150);

        // Assert
        assertEquals(150, inventory.getQuantityOnHand());
    }

    @Test
    void findAvailableInventoryForProduct_ShouldReturnFilteredList() {
        // Arrange
        when(inventoryRepository.findByProductId(1L)).thenReturn(List.of(inventory));

        // Act
        List<Inventory> result = inventoryService.findAvailableInventoryForProduct(1L, 10);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void reserveStockForOrder_WhenAvailable_ShouldReserve() {
        // Arrange
        SalesOrderLine orderLine = SalesOrderLine.builder()
                .id(1L)
                .product(product)
                .quantity(10)
                .quantityReserved(0)
                .build();

        when(inventoryRepository.findByWarehouseIdAndProductId(1L, 1L))
                .thenReturn(Optional.of(inventory));

        // Act
        boolean result = inventoryService.reserveStockForOrder(orderLine, warehouse);

        // Assert
        assertTrue(result);
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    void checkAvailability_WhenSufficient_ShouldReturnTrue() {
        // Arrange
        when(inventoryRepository.getTotalAvailableQuantityByProductId(1L)).thenReturn(100);

        // Act
        boolean result = inventoryService.checkAvailability(1L, 50);

        // Assert
        assertTrue(result);
    }
}