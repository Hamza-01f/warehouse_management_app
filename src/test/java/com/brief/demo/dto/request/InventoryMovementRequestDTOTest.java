package com.brief.demo.dto.request;

import com.brief.demo.enums.MovementType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryMovementRequestDTOTest {

    @Test
    void inventoryMovementRequestDTO_ShouldHaveCorrectFields() {
        // 🟢 ARRANGE
        InventoryMovementRequestDTO dto = new InventoryMovementRequestDTO();

        // 🟢 ACT
        dto.setInventoryId(1L);
        dto.setType(MovementType.IN);
        dto.setQuantity(50);
        dto.setReason("Restock");

        // 🟢 ASSERT
        assertEquals(1L, dto.getInventoryId());
        assertEquals(MovementType.IN, dto.getType());
        assertEquals(50, dto.getQuantity());
        assertEquals("Restock", dto.getReason());
    }

    @Test
    void inventoryMovementRequestDTO_ShouldHandleNullValues() {
        // 🟢 ARRANGE
        InventoryMovementRequestDTO dto = new InventoryMovementRequestDTO();

        // 🟢 ACT
        dto.setInventoryId(null);
        dto.setType(null);
        dto.setQuantity(null);
        dto.setReason(null);

        // 🟢 ASSERT
        assertNull(dto.getInventoryId());
        assertNull(dto.getType());
        assertNull(dto.getQuantity());
        assertNull(dto.getReason());
    }

    @Test
    void inventoryMovementRequestDTO_WithAllMovementTypes() {
        // 🟢 ARRANGE
        MovementType[] movementTypes = {MovementType.IN, MovementType.OUT, MovementType.ADJ};

        for (MovementType type : movementTypes) {
            // 🟢 ACT
            InventoryMovementRequestDTO dto = new InventoryMovementRequestDTO();
            dto.setType(type);

            // 🟢 ASSERT
            assertEquals(type, dto.getType());
        }
    }

    @Test
    void inventoryMovementRequestDTO_EqualsAndHashCode() {
        // 🟢 ARRANGE
        InventoryMovementRequestDTO dto1 = new InventoryMovementRequestDTO();
        dto1.setInventoryId(1L);
        dto1.setType(MovementType.IN);
        dto1.setQuantity(50);
        dto1.setReason("Restock");

        InventoryMovementRequestDTO dto2 = new InventoryMovementRequestDTO();
        dto2.setInventoryId(1L);
        dto2.setType(MovementType.IN);
        dto2.setQuantity(50);
        dto2.setReason("Restock");

        // 🟢 ASSERT
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
}