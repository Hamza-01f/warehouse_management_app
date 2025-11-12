package com.brief.demo.service;

import com.brief.demo.dto.request.SalesOrderRequestDTO;
import com.brief.demo.dto.response.SalesOrderResponseDTO;
import com.brief.demo.enums.OrderStatus;
import com.brief.demo.exception.ResourceNotFoundException;
import com.brief.demo.mappers.SalesOrderMapper;
import com.brief.demo.model.*;
import com.brief.demo.repository.*;
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
class SalesOrderServiceTest {

    @Mock private SalesOrderRepository salesOrderRepository;
    @Mock private SalesOrderLineRepository salesOrderLineRepository;
    @Mock private UserRepository userRepository;
    @Mock private WarehouseRepository warehouseRepository;
    @Mock private ProductRepository productRepository;
    @Mock private InventoryService inventoryService;
    @Mock private SalesOrderMapper salesOrderMapper;
    @Mock private BackOrderService backOrderService;
    @Mock private ShipmentService shipmentService;

    @InjectMocks private SalesOrderService salesOrderService;

    @Test
    void createSalesOrder_Success() {
        // Given
        SalesOrderRequestDTO request = new SalesOrderRequestDTO();
        request.setWarehouseId(1L);
        request.setOrderLines(List.of());

        User client = new User();
        Warehouse warehouse = new Warehouse();
        SalesOrder salesOrder = new SalesOrder();

        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(salesOrderRepository.save(any())).thenReturn(salesOrder);
        when(salesOrderMapper.toResponse(salesOrder)).thenReturn(new SalesOrderResponseDTO());

        // When
        SalesOrderResponseDTO result = salesOrderService.createSalesOrder(request, 1L);

        // Then
        assertNotNull(result);
        verify(salesOrderRepository).save(any(SalesOrder.class));
    }

    @Test
    void shipOrder_Success() {
        // Given
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setStatus(OrderStatus.RESERVED);
        salesOrder.setWarehouse(new Warehouse());

        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(salesOrder));
        when(salesOrderLineRepository.findBySalesOrderId(1L)).thenReturn(List.of());
        when(salesOrderRepository.save(any())).thenReturn(salesOrder);
        when(salesOrderMapper.toResponse(salesOrder)).thenReturn(new SalesOrderResponseDTO());

        // When
        SalesOrderResponseDTO result = salesOrderService.shipOrder(1L);

        // Then
        assertEquals(OrderStatus.SHIPPED, salesOrder.getStatus());
        assertNotNull(result);
    }

    @Test
    void cancelSalesOrder_Success() {
        // Given
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setStatus(OrderStatus.RESERVED);

        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(salesOrder));
        when(salesOrderLineRepository.findBySalesOrderId(1L)).thenReturn(List.of());
        when(salesOrderRepository.save(any())).thenReturn(salesOrder);
        when(salesOrderMapper.toResponse(salesOrder)).thenReturn(new SalesOrderResponseDTO());

        // When
        SalesOrderResponseDTO result = salesOrderService.cancelSalesOrder(1L);

        // Then
        assertEquals(OrderStatus.CANCELED, salesOrder.getStatus());
        assertNotNull(result);
    }
}