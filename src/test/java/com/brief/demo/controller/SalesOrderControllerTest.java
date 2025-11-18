package com.brief.demo.controller;

import com.brief.demo.dto.request.SalesOrderRequestDTO;
import com.brief.demo.dto.response.ApiResponseDTO;
import com.brief.demo.dto.response.SalesOrderResponseDTO;
import com.brief.demo.service.SalesOrderService;
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
class SalesOrderControllerTest {

    @Mock
    private SalesOrderService salesOrderService;

    @InjectMocks
    private SalesOrderController salesOrderController;

    private SalesOrderResponseDTO salesOrderResponse;

    @BeforeEach
    void setUp() {
        salesOrderResponse = new SalesOrderResponseDTO();
        salesOrderResponse.setId(1L);
    }

    @Test
    void createSalesOrder_ShouldReturnSalesOrder() {
        // ARRANGE
        SalesOrderRequestDTO request = new SalesOrderRequestDTO();
        when(salesOrderService.createSalesOrder(any(SalesOrderRequestDTO.class), eq(1L))).thenReturn(salesOrderResponse);

        // ACT
        ResponseEntity<SalesOrderResponseDTO> response = salesOrderController.createSalesOrder(request, 1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(salesOrderResponse, response.getBody());
        verify(salesOrderService, times(1)).createSalesOrder(request, 1L);
    }

    @Test
    void getAllSalesOrders_ShouldReturnList() {
        // ARRANGE
        List<SalesOrderResponseDTO> salesOrders = Arrays.asList(salesOrderResponse);
        when(salesOrderService.getAllSalesOrders()).thenReturn(salesOrders);

        // ACT
        ResponseEntity<List<SalesOrderResponseDTO>> response = salesOrderController.getAllSalesOrders();

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(salesOrderService, times(1)).getAllSalesOrders();
    }

    @Test
    void getSalesOrderById_ShouldReturnSalesOrder() {
        // ARRANGE
        when(salesOrderService.getSalesOrderById(1L)).thenReturn(salesOrderResponse);

        // ACT
        ResponseEntity<SalesOrderResponseDTO> response = salesOrderController.getSalesOrderById(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(salesOrderResponse, response.getBody());
        verify(salesOrderService, times(1)).getSalesOrderById(1L);
    }

    @Test
    void reserveStock_ShouldReturnSuccessMessage() {
        // ARRANGE
        doNothing().when(salesOrderService).reserveStock(1L);

        // ACT
        ResponseEntity<ApiResponseDTO> response = salesOrderController.reserveStock(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Stock reservation processed", response.getBody().getMessage());
        assertTrue(response.getBody().isSuccess());
        verify(salesOrderService, times(1)).reserveStock(1L);
    }

    @Test
    void shipOrder_ShouldReturnSalesOrder() {
        // ARRANGE
        when(salesOrderService.shipOrder(1L)).thenReturn(salesOrderResponse);

        // ACT
        ResponseEntity<SalesOrderResponseDTO> response = salesOrderController.shipOrder(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(salesOrderResponse, response.getBody());
        verify(salesOrderService, times(1)).shipOrder(1L);
    }

//    @Test
//    void cancelSalesOrder_ShouldReturnSuccessMessage() {
//        // ARRANGE
//        doNothing().when(salesOrderService).cancelSalesOrder(1L);
//
//        // ACT
//        ResponseEntity<ApiResponseDTO> response = salesOrderController.cancelSalesOrder(1L);
//
//        // ASSERT
//        assertNotNull(response);
//        assertEquals(200, response.getStatusCodeValue());
//        assertEquals("Order canceled successfully", response.getBody().getMessage());
//        assertTrue(response.getBody().isSuccess());
//        verify(salesOrderService, times(1)).cancelSalesOrder(1L);
//    }
}