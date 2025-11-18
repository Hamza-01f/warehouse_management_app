package com.brief.demo.controller;

import com.brief.demo.dto.request.SupplierRequestDTO;
import com.brief.demo.dto.response.ApiResponseDTO;
import com.brief.demo.dto.response.SupplierResponseDTO;
import com.brief.demo.service.SupplierService;
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
class SupplierControllerTest {

    @Mock
    private SupplierService supplierService;

    @InjectMocks
    private SupplierController supplierController;

    private SupplierResponseDTO supplierResponse;

    @BeforeEach
    void setUp() {
        supplierResponse = new SupplierResponseDTO();
        supplierResponse.setId(1L);
        supplierResponse.setName("Test Supplier");
    }

    @Test
    void createSupplier_ShouldReturnSupplier() {
        // ARRANGE
        SupplierRequestDTO request = new SupplierRequestDTO();
        when(supplierService.createSupplier(any(SupplierRequestDTO.class))).thenReturn(supplierResponse);

        // ACT
        ResponseEntity<SupplierResponseDTO> response = supplierController.createSupplier(request);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(supplierResponse, response.getBody());
        verify(supplierService, times(1)).createSupplier(request);
    }

    @Test
    void getAllSuppliers_ShouldReturnList() {
        // ARRANGE
        List<SupplierResponseDTO> suppliers = Arrays.asList(supplierResponse);
        when(supplierService.getAllSuppliers()).thenReturn(suppliers);

        // ACT
        ResponseEntity<List<SupplierResponseDTO>> response = supplierController.getAllSuppliers();

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(supplierService, times(1)).getAllSuppliers();
    }

    @Test
    void getSupplierById_ShouldReturnSupplier() {
        // ARRANGE
        when(supplierService.getSupplierById(1L)).thenReturn(supplierResponse);

        // ACT
        ResponseEntity<SupplierResponseDTO> response = supplierController.getSupplierById(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(supplierResponse, response.getBody());
        verify(supplierService, times(1)).getSupplierById(1L);
    }

    @Test
    void deleteSupplier_ShouldReturnSuccessMessage() {
        // ARRANGE
        doNothing().when(supplierService).deleteSupplier(1L);

        // ACT
        ResponseEntity<ApiResponseDTO> response = supplierController.deleteSupplier(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Supplier deleted successfully", response.getBody().getMessage());
        assertTrue(response.getBody().isSuccess());
        verify(supplierService, times(1)).deleteSupplier(1L);
    }
}