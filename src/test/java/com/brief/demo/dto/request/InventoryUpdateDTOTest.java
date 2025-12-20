package com.brief.demo.dto.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryUpdateDTOTest {

    @Test
    void inventoryUpdateDTO_ShouldHaveCorrectFields() {
        // 🟢 ARRANGE
        InventoryUpdateDTO dto = new InventoryUpdateDTO();

        // 🟢 ACT
        dto.setQuantityOnHand(150);
        dto.setQuantityReserved(30);

        // 🟢 ASSERT
        assertEquals(150, dto.getQuantityOnHand());
        assertEquals(30, dto.getQuantityReserved());
    }

    @Test
    void inventoryUpdateDTO_ShouldHandleNullValues() {
        // 🟢 ARRANGE
        InventoryUpdateDTO dto = new InventoryUpdateDTO();

        // 🟢 ACT
        dto.setQuantityOnHand(null);
        dto.setQuantityReserved(null);

        // 🟢 ASSERT
        assertNull(dto.getQuantityOnHand());
        assertNull(dto.getQuantityReserved());
    }
}