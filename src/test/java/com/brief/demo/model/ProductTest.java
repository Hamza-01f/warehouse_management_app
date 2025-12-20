package com.brief.demo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .sku("TEST123")
                .name("Test Product")
                .image("image.jpg")
                .price(new BigDecimal("99.99"))
                .unit("pcs")
                .isActive(true)
                .build();
    }

    @Test
    void product_ShouldCreateWithDefaultValues() {
        // 🟢 ARRANGE
        Product newProduct = new Product();

        // 🟢 ASSERT
        assertTrue(newProduct.getIsActive());
    }

    @Test
    void product_ShouldHaveCorrectFields() {
        // 🟢 ASSERT
        assertEquals("TEST123", product.getSku());
        assertEquals("Test Product", product.getName());
        assertEquals("image.jpg", product.getImage());
        assertEquals(new BigDecimal("99.99"), product.getPrice());
        assertEquals("pcs", product.getUnit());
        assertTrue(product.getIsActive());
    }

    @Test
    void product_ShouldUpdateFieldsCorrectly() {
        // 🟢 ARRANGE
        Product updatedProduct = new Product();

        // 🟢 ACT
        updatedProduct.setSku("UPDATED123");
        updatedProduct.setName("Updated Product");
        updatedProduct.setImage("updated.jpg");
        updatedProduct.setPrice(new BigDecimal("149.99"));
        updatedProduct.setUnit("box");
        updatedProduct.setIsActive(false);

        // 🟢 ASSERT
        assertEquals("UPDATED123", updatedProduct.getSku());
        assertEquals("Updated Product", updatedProduct.getName());
        assertEquals("updated.jpg", updatedProduct.getImage());
        assertEquals(new BigDecimal("149.99"), updatedProduct.getPrice());
        assertEquals("box", updatedProduct.getUnit());
        assertFalse(updatedProduct.getIsActive());
    }

    @Test
    void product_ShouldHandleNullValues() {
        // 🟢 ARRANGE
        Product product = new Product();

        // 🟢 ACT
        product.setSku(null);
        product.setName(null);
        product.setImage(null);
        product.setPrice(null);
        product.setUnit(null);
        product.setIsActive(null);

        // 🟢 ASSERT
        assertNull(product.getSku());
        assertNull(product.getName());
        assertNull(product.getImage());
        assertNull(product.getPrice());
        assertNull(product.getUnit());
        assertNull(product.getIsActive());
    }

    @Test
    void generatSku_ShouldGenerateSkuWhenNull() {
        // 🟢 ARRANGE
        Product product = new Product();
        product.setSku(null);

        // 🟢 ACT - Manually call the @PrePersist logic
        product.generatSku();

        // 🟢 ASSERT
        assertNotNull(product.getSku());
        assertTrue(product.getSku().startsWith("PROD-"));
        assertEquals(13, product.getSku().length()); // "PROD-" + 8 characters
    }

    @Test
    void generatSku_ShouldNotOverrideExistingSku() {
        // 🟢 ARRANGE
        Product product = new Product();
        product.setSku("EXISTING123");

        // 🟢 ACT - Manually call the @PrePersist logic
        product.generatSku();

        // 🟢 ASSERT
        assertEquals("EXISTING123", product.getSku());
    }


    @Test
    void product_EqualsAndHashCode_WhenDifferentIds() {
        // 🟢 ARRANGE
        Product product1 = new Product();
        product1.setId(1L);

        Product product2 = new Product();
        product2.setId(2L);

        // 🟢 ASSERT
        assertNotEquals(product1, product2);
    }

    @Test
    void product_Equals_WhenNull() {
        // 🟢 ARRANGE
        Product product = new Product();
        product.setId(1L);

        // 🟢 ASSERT
        assertNotEquals(null, product);
    }

    @Test
    void product_Equals_WhenSameObject() {
        // 🟢 ARRANGE
        Product product = new Product();
        product.setId(1L);

        // 🟢 ASSERT
        assertEquals(product, product);
    }

    @Test
    void product_Equals_WhenDifferentClass() {
        // 🟢 ARRANGE
        Product product = new Product();
        product.setId(1L);

        // 🟢 ASSERT
        assertNotEquals("not a product", product);
    }

    @Test
    void product_ToString_IsNotNull() {
        // 🟢 ARRANGE
        product.setId(1L);

        // 🟢 ACT
        String toString = product.toString();

        // 🟢 ASSERT
        assertNotNull(toString);
    }
}