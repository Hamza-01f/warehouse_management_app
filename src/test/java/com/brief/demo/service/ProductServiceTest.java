package com.brief.demo.service;

import com.brief.demo.dto.request.ProductRequestDTO;
import com.brief.demo.dto.response.ProductResponseDTO;
import com.brief.demo.exception.DuplicateResourceException;
import com.brief.demo.exception.ResourceNotFoundException;
import com.brief.demo.mappers.ProductMapper;
import com.brief.demo.model.Product;
import com.brief.demo.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductRequestDTO productRequestDTO;

    // running before each method calling
    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .sku("TEST123")
                .price(BigDecimal.valueOf(100.00))
                .isActive(true)
                .build();

        productRequestDTO = new ProductRequestDTO();
        productRequestDTO.setName("Test Product");
        productRequestDTO.setSku("TEST123");
        productRequestDTO.setPrice(BigDecimal.valueOf(100.00));
    }

    //AAA pattern
    @Test
    void getProductById_WhenExists_ShouldReturnProduct() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toResponse(any(Product.class))).thenReturn(new ProductResponseDTO());

        // Act
        ProductResponseDTO result = productService.getProductById(1L);

        // Assert
        assertNotNull(result);
    }

    @Test
    void getProductById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                productService.getProductById(1L));
    }

    @Test
    void getAllProducts_ShouldReturnList() {
        // Arrange
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(productMapper.toResponse(any(Product.class))).thenReturn(new ProductResponseDTO());

        // Act
        List<ProductResponseDTO> result = productService.getAllProducts();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getActiveProducts_ShouldReturnActiveProducts() {
        // Arrange
        when(productRepository.findByIsActiveTrue()).thenReturn(List.of(product));
        when(productMapper.toResponse(any(Product.class))).thenReturn(new ProductResponseDTO());

        // Act
        List<ProductResponseDTO> result = productService.getActiveProducts();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void deleteProduct_ShouldDeactivateProduct() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        productService.deleteProduct(1L);

        // Assert
        assertFalse(product.getIsActive());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void activateProduct_ShouldActivateProduct() {
        // Arrange
        product.setIsActive(false);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        productService.activateProduct(1L);

        // Assert
        assertTrue(product.getIsActive());
    }
}