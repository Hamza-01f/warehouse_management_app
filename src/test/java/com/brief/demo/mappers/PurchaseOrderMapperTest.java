package com.brief.demo.mappers;

import com.brief.demo.dto.request.PurchaseOrderRequestDTO;
import com.brief.demo.dto.response.PurchaseOrderResponseDTO;
import com.brief.demo.enums.POStatus;
import com.brief.demo.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderMapperTest {

    @Mock
    private SupplierMapper supplierMapper;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private BackOrderMapper backOrderMapper;

    @InjectMocks
    private PurchaseOrderMapper purchaseOrderMapper;

    private PurchaseOrder purchaseOrder;
    private Supplier supplier;
    private Product product;
    private BackOrder backOrder;

    @BeforeEach
    void setUp() {
        supplier = Supplier.builder()
                .id(1L)
                .name("Test Supplier")
                .build();

        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .build();

        backOrder = BackOrder.builder()
                .id(1L)
                .quantity(10)
                .build();

        POLine poLine = POLine.builder()
                .id(1L)
                .product(product)
                .quantity(10)
                .quantityReceived(0)
                .price(BigDecimal.valueOf(99.00))
                .build();

        purchaseOrder = PurchaseOrder.builder()
                .id(1L)
                .supplier(supplier)
                .backOrder(backOrder)
                .status(POStatus.CREATED)
                .createdAt(LocalDate.now())
                .approvedAt(LocalDate.now().plusDays(1))
                .receivedAt(LocalDate.now().plusDays(2))
                .orderLines(Arrays.asList(poLine))
                .build();
    }

    @Test
    void toEntity_ShouldCreatePurchaseOrderWithDefaultStatus() {
        //  ARRANGE
        PurchaseOrderRequestDTO request = new PurchaseOrderRequestDTO();

        //  ACT
        PurchaseOrder result = purchaseOrderMapper.toEntity(request);

        //  ASSERT
        assertNotNull(result);
        assertEquals(POStatus.CREATED, result.getStatus());
    }

    @Test
    void toResponse_ShouldMapAllFields() {
        //  ARRANGE
        when(supplierMapper.toResponse(any(Supplier.class))).thenReturn(new com.brief.demo.dto.response.SupplierResponseDTO());
        when(backOrderMapper.toResponse(any(BackOrder.class))).thenReturn(new com.brief.demo.dto.response.BackOrderResponseDTO());
        when(productMapper.toResponse(any(Product.class))).thenReturn(new com.brief.demo.dto.response.ProductResponseDTO());

        //  ACT
        PurchaseOrderResponseDTO response = purchaseOrderMapper.toResponse(purchaseOrder);

        //  ASSERT
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(POStatus.CREATED, response.getStatus());
        assertNotNull(response.getSupplier());
        assertNotNull(response.getBackOrder());
        assertNotNull(response.getOrderLines());
        assertEquals(1, response.getOrderLines().size());
    }

    @Test
    void toResponse_WhenNoBackOrder_ShouldHandleGracefully() {
        //  ARRANGE
        purchaseOrder.setBackOrder(null);
        when(supplierMapper.toResponse(any(Supplier.class))).thenReturn(new com.brief.demo.dto.response.SupplierResponseDTO());
        when(productMapper.toResponse(any(Product.class))).thenReturn(new com.brief.demo.dto.response.ProductResponseDTO());

        //  ACT
        PurchaseOrderResponseDTO response = purchaseOrderMapper.toResponse(purchaseOrder);

        //  ASSERT
        assertNotNull(response);
        assertNull(response.getBackOrder());
    }

    @Test
    void toResponse_WhenNoOrderLines_ShouldHandleGracefully() {
        //  ARRANGE
        purchaseOrder.setOrderLines(null);
        when(supplierMapper.toResponse(any(Supplier.class))).thenReturn(new com.brief.demo.dto.response.SupplierResponseDTO());

        //  ACT
        PurchaseOrderResponseDTO response = purchaseOrderMapper.toResponse(purchaseOrder);

        //  ASSERT
        assertNotNull(response);
        assertNull(response.getOrderLines());
    }
}