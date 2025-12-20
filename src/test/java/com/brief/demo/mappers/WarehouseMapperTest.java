package com.brief.demo.mappers;

import com.brief.demo.dto.request.WarehouseRequestDTO;
import com.brief.demo.dto.response.WarehouseResponseDTO;
import com.brief.demo.model.Warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WarehouseMapperTest {

    @InjectMocks
    private WarehouseMapper warehouseMapper;

    private Warehouse warehouse;

    @BeforeEach
    void setUp() {
        warehouse = Warehouse.builder()
                .id(1L)
                .name("Test Warehouse")
                .address("123 Warehouse St")
                .isActive(true)
                .build();
    }

    @Test
    void toEntity_ShouldMapAllFields() {
        //  ARRANGE
        WarehouseRequestDTO request = new WarehouseRequestDTO();
        request.setName("New Warehouse");
        request.setAddress("456 New Address");

        //  ACT
        Warehouse result = warehouseMapper.toEntity(request);

        //  ASSERT
        assertNotNull(result);
        assertEquals("New Warehouse", result.getName());
        assertEquals("456 New Address", result.getAddress());
    }

    @Test
    void toResponse_ShouldMapAllFields() {
        //  ARRANGE - Already set up in beforeEach

        //  ACT
        WarehouseResponseDTO response = warehouseMapper.toResponse(warehouse);

        //  ASSERT
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Warehouse", response.getName());
        assertEquals("123 Warehouse St", response.getAddress());
        assertTrue(response.getIsActive());
    }
}