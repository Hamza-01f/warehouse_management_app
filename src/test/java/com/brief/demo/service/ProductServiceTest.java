package com.brief.demo.service;

import com.brief.demo.dto.request.ProductRequestDTO;
import com.brief.demo.dto.request.ProductRequestUpdateDTO;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    private ProductResponseDTO productResponseDTO;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .sku("PROD-ABC123")
                .name("Test Product")
                .price(new BigDecimal("99.99"))
                .unit("pcs")
                .isActive(true)
                .build();

        productRequestDTO = new ProductRequestDTO();
        productRequestDTO.setName("Test Product");
        productRequestDTO.setPrice(new BigDecimal("99.99"));
        productRequestDTO.setUnit("pcs");

        productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setId(1L);
        productResponseDTO.setSku("PROD-ABC123");
        productResponseDTO.setName("Test Product");
        productResponseDTO.setPrice(new BigDecimal("99.99"));
        productResponseDTO.setUnit("pcs");
        productResponseDTO.setIsActive(true);
    }

    @Test
    void createProduct_ShouldReturnProductResponse_WhenValidRequest() {
        // Arrange
        when(productMapper.toEntity(productRequestDTO)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(productResponseDTO);

        // Act
        ProductResponseDTO result = productService.createProduct(productRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        assertEquals(new BigDecimal("99.99"), result.getPrice());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void getProductById_ShouldReturnProduct_WhenProductExists() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(productResponseDTO);

        // Act
        ProductResponseDTO result = productService.getProductById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Product", result.getName());
    }

    @Test
    void getProductById_ShouldThrowException_WhenProductNotFound() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.getProductById(1L);
        });
    }

    @Test
    void getAllProducts_ShouldReturnProductList() {
        // Arrange
        List<Product> products = Arrays.asList(product);
        when(productRepository.findAll()).thenReturn(products);
        when(productMapper.toResponse(product)).thenReturn(productResponseDTO);

        // Act
        List<ProductResponseDTO> result = productService.getAllProducts();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getName());
    }

    @Test
    void getActiveProducts_ShouldReturnActiveProductsOnly() {
        // Arrange
        Product inactiveProduct = Product.builder()
                .id(2L)
                .name("Inactive Product")
                .isActive(false)
                .build();

        List<Product> activeProducts = Arrays.asList(product);
        when(productRepository.findByIsActiveTrue()).thenReturn(activeProducts);
        when(productMapper.toResponse(product)).thenReturn(productResponseDTO);

        // Act
        List<ProductResponseDTO> result = productService.getActiveProducts();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsActive());
    }

    @Test
    void updateProduct_ShouldUpdateProduct_WhenValidRequest() {
        // Arrange
        ProductRequestUpdateDTO updateDTO = new ProductRequestUpdateDTO();
        updateDTO.setName("Updated Product");
        updateDTO.setPrice(new BigDecimal("149.99"));

        Product updatedProduct = Product.builder()
                .id(1L)
                .name("Updated Product")
                .price(new BigDecimal("149.99"))
                .build();

        ProductResponseDTO updatedResponse = new ProductResponseDTO();
        updatedResponse.setId(1L);
        updatedResponse.setName("Updated Product");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.existsBySkuAndIdNot(anyString(), anyLong())).thenReturn(false);
        when(productRepository.save(product)).thenReturn(updatedProduct);
        when(productMapper.toResponse(updatedProduct)).thenReturn(updatedResponse);

        // Act
        ProductResponseDTO result = productService.updateProduct(1L, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Product", result.getName());
        verify(productMapper, times(1)).updateEntity(product, updateDTO);
    }

    @Test
    void updateProduct_ShouldThrowException_WhenSkuAlreadyExists() {
        // Arrange
        ProductRequestUpdateDTO updateDTO = new ProductRequestUpdateDTO();
        updateDTO.setSku("EXISTING-SKU");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.existsBySkuAndIdNot("EXISTING-SKU", 1L)).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> {
            productService.updateProduct(1L, updateDTO);
        });
    }

    @Test
    void deleteProduct_ShouldSoftDeleteProduct() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        // Act
        productService.deleteProduct(1L);

        // Assert
        assertFalse(product.getIsActive());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void deleteProduct_ShouldThrowException_WhenProductNotFound() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.deleteProduct(1L);
        });
    }

    @Test
    void activateProduct_ShouldActivateProduct() {
        // Arrange
        product.setIsActive(false);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        // Act
        productService.activateProduct(1L);

        // Assert
        assertTrue(product.getIsActive());
        verify(productRepository, times(1)).save(product);
    }
}