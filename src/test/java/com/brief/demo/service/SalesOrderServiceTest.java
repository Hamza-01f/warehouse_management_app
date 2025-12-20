package com.brief.demo.service;

import com.brief.demo.dto.request.SalesOrderRequestDTO;
import com.brief.demo.dto.response.SalesOrderResponseDTO;
import com.brief.demo.enums.OrderStatus;
import com.brief.demo.exception.ResourceNotFoundException;
import com.brief.demo.mappers.SalesOrderMapper;
import com.brief.demo.model.*;
import com.brief.demo.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalesOrderServiceTest {

    @Mock
    private SalesOrderRepository salesOrderRepository;

    @Mock
    private SalesOrderLineRepository salesOrderLineRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private SalesOrderMapper salesOrderMapper;

    @Mock
    private BackOrderService backOrderService;

    @Mock
    private ShipmentService shipmentService;

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private SalesOrderService salesOrderService;

    private SalesOrder salesOrder;
    private User client;
    private Warehouse warehouse;
    private Product product;

    @BeforeEach
    void setUp() {
        client = User.builder()
                .id(1L)
                .email("client@test.com")
                .build();

        warehouse = Warehouse.builder()
                .id(1L)
                .name("Test Warehouse")
                .build();

        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(BigDecimal.valueOf(100.0))
                .build();

        salesOrder = SalesOrder.builder()
                .id(1L)
                .client(client)
                .warehouse(warehouse)
                .status(OrderStatus.CREATED)
                .build();
    }

//    @Test
//    void createSalesOrder_ShouldCreateOrder() {
//        // Arrange
//        SalesOrderRequestDTO request = new SalesOrderRequestDTO();
//        request.setWarehouseId(1L);
//
//        var lineRequest = new SalesOrderRequestDTO.SalesOrderLineRequestDTO();
//        lineRequest.setProductId(1L);
//        lineRequest.setQuantity(5);
//        request.setOrderLines(List.of(lineRequest));
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
//        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
//        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
//        when(salesOrderRepository.save(any(SalesOrder.class))).thenReturn(salesOrder);
//        when(salesOrderMapper.toResponse(any(SalesOrder.class))).thenReturn(new SalesOrderResponseDTO());
//
//        // Act
//        SalesOrderResponseDTO result = salesOrderService.createSalesOrder(request, 1L);
//
//        // Assert
//        assertNotNull(result);
//        verify(salesOrderLineRepository, times(1)).save(any(SalesOrderLine.class));
//    }

    @Test
    void reserveStock_WhenSufficientStock_ShouldReserve() {
        // Arrange
        SalesOrderLine orderLine = SalesOrderLine.builder()
                .id(1L)
                .product(product)
                .quantity(5)
                .quantityReserved(0)
                .build();

        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(salesOrder));
        when(salesOrderLineRepository.findBySalesOrderId(1L)).thenReturn(List.of(orderLine));
        when(inventoryRepository.getTotalAvailableQuantityByProductId(1L)).thenReturn(10);
        when(inventoryService.reserveStockForOrder(any(SalesOrderLine.class), any(Warehouse.class)))
                .thenReturn(true);

        // Act
        salesOrderService.reserveStock(1L);

        // Assert
        verify(inventoryService, atLeastOnce()).reserveStockForOrder(any(), any());
    }

    @Test
    void shipOrder_ShouldUpdateStatus() {
        // Arrange
        salesOrder.setStatus(OrderStatus.RESERVED);
        SalesOrderLine orderLine = SalesOrderLine.builder()
                .id(1L)
                .product(product)
                .quantity(5)
                .quantityReserved(5)
                .quantityFulfilled(0)
                .build();

        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(salesOrder));
        when(salesOrderLineRepository.findBySalesOrderId(1L)).thenReturn(List.of(orderLine));
        when(salesOrderRepository.save(any(SalesOrder.class))).thenReturn(salesOrder);
        when(salesOrderMapper.toResponse(any(SalesOrder.class))).thenReturn(new SalesOrderResponseDTO());

        // Act
        SalesOrderResponseDTO result = salesOrderService.shipOrder(1L);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.SHIPPED, salesOrder.getStatus());
        verify(inventoryService, times(1)).processOutboundMovement(1L, 1L, 5);
    }

    @Test
    void cancelSalesOrder_ShouldCancelOrder() {
        // Arrange
        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(salesOrder));
        when(salesOrderRepository.save(any(SalesOrder.class))).thenReturn(salesOrder);
        when(salesOrderMapper.toResponse(any(SalesOrder.class))).thenReturn(new SalesOrderResponseDTO());

        // Act
        SalesOrderResponseDTO result = salesOrderService.cancelSalesOrder(1L);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.CANCELED, salesOrder.getStatus());
        verify(backOrderService, times(1)).cancelBackOrdersForOrder(1L);
    }

    @Test
    void getSalesOrderById_WhenExists_ShouldReturnOrder() {
        // Arrange
        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(salesOrder));
        when(salesOrderMapper.toResponse(any(SalesOrder.class))).thenReturn(new SalesOrderResponseDTO());

        // Act
        SalesOrderResponseDTO result = salesOrderService.getSalesOrderById(1L);

        // Assert
        assertNotNull(result);
    }
}