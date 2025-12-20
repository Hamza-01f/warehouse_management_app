package com.brief.demo.mappers;

import com.brief.demo.dto.request.InventoryRequestDTO;
import com.brief.demo.dto.request.InventoryUpdateDTO;
import com.brief.demo.dto.response.InventoryResponseDTO;
import com.brief.demo.model.Inventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InventoryMapperTest {

    @InjectMocks
    private InventoryMapper inventoryMapper;

    private Inventory inventory;

    @BeforeEach
    void setUp() {
        inventory = Inventory.builder()
                .id(1L)
                .quantityOnHand(100)
                .quantityReserved(20)
                .build();
    }

    @Test
    void toEntity_ShouldMapAllFields() {
        //  ARRANGE
        InventoryRequestDTO request = new InventoryRequestDTO();
        request.setQuantityOnHand(50);
        request.setQuantityReserved(10);

        //  ACT
        Inventory result = inventoryMapper.toEntity(request);

        //  ASSERT
        assertNotNull(result);
        assertEquals(50, result.getQuantityOnHand());
        assertEquals(10, result.getQuantityReserved());
    }

    @Test
    void toEntity_WhenNullValues_ShouldUseDefaults() {
        //  ARRANGE
        InventoryRequestDTO request = new InventoryRequestDTO();
        request.setQuantityOnHand(null);
        request.setQuantityReserved(null);

        //  ACT
        Inventory result = inventoryMapper.toEntity(request);

        //  ASSERT
        assertNotNull(result);
        assertEquals(0, result.getQuantityOnHand());
        assertEquals(0, result.getQuantityReserved());
    }

    @Test
    void toResponse_ShouldMapAllFields() {
        //  ARRANGE - Already set up in beforeEach

        //  ACT
        InventoryResponseDTO response = inventoryMapper.toResponse(inventory);

        //  ASSERT
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(100, response.getQuantityOnHand());
        assertEquals(20, response.getQuantityReserved());
        assertEquals(80, response.getAvailableQuantity()); // 100 - 20
    }

    @Test
    void updateEntity_ShouldUpdateOnlyProvidedFields() {
        //  ARRANGE
        InventoryUpdateDTO updateDTO = new InventoryUpdateDTO();
        updateDTO.setQuantityOnHand(150);
        updateDTO.setQuantityReserved(30);

        //  ACT
        inventoryMapper.updateEntity(inventory, updateDTO);

        //  ASSERT
        assertEquals(150, inventory.getQuantityOnHand());
        assertEquals(30, inventory.getQuantityReserved());
    }

    @Test
    void updateEntity_WhenPartialUpdate_ShouldUpdateOnlyProvidedFields() {
        //  ARRANGE
        InventoryUpdateDTO updateDTO = new InventoryUpdateDTO();
        updateDTO.setQuantityOnHand(150);
        // quantityReserved not set

        //  ACT
        inventoryMapper.updateEntity(inventory, updateDTO);

        //  ASSERT
        assertEquals(150, inventory.getQuantityOnHand());
        assertEquals(20, inventory.getQuantityReserved()); // unchanged
    }
}