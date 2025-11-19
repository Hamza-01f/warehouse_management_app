package com.brief.demo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class POLineTest {

    private POLine poLine;
    private PurchaseOrder purchaseOrder;
    private Product product;

    @BeforeEach
    void setUp() {
        purchaseOrder = new PurchaseOrder();
        purchaseOrder.setId(1L);

        product = new Product();
        product.setId(1L);

        poLine = POLine.builder()
                .purchaseOrder(purchaseOrder)
                .product(product)
                .quantity(10)
                .price(new BigDecimal("99.99"))
                .quantityReceived(0)
                .build();
    }

    @Test
    void poLine_ShouldCreateWithDefaultValues() {
        // 🟢 ARRANGE
        POLine newPoLine = new POLine();

        // 🟢 ASSERT
        assertEquals(0, newPoLine.getQuantityReceived());
    }

    @Test
    void poLine_ShouldHaveCorrectFields() {
        // 🟢 ASSERT
        assertEquals(purchaseOrder, poLine.getPurchaseOrder());
        assertEquals(product, poLine.getProduct());
        assertEquals(10, poLine.getQuantity());
        assertEquals(new BigDecimal("99.99"), poLine.getPrice());
        assertEquals(0, poLine.getQuantityReceived());
    }

    @Test
    void poLine_ShouldUpdateFieldsCorrectly() {
        // 🟢 ARRANGE
        PurchaseOrder newPurchaseOrder = new PurchaseOrder();
        newPurchaseOrder.setId(2L);

        Product newProduct = new Product();
        newProduct.setId(2L);

        // 🟢 ACT
        poLine.setPurchaseOrder(newPurchaseOrder);
        poLine.setProduct(newProduct);
        poLine.setQuantity(20);
        poLine.setPrice(new BigDecimal("149.99"));
        poLine.setQuantityReceived(5);

        // 🟢 ASSERT
        assertEquals(newPurchaseOrder, poLine.getPurchaseOrder());
        assertEquals(newProduct, poLine.getProduct());
        assertEquals(20, poLine.getQuantity());
        assertEquals(new BigDecimal("149.99"), poLine.getPrice());
        assertEquals(5, poLine.getQuantityReceived());
    }

    @Test
    void poLine_ShouldHandleNullValues() {
        // 🟢 ARRANGE
        POLine poLine = new POLine();

        // 🟢 ACT
        poLine.setPurchaseOrder(null);
        poLine.setProduct(null);
        poLine.setQuantity(null);
        poLine.setPrice(null);
        poLine.setQuantityReceived(null);

        // 🟢 ASSERT
        assertNull(poLine.getPurchaseOrder());
        assertNull(poLine.getProduct());
        assertNull(poLine.getQuantity());
        assertNull(poLine.getPrice());
        assertNull(poLine.getQuantityReceived());
    }

    @Test
    void poLine_WithDifferentPriceValues() {
        // 🟢 ARRANGE
        BigDecimal[] prices = {
                new BigDecimal("0.01"),
                new BigDecimal("99.99"),
                new BigDecimal("1000.00"),
                new BigDecimal("1234.56")
        };

        for (BigDecimal price : prices) {
            // 🟢 ACT
            POLine line = POLine.builder()
                    .purchaseOrder(purchaseOrder)
                    .product(product)
                    .quantity(1)
                    .price(price)
                    .build();

            // 🟢 ASSERT
            assertEquals(price, line.getPrice());
        }
    }


    @Test
    void poLine_EqualsAndHashCode_WhenDifferentIds() {
        // 🟢 ARRANGE
        POLine poLine1 = new POLine();
        poLine1.setId(1L);

        POLine poLine2 = new POLine();
        poLine2.setId(2L);

        // 🟢 ASSERT
        assertNotEquals(poLine1, poLine2);
    }

    @Test
    void poLine_Equals_WhenNull() {
        // 🟢 ARRANGE
        POLine poLine = new POLine();
        poLine.setId(1L);

        // 🟢 ASSERT
        assertNotEquals(null, poLine);
    }

    @Test
    void poLine_Equals_WhenSameObject() {
        // 🟢 ARRANGE
        POLine poLine = new POLine();
        poLine.setId(1L);

        // 🟢 ASSERT
        assertEquals(poLine, poLine);
    }

    @Test
    void poLine_Equals_WhenDifferentClass() {
        // 🟢 ARRANGE
        POLine poLine = new POLine();
        poLine.setId(1L);

        // 🟢 ASSERT
        assertNotEquals("not a po line", poLine);
    }

    @Test
    void poLine_ToString_IsNotNull() {
        // 🟢 ARRANGE
        poLine.setId(1L);

        // 🟢 ACT
        String toString = poLine.toString();

        // 🟢 ASSERT
        assertNotNull(toString);
    }

    @Test
    void poLine_QuantityReceivedCanBeUpdated() {
        // 🟢 ARRANGE
        poLine.setQuantityReceived(0);

        // 🟢 ACT
        poLine.setQuantityReceived(5);
        poLine.setQuantityReceived(10);

        // 🟢 ASSERT
        assertEquals(10, poLine.getQuantityReceived());
    }
}