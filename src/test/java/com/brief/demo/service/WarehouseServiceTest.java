package com.brief.demo.service;

import com.brief.demo.dto.request.WarehouseRequestDTO;
import com.brief.demo.dto.response.WarehouseResponseDTO;
import com.brief.demo.exception.DuplicateResourceException;
import com.brief.demo.exception.ResourceNotFoundException;
import com.brief.demo.mappers.WarehouseMapper;
import com.brief.demo.model.Warehouse;
import com.brief.demo.repository.WarehouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private WarehouseMapper warehouseMapper;

    @InjectMocks
    private WarehouseService warehouseService;

    private WarehouseRequestDTO warehouseRequest;
    private Warehouse warehouse;
    private WarehouseResponseDTO warehouseResponse;

    @BeforeEach
    void setUp() {
        warehouseRequest = new WarehouseRequestDTO();
        warehouseRequest.setName("Test Warehouse");
        warehouseRequest.setAddress("123 Test Street");

        warehouse = Warehouse.builder()
                .id(1L)
                .name("Test Warehouse")
                .address("123 Test Street")
                .isActive(true)
                .build();

        warehouseResponse = new WarehouseResponseDTO();
        warehouseResponse.setId(1L);
        warehouseResponse.setName("Test Warehouse");
        warehouseResponse.setAddress("123 Test Street");
        warehouseResponse.setIsActive(true);
    }

    @Test
    void createWarehouse_ShouldCreateWarehouseSuccessfully() {
        // 🟢 ARRANGE
        when(warehouseRepository.existsByName("Test Warehouse")).thenReturn(false);
        when(warehouseMapper.toEntity(warehouseRequest)).thenReturn(warehouse);
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouse);
        when(warehouseMapper.toResponse(warehouse)).thenReturn(warehouseResponse);

        // 🟢 ACT
        WarehouseResponseDTO result = warehouseService.createWarehouse(warehouseRequest);

        // 🟢 ASSERT
        assertNotNull(result);
        assertEquals(warehouseResponse, result);
        verify(warehouseRepository, times(1)).existsByName("Test Warehouse");
        verify(warehouseRepository, times(1)).save(warehouse);
        verify(warehouseMapper, times(1)).toResponse(warehouse);
    }

    @Test
    void createWarehouse_WhenNameAlreadyExists_ShouldThrowDuplicateResourceException() {
        // 🟢 ARRANGE
        when(warehouseRepository.existsByName("Test Warehouse")).thenReturn(true);

        // 🟢 ACT & ASSERT
        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
                () -> warehouseService.createWarehouse(warehouseRequest));

        assertEquals("Warehouse with name Test Warehouse already exists", exception.getMessage());
        verify(warehouseRepository, never()).save(any(Warehouse.class));
    }

    @Test
    void getWarehouseById_ShouldReturnWarehouse() {
        // 🟢 ARRANGE
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(warehouseMapper.toResponse(warehouse)).thenReturn(warehouseResponse);

        // 🟢 ACT
        WarehouseResponseDTO result = warehouseService.getWarehouseById(1L);

        // 🟢 ASSERT
        assertNotNull(result);
        assertEquals(warehouseResponse, result);
        verify(warehouseRepository, times(1)).findById(1L);
        verify(warehouseMapper, times(1)).toResponse(warehouse);
    }

    @Test
    void getWarehouseById_WhenNotFound_ShouldThrowResourceNotFoundException() {
        // 🟢 ARRANGE
        when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());

        // 🟢 ACT & ASSERT
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> warehouseService.getWarehouseById(1L));

        assertEquals("Warehouse not found with id: 1", exception.getMessage());
    }

    @Test
    void getAllWarehouses_ShouldReturnAllWarehouses() {
        // 🟢 ARRANGE
        List<Warehouse> warehouses = Arrays.asList(warehouse);
        List<WarehouseResponseDTO> warehouseResponses = Arrays.asList(warehouseResponse);

        when(warehouseRepository.findAll()).thenReturn(warehouses);
        when(warehouseMapper.toResponse(warehouse)).thenReturn(warehouseResponse);

        // 🟢 ACT
        List<WarehouseResponseDTO> result = warehouseService.getAllWarehouses();

        // 🟢 ASSERT
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(warehouseResponses, result);
        verify(warehouseRepository, times(1)).findAll();
        verify(warehouseMapper, times(1)).toResponse(warehouse);
    }

    @Test
    void getActiveWarehouses_ShouldReturnActiveWarehouses() {
        // 🟢 ARRANGE
        List<Warehouse> activeWarehouses = Arrays.asList(warehouse);
        List<WarehouseResponseDTO> activeWarehouseResponses = Arrays.asList(warehouseResponse);

        when(warehouseRepository.findByIsActiveTrue()).thenReturn(activeWarehouses);
        when(warehouseMapper.toResponse(warehouse)).thenReturn(warehouseResponse);

        // 🟢 ACT
        List<WarehouseResponseDTO> result = warehouseService.getActiveWarehouses();

        // 🟢 ASSERT
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(activeWarehouseResponses, result);
        verify(warehouseRepository, times(1)).findByIsActiveTrue();
        verify(warehouseMapper, times(1)).toResponse(warehouse);
    }

    @Test
    void updateWarehouse_ShouldUpdateWarehouseSuccessfully() {
        // 🟢 ARRANGE
        WarehouseRequestDTO updateRequest = new WarehouseRequestDTO();
        updateRequest.setName("Updated Warehouse");
        updateRequest.setAddress("456 Updated Street");

        Warehouse updatedWarehouse = Warehouse.builder()
                .id(1L)
                .name("Updated Warehouse")
                .address("456 Updated Street")
                .isActive(true)
                .build();

        WarehouseResponseDTO updatedResponse = new WarehouseResponseDTO();
        updatedResponse.setId(1L);
        updatedResponse.setName("Updated Warehouse");
        updatedResponse.setAddress("456 Updated Street");
        updatedResponse.setIsActive(true);

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.existsByNameAndIdNot("Updated Warehouse", 1L)).thenReturn(false);
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(updatedWarehouse);
        when(warehouseMapper.toResponse(updatedWarehouse)).thenReturn(updatedResponse);

        // 🟢 ACT
        WarehouseResponseDTO result = warehouseService.updateWarehouse(1L, updateRequest);

        // 🟢 ASSERT
        assertNotNull(result);
        assertEquals(updatedResponse, result);
        assertEquals("Updated Warehouse", warehouse.getName());
        assertEquals("456 Updated Street", warehouse.getAddress());
        verify(warehouseRepository, times(1)).save(warehouse);
    }

    @Test
    void updateWarehouse_WhenNameConflict_ShouldThrowDuplicateResourceException() {
        // 🟢 ARRANGE
        WarehouseRequestDTO updateRequest = new WarehouseRequestDTO();
        updateRequest.setName("Existing Warehouse");

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.existsByNameAndIdNot("Existing Warehouse", 1L)).thenReturn(true);

        // 🟢 ACT & ASSERT
        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
                () -> warehouseService.updateWarehouse(1L, updateRequest));

        assertEquals("Warehouse with name Existing Warehouse already exists", exception.getMessage());
        verify(warehouseRepository, never()).save(any(Warehouse.class));
    }

    @Test
    void updateWarehouse_WhenPartialUpdate_ShouldUpdateOnlyProvidedFields() {
        // 🟢 ARRANGE
        WarehouseRequestDTO partialUpdateRequest = new WarehouseRequestDTO();
        partialUpdateRequest.setName("Updated Name Only");
        // Address not set

        Warehouse updatedWarehouse = Warehouse.builder()
                .id(1L)
                .name("Updated Name Only")
                .address("123 Test Street") // Original address unchanged
                .isActive(true)
                .build();

        WarehouseResponseDTO updatedResponse = new WarehouseResponseDTO();
        updatedResponse.setId(1L);
        updatedResponse.setName("Updated Name Only");
        updatedResponse.setAddress("123 Test Street");

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.existsByNameAndIdNot("Updated Name Only", 1L)).thenReturn(false);
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(updatedWarehouse);
        when(warehouseMapper.toResponse(updatedWarehouse)).thenReturn(updatedResponse);

        // 🟢 ACT
        WarehouseResponseDTO result = warehouseService.updateWarehouse(1L, partialUpdateRequest);

        // 🟢 ASSERT
        assertNotNull(result);
        assertEquals("Updated Name Only", warehouse.getName());
        assertEquals("123 Test Street", warehouse.getAddress()); // Unchanged
    }

    @Test
    void deleteWarehouse_ShouldDeactivateWarehouse() {
        // 🟢 ARRANGE
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.save(warehouse)).thenReturn(warehouse);

        // 🟢 ACT
        warehouseService.deleteWarehouse(1L);

        // 🟢 ASSERT
        assertFalse(warehouse.getIsActive());
        verify(warehouseRepository, times(1)).save(warehouse);
    }

    @Test
    void deleteWarehouse_WhenWarehouseNotFound_ShouldThrowResourceNotFoundException() {
        // 🟢 ARRANGE
        when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());

        // 🟢 ACT & ASSERT
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> warehouseService.deleteWarehouse(1L));

        assertEquals("Warehouse not found with id: 1", exception.getMessage());
        verify(warehouseRepository, never()).save(any(Warehouse.class));
    }

    @Test
    void activateWarehouse_ShouldActivateWarehouse() {
        // 🟢 ARRANGE
        warehouse.setIsActive(false); // Start with inactive
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.save(warehouse)).thenReturn(warehouse);

        // 🟢 ACT
        warehouseService.activateWarehouse(1L);

        // 🟢 ASSERT
        assertTrue(warehouse.getIsActive());
        verify(warehouseRepository, times(1)).save(warehouse);
    }

    @Test
    void activateWarehouse_WhenWarehouseNotFound_ShouldThrowResourceNotFoundException() {
        // 🟢 ARRANGE
        when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());

        // 🟢 ACT & ASSERT
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> warehouseService.activateWarehouse(1L));

        assertEquals("Warehouse not found with id: 1", exception.getMessage());
        verify(warehouseRepository, never()).save(any(Warehouse.class));
    }
}