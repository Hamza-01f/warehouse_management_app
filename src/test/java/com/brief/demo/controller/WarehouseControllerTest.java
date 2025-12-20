package com.brief.demo.controller;

import com.brief.demo.dto.request.WarehouseRequestDTO;
import com.brief.demo.dto.response.ApiResponseDTO;
import com.brief.demo.dto.response.WarehouseResponseDTO;
import com.brief.demo.service.WarehouseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseControllerTest {

    @Mock
    private WarehouseService warehouseService;

    @InjectMocks
    private WarehouseController warehouseController;

    private WarehouseResponseDTO warehouseResponse;

    @BeforeEach
    void setUp() {
        warehouseResponse = new WarehouseResponseDTO();
        warehouseResponse.setId(1L);
        warehouseResponse.setName("Test Warehouse");
    }

    @Test
    void createWarehouse_ShouldReturnWarehouse() {
        // ARRANGE
        WarehouseRequestDTO request = new WarehouseRequestDTO();
        when(warehouseService.createWarehouse(any(WarehouseRequestDTO.class))).thenReturn(warehouseResponse);

        // ACT
        ResponseEntity<WarehouseResponseDTO> response = warehouseController.createWarehouse(request);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(warehouseResponse, response.getBody());
        verify(warehouseService, times(1)).createWarehouse(request);
    }

    @Test
    void getAllWarehouses_ShouldReturnList() {
        // ARRANGE
        List<WarehouseResponseDTO> warehouses = Arrays.asList(warehouseResponse);
        when(warehouseService.getAllWarehouses()).thenReturn(warehouses);

        // ACT
        ResponseEntity<List<WarehouseResponseDTO>> response = warehouseController.getAllWarehouses();

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(warehouseService, times(1)).getAllWarehouses();
    }

    @Test
    void getWarehouseById_ShouldReturnWarehouse() {
        // ARRANGE
        when(warehouseService.getWarehouseById(1L)).thenReturn(warehouseResponse);

        // ACT
        ResponseEntity<WarehouseResponseDTO> response = warehouseController.getWarehouseById(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(warehouseResponse, response.getBody());
        verify(warehouseService, times(1)).getWarehouseById(1L);
    }

    @Test
    void updateWarehouse_ShouldReturnUpdatedWarehouse() {
        // ARRANGE
        WarehouseRequestDTO request = new WarehouseRequestDTO();
        when(warehouseService.updateWarehouse(1L, request)).thenReturn(warehouseResponse);

        // ACT
        ResponseEntity<WarehouseResponseDTO> response = warehouseController.updateWarehouse(1L, request);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(warehouseResponse, response.getBody());
        verify(warehouseService, times(1)).updateWarehouse(1L, request);
    }

    @Test
    void deleteWarehouse_ShouldReturnSuccessMessage() {
        // ARRANGE
        doNothing().when(warehouseService).deleteWarehouse(1L);

        // ACT
        ResponseEntity<ApiResponseDTO> response = warehouseController.deleteWarehouse(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Warehouse deleted successfully", response.getBody().getMessage());
        assertTrue(response.getBody().isSuccess());
        verify(warehouseService, times(1)).deleteWarehouse(1L);
    }

    @Test
    void activateWarehouse_ShouldReturnSuccessMessage() {
        // ARRANGE
        doNothing().when(warehouseService).activateWarehouse(1L);

        // ACT
        ResponseEntity<ApiResponseDTO> response = warehouseController.activateWarehouse(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Warehouse activated successfully", response.getBody().getMessage());
        assertTrue(response.getBody().isSuccess());
        verify(warehouseService, times(1)).activateWarehouse(1L);
    }
}