package com.brief.demo.service;

import com.brief.demo.dto.request.ProductRequestDTO;
import com.brief.demo.dto.response.ProductResponseDTO;
import com.brief.demo.exception.DuplicateResourceException;
import com.brief.demo.exception.ResourceNotFoundException;
import com.brief.demo.model.Product;
import com.brief.demo.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private com.brief.demo.mappers.ProductMapper productMapper;

    @InjectMocks private ProductService productService;

    @Test
    void createProduct_Success() {
        // Given
        ProductRequestDTO request = new ProductRequestDTO();
        request.setName("Test Product");
        request.setPrice(java.math.BigDecimal.valueOf(100));

        Product product = new Product();
        when(productMapper.toEntity(request)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(new ProductResponseDTO());

        // When
        ProductResponseDTO result = productService.createProduct(request);

        // Then
        assertNotNull(result);
        verify(productRepository).save(product);
    }

    @Test
    void getProductById_NotFound() {
        // Given
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () ->
                productService.getProductById(999L));
    }

    @Test
    void deleteProduct_Success() {
        // Given
        Product product = new Product();
        product.setIsActive(true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // When
        productService.deleteProduct(1L);

        // Then
        assertFalse(product.getIsActive());
        verify(productRepository).save(product);
    }
}