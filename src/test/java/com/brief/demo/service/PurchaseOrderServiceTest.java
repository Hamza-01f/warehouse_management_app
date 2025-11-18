package com.brief.demo.service;

import com.brief.demo.dto.request.PurchaseOrderRequestDTO;
import com.brief.demo.dto.response.PurchaseOrderResponseDTO;
import com.brief.demo.enums.POStatus;
import com.brief.demo.exception.ResourceNotFoundException;
import com.brief.demo.mappers.PurchaseOrderMapper;
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
class PurchaseOrderServiceTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private POLineRepository poLineRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PurchaseOrderMapper purchaseOrderMapper;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private BackorderRepository backOrderRepository;

    @Mock
    private BackOrderService backOrderService;

    @InjectMocks
    private PurchaseOrderService purchaseOrderService;

    private PurchaseOrder purchaseOrder;
    private Supplier supplier;
    private Product product;
    private Warehouse warehouse;

    @BeforeEach
    void setUp() {
        supplier = Supplier.builder()
                .id(1L)
                .name("Test Supplier")
                .build();

        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(BigDecimal.valueOf(100.00))
                .build();

        warehouse = Warehouse.builder()
                .id(1L)
                .name("Test Warehouse")
                .build();

        purchaseOrder = PurchaseOrder.builder()
                .id(1L)
                .supplier(supplier)
                .status(POStatus.CREATED)
                .build();
    }

    @Test
    void createPurchaseOrder_ShouldCreatePO() {
        // Arrange
        PurchaseOrderRequestDTO request = new PurchaseOrderRequestDTO();
        request.setSupplierId(1L);

        var poLine = new PurchaseOrderRequestDTO.POLineRequestDTO();
        poLine.setProductId(1L);
        poLine.setQuantity(10);
        poLine.setPrice(BigDecimal.valueOf(100.00));
        request.setOrderLines(List.of(poLine));

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(purchaseOrder);
        when(purchaseOrderMapper.toResponse(any(PurchaseOrder.class))).thenReturn(new PurchaseOrderResponseDTO());

        // Act
        PurchaseOrderResponseDTO result = purchaseOrderService.createPurchaseOrder(request);

        // Assert
        assertNotNull(result);
        verify(poLineRepository, times(1)).save(any(POLine.class));
    }

    @Test
    void approvePurchaseOrder_ShouldUpdateStatus() {
        // Arrange
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(purchaseOrder));
        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(purchaseOrder);
        when(purchaseOrderMapper.toResponse(any(PurchaseOrder.class))).thenReturn(new PurchaseOrderResponseDTO());

        // Act
        PurchaseOrderResponseDTO result = purchaseOrderService.approvePurchaseOrder(1L);

        // Assert
        assertNotNull(result);
        assertEquals(POStatus.APPROVED, purchaseOrder.getStatus());
    }

    @Test
    void receivePurchaseOrder_ShouldUpdateInventory() {
        // Arrange
        purchaseOrder.setStatus(POStatus.APPROVED);
        POLine poLine = POLine.builder()
                .id(1L)
                .purchaseOrder(purchaseOrder)
                .product(product)
                .quantity(10)
                .quantityReceived(0)
                .price(BigDecimal.valueOf(100.00))
                .build();

        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(purchaseOrder));
        when(poLineRepository.findByPurchaseOrderId(1L)).thenReturn(List.of(poLine));
        when(warehouseRepository.findByIsActiveTrue()).thenReturn(List.of(warehouse));
        when(inventoryRepository.findByWarehouseIdAndProductId(1L, 1L))
                .thenReturn(Optional.empty());

        // Act
        purchaseOrderService.receivePurchaseOrder(1L);

        // Assert
        verify(inventoryService, times(1)).processInboundMovement(1L, 1L, 10);
        assertEquals(POStatus.RECEIVED, purchaseOrder.getStatus());
    }

    @Test
    void getPurchaseOrderById_WhenExists_ShouldReturnPO() {
        // Arrange
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(purchaseOrder));
        when(purchaseOrderMapper.toResponse(any(PurchaseOrder.class))).thenReturn(new PurchaseOrderResponseDTO());

        // Act
        PurchaseOrderResponseDTO result = purchaseOrderService.getPurchaseOrderById(1L);

        // Assert
        assertNotNull(result);
    }

    @Test
    void getAllPurchaseOrders_ShouldReturnList() {
        // Arrange
        when(purchaseOrderRepository.findAll()).thenReturn(List.of(purchaseOrder));
        when(purchaseOrderMapper.toResponse(any(PurchaseOrder.class))).thenReturn(new PurchaseOrderResponseDTO());

        // Act
        List<PurchaseOrderResponseDTO> result = purchaseOrderService.getAllPurchaseOrders();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}