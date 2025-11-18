package com.brief.demo.mappers;

import com.brief.demo.dto.response.SalesOrderResponseDTO;
import com.brief.demo.enums.OrderStatus;
import com.brief.demo.model.SalesOrder;
import com.brief.demo.model.SalesOrderLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SalesOrderMapperTest {

    @InjectMocks
    private SalesOrderMapper salesOrderMapper;

    private SalesOrder salesOrder;

    @BeforeEach
    void setUp() {
        SalesOrderLine orderLine = SalesOrderLine.builder()
                .id(1L)
                .quantity(5)
                .quantityReserved(3)
                .quantityFulfilled(2)
                .backorderQuantity(2)
                .build();

        salesOrder = SalesOrder.builder()
                .id(1L)
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .reservedAt(LocalDateTime.now().plusHours(1))
                .shippedAt(LocalDateTime.now().plusDays(1))
                .deliveredAt(LocalDateTime.now().plusDays(2))
                .country("USA")
                .city("New York")
                .street("123 Main St")
                .zipCode("10001")
                .orderLines(Arrays.asList(orderLine))
                .build();
    }

    @Test
    void toResponse_ShouldMapAllFields() {
        //  ARRANGE - Already set up in beforeEach

        //  ACT
        SalesOrderResponseDTO response = salesOrderMapper.toResponse(salesOrder);

        //  ASSERT
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(OrderStatus.CREATED, response.getStatus());
        assertEquals("USA", response.getCountry());
        assertEquals("New York", response.getCity());
        assertEquals("123 Main St", response.getStreet());
        assertEquals("10001", response.getZipCode());
        assertNotNull(response.getOrderLines());
        assertEquals(1, response.getOrderLines().size());

        // Check order line mapping
        var orderLineResponse = response.getOrderLines().get(0);
        assertEquals(1L, orderLineResponse.getId());
        assertEquals(5, orderLineResponse.getQuantity());
        assertEquals(3, orderLineResponse.getQuantityReserved());
        assertEquals(2, orderLineResponse.getQuantityFulfilled());
        assertEquals(2, orderLineResponse.getBackorderQuantity());
    }

    @Test
    void toResponse_WhenNoOrderLines_ShouldHandleGracefully() {
        //  ARRANGE
        salesOrder.setOrderLines(null);

        //  ACT
        SalesOrderResponseDTO response = salesOrderMapper.toResponse(salesOrder);

        //  ASSERT
        assertNotNull(response);
        assertNull(response.getOrderLines());
    }

    @Test
    void toResponse_WhenNullDates_ShouldHandleGracefully() {
        //  ARRANGE
        salesOrder.setReservedAt(null);
        salesOrder.setShippedAt(null);
        salesOrder.setDeliveredAt(null);

        //  ACT
        SalesOrderResponseDTO response = salesOrderMapper.toResponse(salesOrder);

        //  ASSERT
        assertNotNull(response);
        assertNull(response.getReservedAt());
        assertNull(response.getShippedAt());
        assertNull(response.getDeliveredAt());
    }
}