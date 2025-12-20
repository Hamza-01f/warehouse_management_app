package com.brief.demo.controller;

import com.brief.demo.dto.request.ShipmentRequestDTO;
import com.brief.demo.dto.response.ShipmentResponseDTO;
import com.brief.demo.enums.ShipmentStatus;
import com.brief.demo.service.ShipmentService;
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
class ShipmentControllerTest {

    @Mock
    private ShipmentService shipmentService;

    @InjectMocks
    private ShipmentController shipmentController;

    private ShipmentResponseDTO shipmentResponse;

    @BeforeEach
    void setUp() {
        shipmentResponse = new ShipmentResponseDTO();
        shipmentResponse.setId(1L);
        shipmentResponse.setTrackingNumber("TRK123");
        shipmentResponse.setStatus(ShipmentStatus.PLANNED);
    }

    @Test
    void createShipment_ShouldReturnShipment() {
        // ARRANGE
        ShipmentRequestDTO request = new ShipmentRequestDTO();
        when(shipmentService.createShipment(any(ShipmentRequestDTO.class))).thenReturn(shipmentResponse);

        // ACT
        ResponseEntity<ShipmentResponseDTO> response = shipmentController.createShipment(request);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(shipmentResponse, response.getBody());
        verify(shipmentService, times(1)).createShipment(request);
    }

    @Test
    void getAllShipments_ShouldReturnList() {
        // ARRANGE
        List<ShipmentResponseDTO> shipments = Arrays.asList(shipmentResponse);
        when(shipmentService.getShipmentsByStatus(null)).thenReturn(shipments);

        // ACT
        ResponseEntity<List<ShipmentResponseDTO>> response = shipmentController.getAllShipments();

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(shipmentService, times(1)).getShipmentsByStatus(null);
    }

    @Test
    void getShipmentById_ShouldReturnShipment() {
        // ARRANGE
        when(shipmentService.getShipmentById(1L)).thenReturn(shipmentResponse);

        // ACT
        ResponseEntity<ShipmentResponseDTO> response = shipmentController.getShipmentById(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(shipmentResponse, response.getBody());
        verify(shipmentService, times(1)).getShipmentById(1L);
    }

    @Test
    void updateShipmentStatus_ShouldReturnUpdatedShipment() {
        // ARRANGE
        when(shipmentService.updateShipmentStatus(1L, ShipmentStatus.IN_TRANSIT)).thenReturn(shipmentResponse);

        // ACT
        ResponseEntity<ShipmentResponseDTO> response = shipmentController.updateShipmentStatus(1L, ShipmentStatus.IN_TRANSIT);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(shipmentResponse, response.getBody());
        verify(shipmentService, times(1)).updateShipmentStatus(1L, ShipmentStatus.IN_TRANSIT);
    }
}