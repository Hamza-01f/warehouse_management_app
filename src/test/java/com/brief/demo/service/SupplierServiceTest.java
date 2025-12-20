package com.brief.demo.service;

import com.brief.demo.dto.request.SupplierRequestDTO;
import com.brief.demo.dto.response.SupplierResponseDTO;
import com.brief.demo.exception.DuplicateResourceException;
import com.brief.demo.exception.ResourceNotFoundException;
import com.brief.demo.mappers.SupplierMapper;
import com.brief.demo.model.Supplier;
import com.brief.demo.repository.SupplierRepository;
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
class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplierMapper supplierMapper;

    @InjectMocks
    private SupplierService supplierService;

    private Supplier supplier;
    private SupplierRequestDTO supplierRequestDTO;

    @BeforeEach
    void setUp() {
        supplier = Supplier.builder()
                .id(1L)
                .name("Test Supplier")
                .contact("test@supplier.com")
                .isActive(true)
                .build();

        supplierRequestDTO = new SupplierRequestDTO();
        supplierRequestDTO.setName("Test Supplier");
        supplierRequestDTO.setContact("test@supplier.com");
    }

    @Test
    void createSupplier_ShouldCreateSupplier() {
        // Arrange
        when(supplierRepository.existsByName("Test Supplier")).thenReturn(false);
        when(supplierMapper.toEntity(any(SupplierRequestDTO.class))).thenReturn(supplier);
        when(supplierRepository.save(any(Supplier.class))).thenReturn(supplier);
        when(supplierMapper.toResponse(any(Supplier.class))).thenReturn(new SupplierResponseDTO());

        // Act
        SupplierResponseDTO result = supplierService.createSupplier(supplierRequestDTO);

        // Assert
        assertNotNull(result);
        verify(supplierRepository, times(1)).save(any(Supplier.class));
    }

    @Test
    void createSupplier_WhenDuplicateName_ShouldThrowException() {
        // Arrange
        when(supplierRepository.existsByName("Test Supplier")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () ->
                supplierService.createSupplier(supplierRequestDTO));
    }

    @Test
    void getSupplierById_WhenExists_ShouldReturnSupplier() {
        // Arrange
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(supplierMapper.toResponse(any(Supplier.class))).thenReturn(new SupplierResponseDTO());

        // Act
        SupplierResponseDTO result = supplierService.getSupplierById(1L);

        // Assert
        assertNotNull(result);
    }

    @Test
    void getSupplierById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(supplierRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                supplierService.getSupplierById(1L));
    }

    @Test
    void getAllSuppliers_ShouldReturnList() {
        // Arrange
        when(supplierRepository.findAll()).thenReturn(List.of(supplier));
        when(supplierMapper.toResponse(any(Supplier.class))).thenReturn(new SupplierResponseDTO());

        // Act
        List<SupplierResponseDTO> result = supplierService.getAllSuppliers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void deleteSupplier_ShouldDeactivateSupplier() {
        // Arrange
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(supplierRepository.save(any(Supplier.class))).thenReturn(supplier);

        // Act
        supplierService.deleteSupplier(1L);

        // Assert
        assertFalse(supplier.getIsActive());
        verify(supplierRepository, times(1)).save(supplier);
    }
}