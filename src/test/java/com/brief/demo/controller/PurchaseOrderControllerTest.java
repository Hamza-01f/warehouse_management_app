package com.brief.demo.controller;

import com.brief.demo.dto.request.PurchaseOrderRequestDTO;
import com.brief.demo.dto.request.PurchaseOrderUpdateDTO;
import com.brief.demo.dto.response.PurchaseOrderResponseDTO;
import com.brief.demo.enums.POStatus;
import com.brief.demo.service.PurchaseOrderService;
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
class PurchaseOrderControllerTest {

    @Mock
    private PurchaseOrderService purchaseOrderService;

    @InjectMocks
    private PurchaseOrderController purchaseOrderController;

    private PurchaseOrderResponseDTO purchaseOrderResponse;

    @BeforeEach
    void setUp() {
        purchaseOrderResponse = new PurchaseOrderResponseDTO();
        purchaseOrderResponse.setId(1L);
        purchaseOrderResponse.setStatus(POStatus.CREATED);
    }

    @Test
    void createPurchaseOrder_ShouldReturnPurchaseOrder() {
        //  ARRANGE
        PurchaseOrderRequestDTO request = new PurchaseOrderRequestDTO();
        when(purchaseOrderService.createPurchaseOrder(any(PurchaseOrderRequestDTO.class))).thenReturn(purchaseOrderResponse);

        //  ACT
        ResponseEntity<PurchaseOrderResponseDTO> response = purchaseOrderController.createPurchaseOrder(request);

        //  ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(purchaseOrderResponse, response.getBody());
        verify(purchaseOrderService, times(1)).createPurchaseOrder(request);
    }

    @Test
    void getAllPurchaseOrders_ShouldReturnList() {
        //  ARRANGE
        List<PurchaseOrderResponseDTO> purchaseOrders = Arrays.asList(purchaseOrderResponse);
        when(purchaseOrderService.getAllPurchaseOrders()).thenReturn(purchaseOrders);

        //  ACT
        ResponseEntity<List<PurchaseOrderResponseDTO>> response = purchaseOrderController.getAllPurchaseOrders();

        //  ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(purchaseOrderService, times(1)).getAllPurchaseOrders();
    }

    @Test
    void getPurchaseOrderById_ShouldReturnPurchaseOrder() {
        //  ARRANGE
        when(purchaseOrderService.getPurchaseOrderById(1L)).thenReturn(purchaseOrderResponse);

        // ACT
        ResponseEntity<PurchaseOrderResponseDTO> response = purchaseOrderController.getPurchaseOrderById(1L);

        //  ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(purchaseOrderResponse, response.getBody());
        verify(purchaseOrderService, times(1)).getPurchaseOrderById(1L);
    }

    @Test
    void updatePurchaseOrderStatus_ShouldReturnUpdatedPurchaseOrder() {
        // ARRANGE
        PurchaseOrderUpdateDTO request = new PurchaseOrderUpdateDTO();
        request.setStatus(POStatus.APPROVED);
        when(purchaseOrderService.updatePurchaseOrderStatus(1L, request)).thenReturn(purchaseOrderResponse);

        //  ACT
        ResponseEntity<PurchaseOrderResponseDTO> response = purchaseOrderController.updatePurchaseOrderStatus(1L, request);

        //  ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(purchaseOrderResponse, response.getBody());
        verify(purchaseOrderService, times(1)).updatePurchaseOrderStatus(1L, request);
    }

    @Test
    void approvePurchaseOrder_ShouldReturnApprovedPurchaseOrder() {
        //  ARRANGE
        when(purchaseOrderService.approvePurchaseOrder(1L)).thenReturn(purchaseOrderResponse);

        //  ACT
        ResponseEntity<PurchaseOrderResponseDTO> response = purchaseOrderController.approvePurchaseOrder(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(purchaseOrderResponse, response.getBody());
        verify(purchaseOrderService, times(1)).approvePurchaseOrder(1L);
    }
}