//package com.brief.demo.service;
//
//import com.brief.demo.dto.request.SalesOrderRequestDTO;
//import com.brief.demo.dto.response.SalesOrderResponseDTO;
//import com.brief.demo.enums.OrderStatus;
//import com.brief.demo.exception.ResourceNotFoundException;
//import com.brief.demo.mappers.SalesOrderMapper;
//import com.brief.demo.model.*;
//import com.brief.demo.repository.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class SalesOrderServiceTest {
//
//    @Mock
//    private SalesOrderRepository salesOrderRepository;
//
//    @Mock
//    private SalesOrderLineRepository salesOrderLineRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private WarehouseRepository warehouseRepository;
//
//    @Mock
//    private ProductRepository productRepository;
//
//    @Mock
//    private InventoryRepository inventoryRepository;
//
//    @Mock
//    private InventoryService inventoryService;
//
//    @Mock
//    private SalesOrderMapper salesOrderMapper;
//
//    @InjectMocks
//    private SalesOrderService salesOrderService;
//
//    private User client;
//    private Warehouse warehouse;
//    private Product product;
//    private SalesOrder salesOrder;
//    private SalesOrderRequestDTO salesOrderRequestDTO;
//    private SalesOrderResponseDTO salesOrderResponseDTO;
//
//    @BeforeEach
//    void setUp() {
//        client = User.builder()
//                .id(1L)
//                .firstName("John")
//                .lastName("Doe")
//                .email("john.doe@example.com")
//                .build();
//
//        warehouse = Warehouse.builder()
//                .id(1L)
//                .name("Main Warehouse")
//                .build();
//
//        product = Product.builder()
//                .id(1L)
//                .name("Test Product")
//                .price(new BigDecimal("99.99"))
//                .build();
//
//        salesOrder = SalesOrder.builder()
//                .id(1L)
//                .client(client)
//                .warehouse(warehouse)
//                .status(OrderStatus.CREATED)
//                .build();
//
//        salesOrderRequestDTO = new SalesOrderRequestDTO();
//        salesOrderRequestDTO.setWarehouseId(1L);
//        salesOrderRequestDTO.setCountry("USA");
//        salesOrderRequestDTO.setCity("New York");
//        salesOrderRequestDTO.setStreet("123 Main St");
//        salesOrderRequestDTO.setZipCode("10001");
//
//        SalesOrderRequestDTO.SalesOrderLineRequestDTO lineRequest = new SalesOrderRequestDTO.SalesOrderLineRequestDTO();
//        lineRequest.setProductId(1L);
//        lineRequest.setQuantity(10);
//        salesOrderRequestDTO.setOrderLines(Arrays.asList(lineRequest));
//
//        salesOrderResponseDTO = new SalesOrderResponseDTO();
//        salesOrderResponseDTO.setId(1L);
//        salesOrderResponseDTO.setStatus(OrderStatus.CREATED);
//    }
//
//    @Test
//    void createSalesOrder_ShouldCreateOrder_WhenValidRequest() {
//        // Arrange
//        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
//        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
//        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
//        when(salesOrderRepository.save(any(SalesOrder.class))).thenReturn(salesOrder);
//        when(salesOrderMapper.toResponse(salesOrder)).thenReturn(salesOrderResponseDTO);
//
//        // Act
//        SalesOrderResponseDTO result = salesOrderService.createSalesOrder(salesOrderRequestDTO, 1L);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(1L, result.getId());
//        verify(salesOrderRepository, times(1)).save(any(SalesOrder.class));
//        verify(salesOrderLineRepository, atLeastOnce()).save(any(SalesOrderLine.class));
//    }
//
//    @Test
//    void createSalesOrder_ShouldThrowException_WhenClientNotFound() {
//        // Arrange
//        when(userRepository.findById(1L)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(ResourceNotFoundException.class, () -> {
//            salesOrderService.createSalesOrder(salesOrderRequestDTO, 1L);
//        });
//    }
//
//    @Test
//    void createSalesOrder_ShouldThrowException_WhenProductNotFound() {
//        // Arrange
//        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
//        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
//        when(productRepository.findById(1L)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(ResourceNotFoundException.class, () -> {
//            salesOrderService.createSalesOrder(salesOrderRequestDTO, 1L);
//        });
//    }
//
//    @Test
//    void reserveStock_ShouldReserveStock_WhenSufficientInventory() {
//        // Arrange
//        salesOrder.setStatus(OrderStatus.CREATED);
//        SalesOrderLine orderLine = SalesOrderLine.builder()
//                .id(1L)
//                .salesOrder(salesOrder)
//                .product(product)
//                .quantity(10)
//                .quantityReserved(0)
//                .build();
//
//        Inventory inventory = Inventory.builder()
//                .id(1L)
//                .product(product)
//                .warehouse(warehouse)
//                .quantityOnHand(50)
//                .quantityReserved(0)
//                .available_quantity(50)
//                .build();
//
//        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(salesOrder));
//        when(salesOrderLineRepository.findBySalesOrderId(1L)).thenReturn(Arrays.asList(orderLine));
//        when(inventoryService.checkAvailability(1L, 10)).thenReturn(true);
//        when(inventoryRepository.findAvailableInventoryForProduct(1L, 10)).thenReturn(Arrays.asList(inventory));
//        when(salesOrderRepository.save(salesOrder)).thenReturn(salesOrder);
//
//        // Act
//        salesOrderService.reserveStock(1L);
//
//        // Assert
//        assertEquals(OrderStatus.RESERVED, salesOrder.getStatus());
//        assertEquals(10, orderLine.getQuantityReserved());
//        verify(inventoryRepository, atLeastOnce()).save(any(Inventory.class));
//    }
//
//    @Test
//    void reserveStock_ShouldHandleBackorder_WhenInsufficientInventory() {
//        // Arrange
//        salesOrder.setStatus(OrderStatus.CREATED);
//        SalesOrderLine orderLine = SalesOrderLine.builder()
//                .id(1L)
//                .salesOrder(salesOrder)
//                .product(product)
//                .quantity(10)
//                .quantityReserved(0)
//                .build();
//
//        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(salesOrder));
//        when(salesOrderLineRepository.findBySalesOrderId(1L)).thenReturn(Arrays.asList(orderLine));
//        when(inventoryService.checkAvailability(1L, 10)).thenReturn(false);
//        when(inventoryRepository.getTotalAvailableQuantityByProductId(1L)).thenReturn(5);
//
//        // Act
//        salesOrderService.reserveStock(1L);
//
//        // Assert
//        assertEquals(OrderStatus.CREATED, salesOrder.getStatus()); // Status remains CREATED
//        assertEquals(5, orderLine.getQuantityReserved()); // Partial reservation
//        assertEquals(5, orderLine.getQuantityOrdered()); // Backordered quantity
//    }
//
//    @Test
//    void reserveStock_ShouldThrowException_WhenOrderNotInCreatedStatus() {
//        // Arrange
//        salesOrder.setStatus(OrderStatus.RESERVED);
//        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(salesOrder));
//
//        // Act & Assert
//        assertThrows(IllegalStateException.class, () -> {
//            salesOrderService.reserveStock(1L);
//        });
//    }
//
//    @Test
//    void getSalesOrderById_ShouldReturnOrder_WhenExists() {
//        // Arrange
//        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(salesOrder));
//        when(salesOrderMapper.toResponse(salesOrder)).thenReturn(salesOrderResponseDTO);
//
//        // Act
//        SalesOrderResponseDTO result = salesOrderService.getSalesOrderById(1L);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(1L, result.getId());
//    }
//
//    @Test
//    void getSalesOrderById_ShouldThrowException_WhenNotFound() {
//        // Arrange
//        when(salesOrderRepository.findById(1L)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(ResourceNotFoundException.class, () -> {
//            salesOrderService.getSalesOrderById(1L);
//        });
//    }
//
//    @Test
//    void cancelSalesOrder_ShouldCancelOrder_WhenValid() {
//        // Arrange
//        salesOrder.setStatus(OrderStatus.RESERVED);
//        SalesOrderLine orderLine = SalesOrderLine.builder()
//                .id(1L)
//                .salesOrder(salesOrder)
//                .product(product)
//                .quantityReserved(10)
//                .build();
//
//        Inventory inventory = Inventory.builder()
//                .id(1L)
//                .product(product)
//                .quantityReserved(10)
//                .build();
//
//        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(salesOrder));
//        when(salesOrderLineRepository.findBySalesOrderId(1L)).thenReturn(Arrays.asList(orderLine));
//        when(inventoryRepository.findByProductId(1L)).thenReturn(Arrays.asList(inventory));
//        when(salesOrderRepository.save(salesOrder)).thenReturn(salesOrder);
//        when(salesOrderMapper.toResponse(salesOrder)).thenReturn(salesOrderResponseDTO);
//
//        // Act
//        SalesOrderResponseDTO result = salesOrderService.cancelSalesOrder(1L);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(OrderStatus.CANCELED, salesOrder.getStatus());
//        verify(inventoryRepository, atLeastOnce()).save(any(Inventory.class));
//    }
//
//    @Test
//    void cancelSalesOrder_ShouldThrowException_WhenAlreadyCanceled() {
//        // Arrange
//        salesOrder.setStatus(OrderStatus.CANCELED);
//        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(salesOrder));
//
//        // Act & Assert
//        assertThrows(IllegalStateException.class, () -> {
//            salesOrderService.cancelSalesOrder(1L);
//        });
//    }
//}