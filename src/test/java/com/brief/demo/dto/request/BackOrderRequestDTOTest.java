package com.brief.demo.dto.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BackOrderRequestDTOTest {

    @Test
    void backOrderRequestDTO_ShouldHaveCorrectFields() {
        // 🟢 ARRANGE
        BackOrderRequestDTO dto = new BackOrderRequestDTO();

        // 🟢 ACT
        dto.setSalesOrderLineId(1L);
        dto.setProductId(2L);
        dto.setQuantity(10);

        // 🟢 ASSERT
        assertEquals(1L, dto.getSalesOrderLineId());
        assertEquals(2L, dto.getProductId());
        assertEquals(10, dto.getQuantity());
    }

    @Test
    void backOrderRequestDTO_ShouldHandleNullValues() {
        // 🟢 ARRANGE
        BackOrderRequestDTO dto = new BackOrderRequestDTO();

        // 🟢 ACT
        dto.setSalesOrderLineId(null);
        dto.setProductId(null);
        dto.setQuantity(null);

        // 🟢 ASSERT
        assertNull(dto.getSalesOrderLineId());
        assertNull(dto.getProductId());
        assertNull(dto.getQuantity());
    }

    @Test
    void backOrderRequestDTO_EqualsAndHashCode() {
        // 🟢 ARRANGE
        BackOrderRequestDTO dto1 = new BackOrderRequestDTO();
        dto1.setSalesOrderLineId(1L);
        dto1.setProductId(2L);
        dto1.setQuantity(10);

        BackOrderRequestDTO dto2 = new BackOrderRequestDTO();
        dto2.setSalesOrderLineId(1L);
        dto2.setProductId(2L);
        dto2.setQuantity(10);

        // 🟢 ASSERT
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void backOrderRequestDTO_ToString() {
        // 🟢 ARRANGE
        BackOrderRequestDTO dto = new BackOrderRequestDTO();
        dto.setSalesOrderLineId(1L);
        dto.setProductId(2L);
        dto.setQuantity(10);

        // 🟢 ACT
        String toString = dto.toString();

        // 🟢 ASSERT
        assertNotNull(toString);
        assertTrue(toString.contains("salesOrderLineId=1"));
        assertTrue(toString.contains("productId=2"));
        assertTrue(toString.contains("quantity=10"));
    }
}