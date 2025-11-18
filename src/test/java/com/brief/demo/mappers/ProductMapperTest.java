package com.brief.demo.mappers;

import com.brief.demo.dto.request.ProductRequestDTO;
import com.brief.demo.dto.request.ProductRequestUpdateDTO;
import com.brief.demo.dto.response.ProductResponseDTO;
import com.brief.demo.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductMapperTest {

    @InjectMocks
    private ProductMapper productMapper;

    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .sku("TEST123")
                .name("Test Product")
                .image("image.jpg")
                .price(BigDecimal.valueOf(99.00))
                .unit("pcs")
                .isActive(true)
                .build();
    }

    @Test
    void toEntity_ShouldMapAllFields() {
        //  ARRANGE
        ProductRequestDTO request = new ProductRequestDTO();
        request.setName("New Product");
        request.setImage("new.jpg");
        request.setPrice(BigDecimal.valueOf(99.00));
        request.setUnit("kg");

        //  ACT
        Product result = productMapper.toEntity(request);

        //  ASSERT
        assertNotNull(result);
        assertEquals("New Product", result.getName());
        assertEquals("new.jpg", result.getImage());
        assertEquals(BigDecimal.valueOf(99.00), result.getPrice());
        assertEquals("kg", result.getUnit());
    }

    @Test
    void toResponse_ShouldMapAllFields() {
        //  ACT
        ProductResponseDTO response = productMapper.toResponse(product);

        //  ASSERT
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("TEST123", response.getSku());
        assertEquals("Test Product", response.getName());
        assertEquals("image.jpg", response.getImage());
        assertEquals(BigDecimal.valueOf(99.00), response.getPrice());
        assertEquals("pcs", response.getUnit());
        assertTrue(response.getIsActive());
    }

    @Test
    void updateEntity_ShouldUpdateAllProvidedFields() {
        //  ARRANGE
        ProductRequestUpdateDTO updateDTO = new ProductRequestUpdateDTO();
        updateDTO.setSku("UPDATED123");
        updateDTO.setName("Updated Product");
        updateDTO.setImage("updated.jpg");
        updateDTO.setPrice(BigDecimal.valueOf(99.00));
        updateDTO.setUnit("box");
        updateDTO.setIsActive(false);

        //  ACT
        productMapper.updateEntity(product, updateDTO);

        //  ASSERT
        assertEquals("UPDATED123", product.getSku());
        assertEquals("Updated Product", product.getName());
        assertEquals("updated.jpg", product.getImage());
        assertEquals(BigDecimal.valueOf(99.00), product.getPrice());
        assertEquals("box", product.getUnit());
        assertFalse(product.getIsActive());
    }

    @Test
    void updateEntity_WhenPartialUpdate_ShouldUpdateOnlyProvidedFields() {
        //  ARRANGE
        ProductRequestUpdateDTO updateDTO = new ProductRequestUpdateDTO();
        updateDTO.setName("Updated Name Only");

        //  ACT
        productMapper.updateEntity(product, updateDTO);

        //  ASSERT
        assertEquals("Updated Name Only", product.getName());
        assertEquals("TEST123", product.getSku()); // unchanged
        assertEquals("image.jpg", product.getImage()); // unchanged
        assertEquals(BigDecimal.valueOf(99.00), product.getPrice()); // unchanged
        assertTrue(product.getIsActive()); // unchanged
    }
}
