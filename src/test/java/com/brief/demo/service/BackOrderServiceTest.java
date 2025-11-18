package com.brief.demo.service;

import com.brief.demo.dto.response.BackOrderResponseDTO;
import com.brief.demo.enums.BackOrderStatus;
import com.brief.demo.exception.ResourceNotFoundException;
import com.brief.demo.mappers.BackOrderMapper;
import com.brief.demo.model.*;
import com.brief.demo.repository.BackorderRepository;
import com.brief.demo.repository.SalesOrderLineRepository;
import com.brief.demo.repository.SupplierRepository;
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
class BackOrderServiceTest {

    @Mock
    private BackorderRepository backorderRepository;

    @Mock
    private BackOrderMapper backOrderMapper;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private SalesOrderLineRepository salesOrderLineRepository;

    @Mock
    private PurchaseOrderService purchaseOrderService;

    @InjectMocks
    private BackOrderService backOrderService;

    private BackOrder backOrder;
    private SalesOrderLine salesOrderLine;
    private Warehouse warehouse;
    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(BigDecimal.valueOf(100.00))
                .build();

        warehouse = Warehouse.builder()
                .id(1L)
                .name("Test Warehouse")
                .build();

        salesOrderLine = SalesOrderLine.builder()
                .id(1L)
                .product(product)
                .quantity(10)
                .quantityReserved(0)
                .backorderQuantity(5)
                .build();

        backOrder = BackOrder.builder()
                .id(1L)
                .salesOrderLine(salesOrderLine)
                .product(product)
                .warehouse(warehouse)
                .quantity(5)
                .status(BackOrderStatus.PENDING)
                .autoPurchaseOrder(true)
                .build();
    }

    @Test
    void createBackOrder_ShouldCreateAndSaveBackOrder() {
        // Arrange
        when(backorderRepository.save(any(BackOrder.class))).thenReturn(backOrder);

        // Act
        backOrderService.createBackOrder(salesOrderLine, warehouse, 5);

        // Assert
        verify(backorderRepository, times(1)).save(any(BackOrder.class));
    }

    @Test
    void activateBackOrder_WhenBackOrderNotFound_ShouldThrowException() {
        // Arrange
        when(backorderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                backOrderService.activateBackOrder(1L));
    }

    @Test
    void activateBackOrder_WhenNotPending_ShouldThrowException() {
        // Arrange
        backOrder.setStatus(BackOrderStatus.FULFILLED);
        when(backorderRepository.findById(1L)).thenReturn(Optional.of(backOrder));

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                backOrderService.activateBackOrder(1L));
    }

    @Test
    void activateBackOrder_WithAutoPurchase_ShouldCreatePurchaseOrder() {
        // Arrange
        when(backorderRepository.findById(1L)).thenReturn(Optional.of(backOrder));
        when(supplierRepository.findByIsActiveTrue()).thenReturn(List.of(
                Supplier.builder().id(1L).name("Test Supplier").build()
        ));
        when(backorderRepository.save(any(BackOrder.class))).thenReturn(backOrder);
        when(backOrderMapper.toResponse(any(BackOrder.class))).thenReturn(new BackOrderResponseDTO());

        // Act
        BackOrderResponseDTO result = backOrderService.activateBackOrder(1L);

        // Assert
        assertNotNull(result);
        verify(purchaseOrderService, times(1)).createPurchaseOrder(any());
    }

    @Test
    void fulfillBackOrder_ShouldUpdateInventoryAndStatus() {
        // Arrange
        when(backorderRepository.findById(1L)).thenReturn(Optional.of(backOrder));
        when(salesOrderLineRepository.save(any(SalesOrderLine.class))).thenReturn(salesOrderLine);
        when(backorderRepository.save(any(BackOrder.class))).thenReturn(backOrder);
        when(backOrderMapper.toResponse(any(BackOrder.class))).thenReturn(new BackOrderResponseDTO());

        // Act
        BackOrderResponseDTO result = backOrderService.fulfillBackOrder(1L);

        // Assert
        assertNotNull(result);
        verify(inventoryService, times(1)).processInboundMovement(1L, 1L, 5);
        verify(salesOrderLineRepository, times(1)).save(any(SalesOrderLine.class));
    }

    @Test
    void cancelBackOrder_ShouldUpdateStatus() {
        // Arrange
        when(backorderRepository.findById(1L)).thenReturn(Optional.of(backOrder));
        when(backorderRepository.save(any(BackOrder.class))).thenReturn(backOrder);
        when(backOrderMapper.toResponse(any(BackOrder.class))).thenReturn(new BackOrderResponseDTO());

        // Act
        BackOrderResponseDTO result = backOrderService.cancelBackOrder(1L);

        // Assert
        assertNotNull(result);
        assertEquals(BackOrderStatus.CANCELLED, backOrder.getStatus());
    }

    @Test
    void getAllBackOrders_ShouldReturnList() {
        // Arrange
        when(backorderRepository.findAll()).thenReturn(List.of(backOrder));
        when(backOrderMapper.toResponse(any(BackOrder.class))).thenReturn(new BackOrderResponseDTO());

        // Act
        List<BackOrderResponseDTO> result = backOrderService.getAllBackOrders();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}