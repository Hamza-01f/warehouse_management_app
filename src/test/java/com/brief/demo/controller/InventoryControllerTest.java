package com.brief.demo.controller;

import com.brief.demo.dto.request.InventoryRequestDTO;
import com.brief.demo.dto.request.InventoryUpdateDTO;
import com.brief.demo.dto.request.InventoryMovementRequestDTO;
import com.brief.demo.dto.response.ApiResponseDTO;
import com.brief.demo.dto.response.InventoryResponseDTO;
import com.brief.demo.dto.response.InventoryMovementResponseDTO;
import com.brief.demo.service.InventoryService;
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
class InventoryControllerTest {

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController inventoryController;

    private InventoryResponseDTO inventoryResponse;
    private InventoryMovementResponseDTO movementResponse;

    @BeforeEach
    void setUp() {
        inventoryResponse = new InventoryResponseDTO();
        inventoryResponse.setId(1L);

        movementResponse = new InventoryMovementResponseDTO();
        movementResponse.setId(1L);
    }

    @Test
    void createInventory_ShouldReturnInventory() {
        // 🟢 ARRANGE
        InventoryRequestDTO request = new InventoryRequestDTO();
        when(inventoryService.createInventory(any(InventoryRequestDTO.class))).thenReturn(inventoryResponse);

        // 🟢 ACT
        ResponseEntity<InventoryResponseDTO> response = inventoryController.createInventory(request);

        // 🟢 ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(inventoryResponse, response.getBody());
        verify(inventoryService, times(1)).createInventory(request);
    }

    @Test
    void getAllInventory_ShouldReturnList() {
        // 🟢 ARRANGE
        List<InventoryResponseDTO> inventoryList = Arrays.asList(inventoryResponse);
        when(inventoryService.getAllInventory()).thenReturn(inventoryList);

        // 🟢 ACT
        ResponseEntity<List<InventoryResponseDTO>> response = inventoryController.getAllInventory();

        // 🟢 ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(inventoryService, times(1)).getAllInventory();
    }

    @Test
    void getInventoryByWarehouse_ShouldReturnList() {
        // 🟢 ARRANGE
        List<InventoryResponseDTO> inventoryList = Arrays.asList(inventoryResponse);
        when(inventoryService.getInventoryByWarehouse(1L)).thenReturn(inventoryList);

        // 🟢 ACT
        ResponseEntity<List<InventoryResponseDTO>> response = inventoryController.getInventoryByWarehouse(1L);

        // 🟢 ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(inventoryService, times(1)).getInventoryByWarehouse(1L);
    }

    @Test
    void getInventoryByProduct_ShouldReturnList() {
        // 🟢 ARRANGE
        List<InventoryResponseDTO> inventoryList = Arrays.asList(inventoryResponse);
        when(inventoryService.getInventoryByProduct(1L)).thenReturn(inventoryList);

        // 🟢 ACT
        ResponseEntity<List<InventoryResponseDTO>> response = inventoryController.getInventoryByProduct(1L);

        // 🟢 ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(inventoryService, times(1)).getInventoryByProduct(1L);
    }

    @Test
    void getInventoryById_ShouldReturnInventory() {
        // 🟢 ARRANGE
        when(inventoryService.getInventoryById(1L)).thenReturn(inventoryResponse);

        // 🟢 ACT
        ResponseEntity<InventoryResponseDTO> response = inventoryController.getInventoryById(1L);

        // 🟢 ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(inventoryResponse, response.getBody());
        verify(inventoryService, times(1)).getInventoryById(1L);
    }

    @Test
    void updateInventory_ShouldReturnUpdatedInventory() {
        // 🟢 ARRANGE
        InventoryUpdateDTO request = new InventoryUpdateDTO();
        when(inventoryService.updateInventory(1L, request)).thenReturn(inventoryResponse);

        // 🟢 ACT
        ResponseEntity<InventoryResponseDTO> response = inventoryController.updateInventory(1L, request);

        // 🟢 ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(inventoryResponse, response.getBody());
        verify(inventoryService, times(1)).updateInventory(1L, request);
    }

    @Test
    void createMovement_ShouldReturnMovement() {
        // 🟢 ARRANGE
        InventoryMovementRequestDTO request = new InventoryMovementRequestDTO();
        when(inventoryService.createMovement(any(InventoryMovementRequestDTO.class))).thenReturn(movementResponse);

        // 🟢 ACT
        ResponseEntity<InventoryMovementResponseDTO> response = inventoryController.createMovement(request);

        // 🟢 ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(movementResponse, response.getBody());
        verify(inventoryService, times(1)).createMovement(request);
    }

    @Test
    void checkAvailability_WhenAvailable_ShouldReturnTrue() {
        // 🟢 ARRANGE
        when(inventoryService.checkAvailability(1L, 10)).thenReturn(true);

        // 🟢 ACT
        ResponseEntity<ApiResponseDTO> response = inventoryController.checkAvailability(1L, 10);

        // 🟢 ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Product is available", response.getBody().getMessage());
        assertTrue(response.getBody().isSuccess());
        verify(inventoryService, times(1)).checkAvailability(1L, 10);
    }

    @Test
    void checkAvailability_WhenNotAvailable_ShouldReturnFalse() {
        // 🟢 ARRANGE
        when(inventoryService.checkAvailability(1L, 100)).thenReturn(false);

        // 🟢 ACT
        ResponseEntity<ApiResponseDTO> response = inventoryController.checkAvailability(1L, 100);

        // 🟢 ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Insufficient stock", response.getBody().getMessage());
        assertFalse(response.getBody().isSuccess());
    }
}