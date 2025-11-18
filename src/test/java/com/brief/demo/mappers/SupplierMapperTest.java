package com.brief.demo.mappers;

import com.brief.demo.dto.request.SupplierRequestDTO;
import com.brief.demo.dto.response.SupplierResponseDTO;
import com.brief.demo.model.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SupplierMapperTest {

    @InjectMocks
    private SupplierMapper supplierMapper;

    private Supplier supplier;

    @BeforeEach
    void setUp() {
        supplier = Supplier.builder()
                .id(1L)
                .name("Test Supplier")
                .contact("contact@supplier.com")
                .isActive(true)
                .build();
    }

    @Test
    void toEntity_ShouldMapAllFields() {
        //  ARRANGE
        SupplierRequestDTO request = new SupplierRequestDTO();
        request.setName("New Supplier");
        request.setContact("new@supplier.com");

        //  ACT
        Supplier result = supplierMapper.toEntity(request);

        //  ASSERT
        assertNotNull(result);
        assertEquals("New Supplier", result.getName());
        assertEquals("new@supplier.com", result.getContact());
    }

    @Test
    void toResponse_ShouldMapAllFields() {
        //  ARRANGE - Already set up in beforeEach

        //  ACT
        SupplierResponseDTO response = supplierMapper.toResponse(supplier);

        //  ASSERT
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Supplier", response.getName());
        assertEquals("contact@supplier.com", response.getContact());
        assertTrue(response.getIsActive());
    }
}