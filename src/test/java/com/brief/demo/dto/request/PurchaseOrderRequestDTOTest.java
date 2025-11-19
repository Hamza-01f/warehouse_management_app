package com.brief.demo.dto.request;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PurchaseOrderRequestDTOTest {

    @Test
    void purchaseOrderRequestDTO_ShouldHaveCorrectFields() {
        // 🟢 ARRANGE
        PurchaseOrderRequestDTO dto = new PurchaseOrderRequestDTO();
        PurchaseOrderRequestDTO.POLineRequestDTO line = new PurchaseOrderRequestDTO.POLineRequestDTO();
        line.setProductId(1L);
        line.setQuantity(10);
        line.setPrice(new BigDecimal("99.99"));

        // 🟢 ACT
        dto.setSupplierId(1L);
        dto.setOrderLines(Arrays.asList(line));

        // 🟢 ASSERT
        assertEquals(1L, dto.getSupplierId());
        assertNotNull(dto.getOrderLines());
        assertEquals(1, dto.getOrderLines().size());
        assertEquals(1L, dto.getOrderLines().get(0).getProductId());
        assertEquals(10, dto.getOrderLines().get(0).getQuantity());
        assertEquals(new BigDecimal("99.99"), dto.getOrderLines().get(0).getPrice());
    }

    @Test
    void purchaseOrderRequestDTO_ShouldHandleNullValues() {
        // 🟢 ARRANGE
        PurchaseOrderRequestDTO dto = new PurchaseOrderRequestDTO();

        // 🟢 ACT
        dto.setSupplierId(null);
        dto.setOrderLines(null);

        // 🟢 ASSERT
        assertNull(dto.getSupplierId());
        assertNull(dto.getOrderLines());
    }

    @Test
    void poLineRequestDTO_ShouldHandleNullValues() {
        // 🟢 ARRANGE
        PurchaseOrderRequestDTO.POLineRequestDTO line = new PurchaseOrderRequestDTO.POLineRequestDTO();

        // 🟢 ACT
        line.setProductId(null);
        line.setQuantity(null);
        line.setPrice(null);

        // 🟢 ASSERT
        assertNull(line.getProductId());
        assertNull(line.getQuantity());
        assertNull(line.getPrice());
    }
}