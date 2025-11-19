package com.brief.demo.dto.request;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductRequestUpdateDTOTest {

    @Test
    void productRequestUpdateDTO_ShouldHaveCorrectFields() {
        // 🟢 ARRANGE
        ProductRequestUpdateDTO dto = new ProductRequestUpdateDTO();

        // 🟢 ACT
        dto.setSku("UPDATED123");
        dto.setName("Updated Product");
        dto.setImage("updated.jpg");
        dto.setPrice(new BigDecimal("149.99"));
        dto.setUnit("box");
        dto.setIsActive(false);

        // 🟢 ASSERT
        assertEquals("UPDATED123", dto.getSku());
        assertEquals("Updated Product", dto.getName());
        assertEquals("updated.jpg", dto.getImage());
        assertEquals(new BigDecimal("149.99"), dto.getPrice());
        assertEquals("box", dto.getUnit());
        assertFalse(dto.getIsActive());
    }

    @Test
    void productRequestUpdateDTO_ShouldHandleNullValues() {
        // 🟢 ARRANGE
        ProductRequestUpdateDTO dto = new ProductRequestUpdateDTO();

        // 🟢 ACT
        dto.setSku(null);
        dto.setName(null);
        dto.setImage(null);
        dto.setPrice(null);
        dto.setUnit(null);
        dto.setIsActive(null);

        // 🟢 ASSERT
        assertNull(dto.getSku());
        assertNull(dto.getName());
        assertNull(dto.getImage());
        assertNull(dto.getPrice());
        assertNull(dto.getUnit());
        assertNull(dto.getIsActive());
    }
}