package com.brief.demo.controller;

import com.brief.demo.dto.request.ProductRequestDTO;
import com.brief.demo.dto.request.ProductRequestUpdateDTO;
import com.brief.demo.dto.response.ApiResponseDTO;
import com.brief.demo.dto.response.ProductResponseDTO;
import com.brief.demo.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ProductResponseDTO productResponse;

    @BeforeEach
    void setUp() {
        productResponse = new ProductResponseDTO();
        productResponse.setId(1L);
        productResponse.setName("Test Product");
    }

    @Test
    void createProduct_ShouldReturnProduct() {
        //  ARRANGE
        ProductRequestDTO request = new ProductRequestDTO();
        when(productService.createProduct(any(ProductRequestDTO.class))).thenReturn(productResponse);

        //  ACT
        ResponseEntity<ProductResponseDTO> response = productController.createProduct(request);

        //  ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(productResponse, response.getBody());
        verify(productService, times(1)).createProduct(request);
    }

    @Test
    void getAllProducts_ShouldReturnList() {
        //  ARRANGE
        List<ProductResponseDTO> products = Arrays.asList(productResponse);
        when(productService.getAllProducts()).thenReturn(products);

        //  ACT
        ResponseEntity<List<ProductResponseDTO>> response = productController.getAllProducts();

        //  ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void getActiveProducts_ShouldReturnActiveProducts() {
        //  ARRANGE
        List<ProductResponseDTO> products = Arrays.asList(productResponse);
        when(productService.getActiveProducts()).thenReturn(products);

        //  ACT
        ResponseEntity<List<ProductResponseDTO>> response = productController.getActiveProducts();

        //  ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(productService, times(1)).getActiveProducts();
    }

    @Test
    void getProductById_ShouldReturnProduct() {
        //  ARRANGE
        when(productService.getProductById(1L)).thenReturn(productResponse);

        //  ACT
        ResponseEntity<ProductResponseDTO> response = productController.getProductById(1L);

        //  ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(productResponse, response.getBody());
        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProduct() {
        //  ARRANGE
        ProductRequestUpdateDTO request = new ProductRequestUpdateDTO();
        when(productService.updateProduct(1L, request)).thenReturn(productResponse);

        //  ACT
        ResponseEntity<ProductResponseDTO> response = productController.updateProduct(1L, request);

        //  ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(productResponse, response.getBody());
        verify(productService, times(1)).updateProduct(1L, request);
    }

    @Test
    void deleteProduct_ShouldReturnSuccessMessage() {
        //  ARRANGE
        doNothing().when(productService).deleteProduct(1L);

        //  ACT
        ResponseEntity<ApiResponseDTO> response = productController.deleteProduct(1L);

        //  ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Product deleted successfully", response.getBody().getMessage());
        assertTrue(response.getBody().isSuccess());
        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    void activateProduct_ShouldReturnSuccessMessage() {
        //  ARRANGE
        doNothing().when(productService).activateProduct(1L);

        //  ACT
        ResponseEntity<ApiResponseDTO> response = productController.activateProduct(1L);

        //  ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Product activated successfully", response.getBody().getMessage());
        assertTrue(response.getBody().isSuccess());
        verify(productService, times(1)).activateProduct(1L);
    }
}