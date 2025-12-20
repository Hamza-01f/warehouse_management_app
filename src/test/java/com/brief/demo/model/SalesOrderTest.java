package com.brief.demo.model;

import com.brief.demo.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SalesOrderTest {

    private SalesOrder salesOrder;
    private User client;
    private Warehouse warehouse;

    @BeforeEach
    void setUp() {
        client = new User();
        client.setId(1L);

        warehouse = new Warehouse();
        warehouse.setId(1L);

        salesOrder = SalesOrder.builder()
                .client(client)
                .warehouse(warehouse)
                .status(OrderStatus.CREATED)
                .country("USA")
                .city("New York")
                .street("123 Main St")
                .zipCode("10001")
                .build();
    }

    @Test
    void salesOrder_ShouldCreateWithDefaultValues() {
        // 🟢 ARRANGE
        SalesOrder newSalesOrder = new SalesOrder();

        // 🟢 ACT - Manually call the @PrePersist logic
        newSalesOrder.onCreate();

        // 🟢 ASSERT
        assertNotNull(newSalesOrder.getCreatedAt());
        assertEquals(OrderStatus.CREATED, newSalesOrder.getStatus());
        assertNotNull(newSalesOrder.getOrderLines());
        assertTrue(newSalesOrder.getOrderLines().isEmpty());
    }

    @Test
    void salesOrder_ShouldHaveCorrectFields() {
        // 🟢 ASSERT
        assertEquals(client, salesOrder.getClient());
        assertEquals(warehouse, salesOrder.getWarehouse());
        assertEquals(OrderStatus.CREATED, salesOrder.getStatus());
        assertEquals("USA", salesOrder.getCountry());
        assertEquals("New York", salesOrder.getCity());
        assertEquals("123 Main St", salesOrder.getStreet());
        assertEquals("10001", salesOrder.getZipCode());
        assertNotNull(salesOrder.getOrderLines());
    }

    @Test
    void salesOrder_ShouldUpdateFieldsCorrectly() {
        // 🟢 ARRANGE
        User newClient = new User();
        newClient.setId(2L);

        Warehouse newWarehouse = new Warehouse();
        newWarehouse.setId(2L);

        LocalDateTime reservedAt = LocalDateTime.now().plusHours(1);
        LocalDateTime shippedAt = LocalDateTime.now().plusDays(1);
        LocalDateTime deliveredAt = LocalDateTime.now().plusDays(2);

        // 🟢 ACT
        salesOrder.setClient(newClient);
        salesOrder.setWarehouse(newWarehouse);
        salesOrder.setStatus(OrderStatus.RESERVED);
        salesOrder.setCountry("Canada");
        salesOrder.setCity("Toronto");
        salesOrder.setStreet("456 Oak St");
        salesOrder.setZipCode("M5H 2N2");
        salesOrder.setReservedAt(reservedAt);
        salesOrder.setShippedAt(shippedAt);
        salesOrder.setDeliveredAt(deliveredAt);

        // 🟢 ASSERT
        assertEquals(newClient, salesOrder.getClient());
        assertEquals(newWarehouse, salesOrder.getWarehouse());
        assertEquals(OrderStatus.RESERVED, salesOrder.getStatus());
        assertEquals("Canada", salesOrder.getCountry());
        assertEquals("Toronto", salesOrder.getCity());
        assertEquals("456 Oak St", salesOrder.getStreet());
        assertEquals("M5H 2N2", salesOrder.getZipCode());
        assertEquals(reservedAt, salesOrder.getReservedAt());
        assertEquals(shippedAt, salesOrder.getShippedAt());
        assertEquals(deliveredAt, salesOrder.getDeliveredAt());
    }

    @Test
    void canBeReserved_WhenCreated_ShouldReturnTrue() {
        // 🟢 ARRANGE
        salesOrder.setStatus(OrderStatus.CREATED);

        // 🟢 ACT & ASSERT
        assertTrue(salesOrder.canBeReserved());
    }

    @Test
    void canBeReserved_WhenNotCreated_ShouldReturnFalse() {
        // 🟢 ARRANGE
        OrderStatus[] nonCreatableStatuses = {
                OrderStatus.RESERVED,
                OrderStatus.PARTIALLY_FULFILLED,
                OrderStatus.SHIPPED,
                OrderStatus.DELIVERED,
                OrderStatus.CANCELED
        };

        for (OrderStatus status : nonCreatableStatuses) {
            salesOrder.setStatus(status);
            // 🟢 ACT & ASSERT
            assertFalse(salesOrder.canBeReserved(), "Should not be reservable in status: " + status);
        }
    }

    @Test
    void canBeShipped_WhenReserved_ShouldReturnTrue() {
        // 🟢 ARRANGE
        salesOrder.setStatus(OrderStatus.RESERVED);

        // 🟢 ACT & ASSERT
        assertTrue(salesOrder.canBeShipped());
    }

    @Test
    void canBeShipped_WhenNotReserved_ShouldReturnFalse() {
        // 🟢 ARRANGE
        OrderStatus[] nonShippableStatuses = {
                OrderStatus.CREATED,
                OrderStatus.PARTIALLY_FULFILLED,
                OrderStatus.SHIPPED,
                OrderStatus.DELIVERED,
                OrderStatus.CANCELED
        };

        for (OrderStatus status : nonShippableStatuses) {
            salesOrder.setStatus(status);
            // 🟢 ACT & ASSERT
            assertFalse(salesOrder.canBeShipped(), "Should not be shippable in status: " + status);
        }
    }

    @Test
    void canBeDelivered_WhenShipped_ShouldReturnTrue() {
        // 🟢 ARRANGE
        salesOrder.setStatus(OrderStatus.SHIPPED);

        // 🟢 ACT & ASSERT
        assertTrue(salesOrder.canBeDelivered());
    }

    @Test
    void canBeDelivered_WhenNotShipped_ShouldReturnFalse() {
        // 🟢 ARRANGE
        OrderStatus[] nonDeliverableStatuses = {
                OrderStatus.CREATED,
                OrderStatus.RESERVED,
                OrderStatus.PARTIALLY_FULFILLED,
                OrderStatus.DELIVERED,
                OrderStatus.CANCELED
        };

        for (OrderStatus status : nonDeliverableStatuses) {
            salesOrder.setStatus(status);
            // 🟢 ACT & ASSERT
            assertFalse(salesOrder.canBeDelivered(), "Should not be deliverable in status: " + status);
        }
    }

    @Test
    void salesOrder_EqualsAndHashCode_WhenDifferentIds() {
        // 🟢 ARRANGE
        SalesOrder so1 = new SalesOrder();
        so1.setId(1L);

        SalesOrder so2 = new SalesOrder();
        so2.setId(2L);

        // 🟢 ASSERT
        assertNotEquals(so1, so2);
    }

    @Test
    void salesOrder_Equals_WhenNull() {
        // 🟢 ARRANGE
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setId(1L);

        // 🟢 ASSERT
        assertNotEquals(null, salesOrder);
    }

    @Test
    void salesOrder_Equals_WhenSameObject() {
        // 🟢 ARRANGE
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setId(1L);

        // 🟢 ASSERT
        assertEquals(salesOrder, salesOrder);
    }

    @Test
    void salesOrder_Equals_WhenDifferentClass() {
        // 🟢 ARRANGE
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setId(1L);

        // 🟢 ASSERT
        assertNotEquals("not a sales order", salesOrder);
    }

    @Test
    void salesOrder_ToString_IsNotNull() {
        // 🟢 ARRANGE
        salesOrder.setId(1L);

        // 🟢 ACT
        String toString = salesOrder.toString();

        // 🟢 ASSERT
        assertNotNull(toString);
    }
}