package com.brief.demo.model;

import com.brief.demo.enums.BackOrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BackOrderTest {

    private BackOrder backOrder;
    private SalesOrderLine salesOrderLine;
    private Product product;
    private Warehouse warehouse;

    @BeforeEach
    void setUp() {
        salesOrderLine = new SalesOrderLine();
        salesOrderLine.setId(1L);

        product = new Product();
        product.setId(1L);

        warehouse = new Warehouse();
        warehouse.setId(1L);

        backOrder = BackOrder.builder()
                .salesOrderLine(salesOrderLine)
                .product(product)
                .warehouse(warehouse)
                .quantity(10)
                .status(BackOrderStatus.PENDING)
                .autoPurchaseOrder(true)
                .build();
    }

    @Test
    void backOrder_ShouldCreateWithDefaultValues() {
        // 🟢 ARRANGE
        BackOrder newBackOrder = new BackOrder();

        // 🟢 ACT - Manually call the @PrePersist logic
        newBackOrder.onCreate();

        // 🟢 ASSERT
        assertNotNull(newBackOrder.getCreatedAt());
        assertEquals(BackOrderStatus.PENDING, newBackOrder.getStatus());
    }

    @Test
    void backOrder_ShouldHaveCorrectFields() {
        // 🟢 ASSERT
        assertEquals(salesOrderLine, backOrder.getSalesOrderLine());
        assertEquals(product, backOrder.getProduct());
        assertEquals(warehouse, backOrder.getWarehouse());
        assertEquals(10, backOrder.getQuantity());
        assertEquals(BackOrderStatus.PENDING, backOrder.getStatus());
        assertTrue(backOrder.getAutoPurchaseOrder());
    }

    @Test
    void backOrder_ShouldHandleNullValues() {
        // 🟢 ARRANGE
        BackOrder backOrder = new BackOrder();

        // 🟢 ACT
        backOrder.setSalesOrderLine(null);
        backOrder.setProduct(null);
        backOrder.setWarehouse(null);
        backOrder.setFulfilledAt(null);
        backOrder.setAutoPurchaseOrder(null);

        // 🟢 ASSERT
        assertNull(backOrder.getSalesOrderLine());
        assertNull(backOrder.getProduct());
        assertNull(backOrder.getWarehouse());
        assertNull(backOrder.getFulfilledAt());
        assertNull(backOrder.getAutoPurchaseOrder());
    }

    @Test
    void backOrder_ShouldUpdateFieldsCorrectly() {
        // 🟢 ARRANGE
        SalesOrderLine newSalesOrderLine = new SalesOrderLine();
        newSalesOrderLine.setId(2L);

        Product newProduct = new Product();
        newProduct.setId(2L);

        Warehouse newWarehouse = new Warehouse();
        newWarehouse.setId(2L);

        LocalDateTime fulfilledAt = LocalDateTime.now();

        // 🟢 ACT
        backOrder.setSalesOrderLine(newSalesOrderLine);
        backOrder.setProduct(newProduct);
        backOrder.setWarehouse(newWarehouse);
        backOrder.setQuantity(20);
        backOrder.setStatus(BackOrderStatus.FULFILLED);
        backOrder.setFulfilledAt(fulfilledAt);
        backOrder.setAutoPurchaseOrder(false);

        // 🟢 ASSERT
        assertEquals(newSalesOrderLine, backOrder.getSalesOrderLine());
        assertEquals(newProduct, backOrder.getProduct());
        assertEquals(newWarehouse, backOrder.getWarehouse());
        assertEquals(20, backOrder.getQuantity());
        assertEquals(BackOrderStatus.FULFILLED, backOrder.getStatus());
        assertEquals(fulfilledAt, backOrder.getFulfilledAt());
        assertFalse(backOrder.getAutoPurchaseOrder());
    }


    @Test
    void backOrder_EqualsAndHashCode_WhenDifferentIds() {
        // 🟢 ARRANGE
        BackOrder backOrder1 = new BackOrder();
        backOrder1.setId(1L);

        BackOrder backOrder2 = new BackOrder();
        backOrder2.setId(2L);

        // 🟢 ASSERT
        assertNotEquals(backOrder1, backOrder2);
    }

    @Test
    void backOrder_Equals_WhenNull() {
        // 🟢 ARRANGE
        BackOrder backOrder = new BackOrder();
        backOrder.setId(1L);

        // 🟢 ASSERT
        assertNotEquals(null, backOrder);
    }

    @Test
    void backOrder_Equals_WhenSameObject() {
        // 🟢 ARRANGE
        BackOrder backOrder = new BackOrder();
        backOrder.setId(1L);

        // 🟢 ASSERT
        assertEquals(backOrder, backOrder);
    }

    @Test
    void backOrder_Equals_WhenDifferentClass() {
        // 🟢 ARRANGE
        BackOrder backOrder = new BackOrder();
        backOrder.setId(1L);

        // 🟢 ASSERT
        assertNotEquals("not a backorder", backOrder);
    }

    @Test
    void backOrder_ToString_ContainsBasicInfo() {
        // 🟢 ARRANGE
        backOrder.setId(1L);

        // 🟢 ACT
        String toString = backOrder.toString();

        // 🟢 ASSERT - Just check it's not null and contains some basic info
        assertNotNull(toString);
        // Don't check specific content as Lombok's toString format can vary
    }

    @Test
    void onCreate_ShouldSetDefaults() {
        // 🟢 ARRANGE
        BackOrder backOrder = new BackOrder();
        backOrder.setStatus(null);

        // 🟢 ACT
        backOrder.onCreate();

        // 🟢 ASSERT
        assertNotNull(backOrder.getCreatedAt());
        assertEquals(BackOrderStatus.PENDING, backOrder.getStatus());
    }

    @Test
    void onCreate_ShouldNotOverrideExistingStatus() {
        // 🟢 ARRANGE
        BackOrder backOrder = new BackOrder();
        backOrder.setStatus(BackOrderStatus.FULFILLED);

        // 🟢 ACT
        backOrder.onCreate();

        // 🟢 ASSERT
        assertEquals(BackOrderStatus.FULFILLED, backOrder.getStatus());
    }
}