package com.brief.demo.controller;

import com.brief.demo.dto.response.BackOrderResponseDTO;
import com.brief.demo.enums.BackOrderStatus;
import com.brief.demo.service.BackOrderService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BackOrderControllerTest {

    @Mock
    private BackOrderService backOrderService;

    @InjectMocks
    private BackOrderController backOrderController;

    private BackOrderResponseDTO backOrderResponse;

    @BeforeEach
    void setUp() {
        backOrderResponse = new BackOrderResponseDTO();
        backOrderResponse.setId(1L);
        backOrderResponse.setStatus(BackOrderStatus.PENDING);
    }

    @Test
    void getAllBackOrders_ShouldReturnList() {
        //  ARRANGE
        List<BackOrderResponseDTO> backOrders = Arrays.asList(backOrderResponse);
        when(backOrderService.getAllBackOrders()).thenReturn(backOrders);

        //  ACT
        ResponseEntity<List<BackOrderResponseDTO>> response = backOrderController.getAllBackOrders();

        //  ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(backOrderService, times(1)).getAllBackOrders();
    }

    @Test
    void getBackOrdersByStatus_ShouldReturnFilteredList() {
        //  ARRANGE
        List<BackOrderResponseDTO> backOrders = Arrays.asList(backOrderResponse);
        when(backOrderService.getBackOrdersByStatus(BackOrderStatus.PENDING)).thenReturn(backOrders);

        //  ACT
        ResponseEntity<List<BackOrderResponseDTO>> response =
                backOrderController.getBackOrdersByStatus("PENDING");

        //  ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(backOrderService, times(1)).getBackOrdersByStatus(BackOrderStatus.PENDING);
    }

    @Test
    void fulfillBackOrder_ShouldReturnBackOrder() {
        //  ARRANGE
        when(backOrderService.fulfillBackOrder(1L)).thenReturn(backOrderResponse);

        //  ACT
        ResponseEntity<BackOrderResponseDTO> response = backOrderController.fulfillBackOrder(1L);

        //  ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(backOrderResponse, response.getBody());
        verify(backOrderService, times(1)).fulfillBackOrder(1L);
    }

    @Test
    void cancelBackOrder_ShouldReturnBackOrder() {
        //  ARRANGE
        when(backOrderService.cancelBackOrder(1L)).thenReturn(backOrderResponse);

        //  ACT
        ResponseEntity<BackOrderResponseDTO> response = backOrderController.cancelBackOrder(1L);

        //  ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(backOrderResponse, response.getBody());
        verify(backOrderService, times(1)).cancelBackOrder(1L);
    }

    @Test
    void processPendingBackOrders_ShouldReturnSuccessMessage() {
        //  ARRANGE
        doNothing().when(backOrderService).processPendingBackOrders();

        //  ACT
        ResponseEntity<String> response = backOrderController.processPendingBackOrders();

        //  ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Pending backorders processed successfully", response.getBody());
        verify(backOrderService, times(1)).processPendingBackOrders();
    }

    @Test
    void activateBackOrder_ShouldReturnBackOrder() {
        //  ARRANGE
        when(backOrderService.activateBackOrder(1L)).thenReturn(backOrderResponse);

        //  ACT
        ResponseEntity<BackOrderResponseDTO> response = backOrderController.activateBackOrder(1L);

        //  ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(backOrderResponse, response.getBody());
        verify(backOrderService, times(1)).activateBackOrder(1L);
    }
}