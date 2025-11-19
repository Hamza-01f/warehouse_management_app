package com.brief.demo.service;

import com.brief.demo.dto.request.ShipmentRequestDTO;
import com.brief.demo.dto.response.ShipmentResponseDTO;
import com.brief.demo.enums.OrderStatus;
import com.brief.demo.enums.ShipmentStatus;
import com.brief.demo.exception.ResourceNotFoundException;
import com.brief.demo.model.SalesOrder;
import com.brief.demo.model.Shipment;
import com.brief.demo.repository.SalesOrderRepository;
import com.brief.demo.repository.ShipmentRepository;
import org.junit.jupiter.api.BeforeEach;
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
class ShipmentServiceTest {

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private SalesOrderRepository salesOrderRepository;

    @InjectMocks
    private ShipmentService shipmentService;

    private ShipmentRequestDTO shipmentRequest;
    private SalesOrder salesOrder;
    private Shipment shipment;

    @BeforeEach
    void setUp() {
        salesOrder = new SalesOrder();
        salesOrder.setId(1L);
        salesOrder.setStatus(OrderStatus.RESERVED);

        shipment = Shipment.builder()
                .id(1L)
                .salesOrder(salesOrder)
                .trackingNumber("TRK123456")
                .status(ShipmentStatus.PLANNED)
                .build();

        shipmentRequest = new ShipmentRequestDTO();
        shipmentRequest.setSalesOrderId(1L);
        shipmentRequest.setTrackingNumber("TRK123456");
    }

    @Test
    void createShipment_ShouldCreateShipmentSuccessfully() {
        // 🟢 ARRANGE
        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(salesOrder));
        when(shipmentRepository.findBySalesOrderId(1L)).thenReturn(Optional.empty());
        when(shipmentRepository.save(any(Shipment.class))).thenReturn(shipment);

        // 🟢 ACT
        ShipmentResponseDTO result = shipmentService.createShipment(shipmentRequest);

        // 🟢 ASSERT
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("TRK123456", result.getTrackingNumber());
        assertEquals(ShipmentStatus.PLANNED, result.getStatus());

        verify(salesOrderRepository, times(1)).findById(1L);
        verify(shipmentRepository, times(1)).findBySalesOrderId(1L);
        verify(shipmentRepository, times(1)).save(any(Shipment.class));
    }

    @Test
    void createShipment_WhenSalesOrderNotFound_ShouldThrowResourceNotFoundException() {
        // 🟢 ARRANGE
        when(salesOrderRepository.findById(1L)).thenReturn(Optional.empty());

        // 🟢 ACT & ASSERT
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> shipmentService.createShipment(shipmentRequest));

        assertEquals("Sales order not found", exception.getMessage());
        verify(shipmentRepository, never()).save(any(Shipment.class));
    }

    @Test
    void createShipment_WhenOrderNotReserved_ShouldThrowIllegalStateException() {
        // 🟢 ARRANGE
        salesOrder.setStatus(OrderStatus.CREATED);
        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(salesOrder));

        // 🟢 ACT & ASSERT
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> shipmentService.createShipment(shipmentRequest));

        assertEquals("Cannot create shipment for order in status: CREATED", exception.getMessage());
        verify(shipmentRepository, never()).save(any(Shipment.class));
    }

    @Test
    void createShipment_WhenShipmentAlreadyExists_ShouldThrowIllegalStateException() {
        // 🟢 ARRANGE
        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(salesOrder));
        when(shipmentRepository.findBySalesOrderId(1L)).thenReturn(Optional.of(shipment));

        // 🟢 ACT & ASSERT
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> shipmentService.createShipment(shipmentRequest));

        assertEquals("Shipment already exists for this order", exception.getMessage());
        verify(shipmentRepository, never()).save(any(Shipment.class));
    }

    @Test
    void createShipmentForOrder_ShouldCreateShipment() {
        // 🟢 ARRANGE
        when(shipmentRepository.save(any(Shipment.class))).thenReturn(shipment);

        // 🟢 ACT
        shipmentService.createShipmentForOrder(salesOrder);

        // 🟢 ASSERT
        verify(shipmentRepository, times(1)).save(any(Shipment.class));
    }

    @Test
    void updateShipmentStatus_ShouldUpdateStatusSuccessfully() {
        // 🟢 ARRANGE
        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));
        when(shipmentRepository.save(any(Shipment.class))).thenReturn(shipment);

        // 🟢 ACT
        ShipmentResponseDTO result = shipmentService.updateShipmentStatus(1L, ShipmentStatus.IN_TRANSIT);

        // 🟢 ASSERT
        assertNotNull(result);
        assertEquals(ShipmentStatus.IN_TRANSIT, shipment.getStatus());
        verify(shipmentRepository, times(1)).findById(1L);
        verify(shipmentRepository, times(1)).save(shipment);
    }

    @Test
    void updateShipmentStatus_WhenShipmentNotFound_ShouldThrowResourceNotFoundException() {
        // 🟢 ARRANGE
        when(shipmentRepository.findById(1L)).thenReturn(Optional.empty());

        // 🟢 ACT & ASSERT
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> shipmentService.updateShipmentStatus(1L, ShipmentStatus.IN_TRANSIT));

        assertEquals("Shipment not found", exception.getMessage());
        verify(shipmentRepository, never()).save(any(Shipment.class));
    }

    @Test
    void updateShipmentStatusByOrder_ShouldUpdateStatusSuccessfully() {
        // 🟢 ARRANGE
        when(shipmentRepository.findBySalesOrderId(1L)).thenReturn(Optional.of(shipment));
        when(shipmentRepository.save(any(Shipment.class))).thenReturn(shipment);

        // 🟢 ACT
        shipmentService.updateShipmentStatusByOrder(1L, ShipmentStatus.DELIVERED);

        // 🟢 ASSERT
        assertEquals(ShipmentStatus.DELIVERED, shipment.getStatus());
        verify(shipmentRepository, times(1)).findBySalesOrderId(1L);
        verify(shipmentRepository, times(1)).save(shipment);
    }

    @Test
    void updateShipmentStatusByOrder_WhenShipmentNotFound_ShouldThrowResourceNotFoundException() {
        // 🟢 ARRANGE
        when(shipmentRepository.findBySalesOrderId(1L)).thenReturn(Optional.empty());

        // 🟢 ACT & ASSERT
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> shipmentService.updateShipmentStatusByOrder(1L, ShipmentStatus.DELIVERED));

        assertEquals("Shipment not found for order", exception.getMessage());
        verify(shipmentRepository, never()).save(any(Shipment.class));
    }

    @Test
    void getShipmentById_ShouldReturnShipment() {
        // 🟢 ARRANGE
        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));

        // 🟢 ACT
        ShipmentResponseDTO result = shipmentService.getShipmentById(1L);

        // 🟢 ASSERT
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("TRK123456", result.getTrackingNumber());
        assertEquals(ShipmentStatus.PLANNED, result.getStatus());
        verify(shipmentRepository, times(1)).findById(1L);
    }

    @Test
    void getShipmentById_WhenNotFound_ShouldThrowResourceNotFoundException() {
        // 🟢 ARRANGE
        when(shipmentRepository.findById(1L)).thenReturn(Optional.empty());

        // 🟢 ACT & ASSERT
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> shipmentService.getShipmentById(1L));

        assertEquals("Shipment not found", exception.getMessage());
    }

    @Test
    void getShipmentsByStatus_ShouldReturnFilteredShipments() {
        // 🟢 ARRANGE
        List<Shipment> shipments = List.of(shipment);
        when(shipmentRepository.findByStatus("PLANNED")).thenReturn(shipments);

        // 🟢 ACT
        List<ShipmentResponseDTO> result = shipmentService.getShipmentsByStatus(ShipmentStatus.PLANNED);

        // 🟢 ASSERT
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(shipmentRepository, times(1)).findByStatus("PLANNED");
    }

    @Test
    void getShipmentsByStatus_WhenNoShipments_ShouldReturnEmptyList() {
        // 🟢 ARRANGE
        when(shipmentRepository.findByStatus("DELIVERED")).thenReturn(List.of());

        // 🟢 ACT
        List<ShipmentResponseDTO> result = shipmentService.getShipmentsByStatus(ShipmentStatus.DELIVERED);

        // 🟢 ASSERT
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(shipmentRepository, times(1)).findByStatus("DELIVERED");
    }

    @Test
    void getShipmentByOrder_ShouldReturnShipment() {
        // 🟢 ARRANGE
        when(shipmentRepository.findBySalesOrderId(1L)).thenReturn(Optional.of(shipment));

        // 🟢 ACT
        ShipmentResponseDTO result = shipmentService.getShipmentByOrder(1L);

        // 🟢 ASSERT
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("TRK123456", result.getTrackingNumber());
        verify(shipmentRepository, times(1)).findBySalesOrderId(1L);
    }

    @Test
    void getShipmentByOrder_WhenNotFound_ShouldThrowResourceNotFoundException() {
        // 🟢 ARRANGE
        when(shipmentRepository.findBySalesOrderId(1L)).thenReturn(Optional.empty());

        // 🟢 ACT & ASSERT
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> shipmentService.getShipmentByOrder(1L));

        assertEquals("Shipment not found for order", exception.getMessage());
    }

    @Test
    void createShipmentForOrder_ShouldGenerateTrackingNumber() {
        // 🟢 ARRANGE
        when(shipmentRepository.save(any(Shipment.class))).thenAnswer(invocation -> {
            Shipment savedShipment = invocation.getArgument(0);
            assertNotNull(savedShipment.getTrackingNumber());
            assertTrue(savedShipment.getTrackingNumber().startsWith("TRK-"));
            return savedShipment;
        });

        // 🟢 ACT
        shipmentService.createShipmentForOrder(salesOrder);

        // 🟢 ASSERT
        verify(shipmentRepository, times(1)).save(any(Shipment.class));
    }

    @Test
    void createShipment_WithAllShipmentStatuses() {
        // 🟢 ARRANGE
        ShipmentStatus[] statuses = {
                ShipmentStatus.PLANNED,
                ShipmentStatus.IN_TRANSIT,
                ShipmentStatus.DELIVERED
        };

        for (ShipmentStatus status : statuses) {
            shipment.setStatus(status);
            when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(salesOrder));
            when(shipmentRepository.findBySalesOrderId(1L)).thenReturn(Optional.empty());
            when(shipmentRepository.save(any(Shipment.class))).thenReturn(shipment);

            // 🟢 ACT
            ShipmentResponseDTO result = shipmentService.createShipment(shipmentRequest);

            // 🟢 ASSERT
            assertNotNull(result);
            reset(salesOrderRepository, shipmentRepository);
        }
    }

    @Test
    void updateShipmentStatus_WithAllStatusTransitions() {
        // 🟢 ARRANGE
        ShipmentStatus[] newStatuses = {
                ShipmentStatus.IN_TRANSIT,
                ShipmentStatus.DELIVERED,
                ShipmentStatus.PLANNED
        };

        for (ShipmentStatus newStatus : newStatuses) {
            shipment.setStatus(ShipmentStatus.PLANNED);
            when(shipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));
            when(shipmentRepository.save(any(Shipment.class))).thenReturn(shipment);

            // 🟢 ACT
            ShipmentResponseDTO result = shipmentService.updateShipmentStatus(1L, newStatus);

            // 🟢 ASSERT
            assertNotNull(result);
            assertEquals(newStatus, shipment.getStatus());
            reset(shipmentRepository);
        }
    }

    @Test
    void responseMapping_ShouldWorkCorrectlyThroughPublicMethods() {
        // 🟢 ARRANGE
        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));

        // 🟢 ACT
        ShipmentResponseDTO result = shipmentService.getShipmentById(1L);

        // 🟢 ASSERT - Test that the mapping works correctly through public methods
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("TRK123456", result.getTrackingNumber());
        assertEquals(ShipmentStatus.PLANNED, result.getStatus());
    }

    @Test
    void createShipment_WithCustomTrackingNumber() {
        // 🟢 ARRANGE
        shipmentRequest.setTrackingNumber("CUSTOM-TRK-123");
        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(salesOrder));
        when(shipmentRepository.findBySalesOrderId(1L)).thenReturn(Optional.empty());
        when(shipmentRepository.save(any(Shipment.class))).thenReturn(shipment);

        // 🟢 ACT
        ShipmentResponseDTO result = shipmentService.createShipment(shipmentRequest);

        // 🟢 ASSERT
        assertNotNull(result);
        // The tracking number from the request should be used
        verify(shipmentRepository, times(1)).save(any(Shipment.class));
    }
}