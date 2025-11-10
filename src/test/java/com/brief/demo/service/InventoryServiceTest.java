//package com.brief.demo.service;
//
//import com.brief.demo.dto.request.InventoryRequestDTO;
//import com.brief.demo.dto.request.InventoryUpdateDTO;
//import com.brief.demo.dto.request.InventoryMovementRequestDTO;
//import com.brief.demo.dto.response.InventoryResponseDTO;
//import com.brief.demo.dto.response.InventoryMovementResponseDTO;
//import com.brief.demo.enums.MovementType;
//import com.brief.demo.exception.ResourceNotFoundException;
//import com.brief.demo.mappers.InventoryMapper;
//import com.brief.demo.model.*;
//import com.brief.demo.repository.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class InventoryServiceTest {
//
//    @Mock
//    private InventoryRepository inventoryRepository;
//
//    @Mock
//    private InventoryMovementRepository inventoryMovementRepository;
//
//    @Mock
//    private ProductRepository productRepository;
//
//    @Mock
//    private WarehouseRepository warehouseRepository;
//
//    @Mock
//    private InventoryMapper inventoryMapper;
//
//    @InjectMocks
//    private InventoryService inventoryService;
//
//    private Product product;
//    private Warehouse warehouse;
//    private Inventory inventory;
//    private InventoryRequestDTO inventoryRequestDTO;
//    private InventoryResponseDTO inventoryResponseDTO;
//
//    @BeforeEach
//    void setUp() {
//        product = Product.builder()
//                .id(1L)
//                .name("Test Product")
//                .build();
//
//        warehouse = Warehouse.builder()
//                .id(1L)
//                .name("Main Warehouse")
//                .build();
//
//        inventory = Inventory.builder()
//                .id(1L)
//                .product(product)
//                .warehouse(warehouse)
//                .quantityOnHand(100)
//                .quantityReserved(20)
//                .available_quantity(80)
//                .build();
//
//        inventoryRequestDTO = new InventoryRequestDTO();
//        inventoryRequestDTO.setProductId(1L);
//        inventoryRequestDTO.setWarehouseId(1L);
//        inventoryRequestDTO.setQuantityOnHand(100);
//        inventoryRequestDTO.setQuantityReserved(0);
//
//        inventoryResponseDTO = new InventoryResponseDTO();
//        inventoryResponseDTO.setId(1L);
//        inventoryResponseDTO.setQuantityOnHand(100);
//        inventoryResponseDTO.setQuantityReserved(20);
//        inventoryResponseDTO.setAvailableQuantity(80);
//    }
//
//    @Test
//    void createInventory_ShouldCreateInventory_WhenValidRequest() {
//        // Arrange
//        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
//        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
//        when(inventoryRepository.findByWarehouseIdAndProductId(1L, 1L)).thenReturn(Optional.empty());
//        when(inventoryMapper.toEntity(inventoryRequestDTO)).thenReturn(inventory);
//        when(inventoryRepository.save(inventory)).thenReturn(inventory);
//        when(inventoryMapper.toResponse(inventory)).thenReturn(inventoryResponseDTO);
//
//        // Act
//        InventoryResponseDTO result = inventoryService.createInventory(inventoryRequestDTO);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(1L, result.getId());
//        verify(inventoryRepository, times(1)).save(inventory);
//        verify(inventoryMovementRepository, times(1)).save(any(InventoryMovement.class));
//    }
//
//    @Test
//    void createInventory_ShouldThrowException_WhenProductNotFound() {
//        // Arrange
//        when(productRepository.findById(1L)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(ResourceNotFoundException.class, () -> {
//            inventoryService.createInventory(inventoryRequestDTO);
//        });
//    }
//
//    @Test
//    void createInventory_ShouldThrowException_WhenInventoryAlreadyExists() {
//        // Arrange
//        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
//        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
//        when(inventoryRepository.findByWarehouseIdAndProductId(1L, 1L)).thenReturn(Optional.of(inventory));
//
//        // Act & Assert
//        assertThrows(ResourceNotFoundException.class, () -> {
//            inventoryService.createInventory(inventoryRequestDTO);
//        });
//    }
//
//    @Test
//    void getInventoryById_ShouldReturnInventory_WhenExists() {
//        // Arrange
//        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));
//        when(inventoryMapper.toResponse(inventory)).thenReturn(inventoryResponseDTO);
//
//        // Act
//        InventoryResponseDTO result = inventoryService.getInventoryById(1L);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(1L, result.getId());
//    }
//
//    @Test
//    void getInventoryById_ShouldThrowException_WhenNotFound() {
//        // Arrange
//        when(inventoryRepository.findById(1L)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(ResourceNotFoundException.class, () -> {
//            inventoryService.getInventoryById(1L);
//        });
//    }
//
//    @Test
//    void getAllInventory_ShouldReturnAllInventory() {
//        // Arrange
//        List<Inventory> inventories = Arrays.asList(inventory);
//        when(inventoryRepository.findAll()).thenReturn(inventories);
//        when(inventoryMapper.toResponse(inventory)).thenReturn(inventoryResponseDTO);
//
//        // Act
//        List<InventoryResponseDTO> result = inventoryService.getAllInventory();
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(1, result.size());
//    }
//
//    @Test
//    void getInventoryByWarehouse_ShouldReturnWarehouseInventory() {
//        // Arrange
//        List<Inventory> inventories = Arrays.asList(inventory);
//        when(inventoryRepository.findByWarehouseId(1L)).thenReturn(inventories);
//        when(inventoryMapper.toResponse(inventory)).thenReturn(inventoryResponseDTO);
//
//        // Act
//        List<InventoryResponseDTO> result = inventoryService.getInventoryByWarehouse(1L);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(1, result.size());
//    }
//
//    @Test
//    void updateInventory_ShouldUpdateQuantity_WhenValidRequest() {
//        // Arrange
//        InventoryUpdateDTO updateDTO = new InventoryUpdateDTO();
//        updateDTO.setQuantityOnHand(150);
//
//        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));
//        when(inventoryRepository.save(inventory)).thenReturn(inventory);
//        when(inventoryMapper.toResponse(inventory)).thenReturn(inventoryResponseDTO);
//
//        // Act
//        InventoryResponseDTO result = inventoryService.updateInventory(1L, updateDTO);
//
//        // Assert
//        assertNotNull(result);
//        verify(inventoryMapper, times(1)).updateEntity(inventory, updateDTO);
//        verify(inventoryMovementRepository, times(1)).save(any(InventoryMovement.class));
//    }
//
//    @Test
//    void createMovement_IN_ShouldIncreaseQuantity() {
//        // Arrange
//        InventoryMovementRequestDTO movementRequest = new InventoryMovementRequestDTO();
//        movementRequest.setInventoryId(1L);
//        movementRequest.setType(MovementType.IN);
//        movementRequest.setQuantity(10);
//
//        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));
//        when(inventoryRepository.save(inventory)).thenReturn(inventory);
//        when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenReturn(new InventoryMovement());
//
//        // Act
//        InventoryMovementResponseDTO result = inventoryService.createMovement(movementRequest);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(110, inventory.getQuantityOnHand()); // 100 + 10
//    }
//
//    @Test
//    void createMovement_OUT_ShouldDecreaseQuantity_WhenSufficientStock() {
//        // Arrange
//        InventoryMovementRequestDTO movementRequest = new InventoryMovementRequestDTO();
//        movementRequest.setInventoryId(1L);
//        movementRequest.setType(MovementType.OUT);
//        movementRequest.setQuantity(10);
//
//        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));
//        when(inventoryRepository.save(inventory)).thenReturn(inventory);
//        when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenReturn(new InventoryMovement());
//
//        // Act
//        InventoryMovementResponseDTO result = inventoryService.createMovement(movementRequest);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(90, inventory.getQuantityOnHand()); // 100 - 10
//    }
//
//    @Test
//    void createMovement_OUT_ShouldThrowException_WhenInsufficientStock() {
//        // Arrange
//        inventory.setQuantityOnHand(5); // Low stock
//        InventoryMovementRequestDTO movementRequest = new InventoryMovementRequestDTO();
//        movementRequest.setInventoryId(1L);
//        movementRequest.setType(MovementType.OUT);
//        movementRequest.setQuantity(10); // Try to remove more than available
//
//        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));
//
//        // Act & Assert
//        assertThrows(IllegalStateException.class, () -> {
//            inventoryService.createMovement(movementRequest);
//        });
//    }
//
//    @Test
//    void checkAvailability_ShouldReturnTrue_WhenSufficientQuantity() {
//        // Arrange
//        when(inventoryRepository.getTotalAvailableQuantityByProductId(1L)).thenReturn(50);
//
//        // Act
//        boolean result = inventoryService.checkAvailability(1L, 30);
//
//        // Assert
//        assertTrue(result);
//    }
//
//    @Test
//    void checkAvailability_ShouldReturnFalse_WhenInsufficientQuantity() {
//        // Arrange
//        when(inventoryRepository.getTotalAvailableQuantityByProductId(1L)).thenReturn(20);
//
//        // Act
//        boolean result = inventoryService.checkAvailability(1L, 30);
//
//        // Assert
//        assertFalse(result);
//    }
//
//    @Test
//    void checkAvailability_ShouldReturnFalse_WhenNoInventory() {
//        // Arrange
//        when(inventoryRepository.getTotalAvailableQuantityByProductId(1L)).thenReturn(null);
//
//        // Act
//        boolean result = inventoryService.checkAvailability(1L, 10);
//
//        // Assert
//        assertFalse(result);
//    }
//}