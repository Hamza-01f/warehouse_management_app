package com.brief.demo.dto.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryRequestDTOTest {

    @Test
    void inventoryRequestDTO_ShouldHaveCorrectFields() {
        // 🟢 ARRANGE
        InventoryRequestDTO dto = new InventoryRequestDTO();

        // 🟢 ACT
        dto.setWarehouseId(1L);
        dto.setProductId(2L);
        dto.setQuantityOnHand(100);
        dto.setQuantityReserved(20);

        // 🟢 ASSERT
        assertEquals(1L, dto.getWarehouseId());
        assertEquals(2L, dto.getProductId());
        assertEquals(100, dto.getQuantityOnHand());
        assertEquals(20, dto.getQuantityReserved());
    }

    @Test
    void inventoryRequestDTO_ShouldHandleNullValues() {
        // 🟢 ARRANGE
        InventoryRequestDTO dto = new InventoryRequestDTO();

        // 🟢 ACT
        dto.setWarehouseId(null);
        dto.setProductId(null);
        dto.setQuantityOnHand(null);
        dto.setQuantityReserved(null);

        // 🟢 ASSERT
        assertNull(dto.getWarehouseId());
        assertNull(dto.getProductId());
        assertNull(dto.getQuantityOnHand());
        assertNull(dto.getQuantityReserved());
    }
}