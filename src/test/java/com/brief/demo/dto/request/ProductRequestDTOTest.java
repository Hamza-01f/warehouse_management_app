package com.brief.demo.dto.request;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductRequestDTOTest {

    @Test
    void productRequestDTO_ShouldHaveCorrectFields() {
        // 🟢 ARRANGE
        ProductRequestDTO dto = new ProductRequestDTO();

        // 🟢 ACT
        dto.setSku("TEST123");
        dto.setName("Test Product");
        dto.setImage("image.jpg");
        dto.setPrice(new BigDecimal("99.99"));
        dto.setUnit("pcs");
        dto.setIsActive(true);

        // 🟢 ASSERT
        assertEquals("TEST123", dto.getSku());
        assertEquals("Test Product", dto.getName());
        assertEquals("image.jpg", dto.getImage());
        assertEquals(new BigDecimal("99.99"), dto.getPrice());
        assertEquals("pcs", dto.getUnit());
        assertTrue(dto.getIsActive());
    }

    @Test
    void productRequestDTO_ShouldHandleNullValues() {
        // 🟢 ARRANGE
        ProductRequestDTO dto = new ProductRequestDTO();

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