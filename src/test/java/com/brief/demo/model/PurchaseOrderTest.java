package com.brief.demo.model;

import com.brief.demo.enums.POStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PurchaseOrderTest {

    private PurchaseOrder purchaseOrder;
    private Supplier supplier;
    private BackOrder backOrder;

    @BeforeEach
    void setUp() {
        supplier = new Supplier();
        supplier.setId(1L);

        backOrder = new BackOrder();
        backOrder.setId(1L);

        purchaseOrder = PurchaseOrder.builder()
                .supplier(supplier)
                .status(POStatus.CREATED)
                .backOrder(backOrder)
                .build();
    }

    @Test
    void purchaseOrder_ShouldCreateWithDefaultValues() {
        // 🟢 ARRANGE
        PurchaseOrder newPurchaseOrder = new PurchaseOrder();

        // 🟢 ACT - Manually call the @PrePersist logic
        newPurchaseOrder.onCreate();

        // 🟢 ASSERT
        assertNotNull(newPurchaseOrder.getCreatedAt());
        assertEquals(POStatus.CREATED, newPurchaseOrder.getStatus());
        assertNotNull(newPurchaseOrder.getOrderLines());
        assertTrue(newPurchaseOrder.getOrderLines().isEmpty());
    }

    @Test
    void purchaseOrder_ShouldHaveCorrectFields() {
        // 🟢 ASSERT
        assertEquals(supplier, purchaseOrder.getSupplier());
        assertEquals(POStatus.CREATED, purchaseOrder.getStatus());
        assertEquals(backOrder, purchaseOrder.getBackOrder());
        assertNotNull(purchaseOrder.getOrderLines());
    }

    @Test
    void purchaseOrder_ShouldUpdateFieldsCorrectly() {
        // 🟢 ARRANGE
        Supplier newSupplier = new Supplier();
        newSupplier.setId(2L);

        LocalDate approvedAt = LocalDate.now().plusDays(1);
        LocalDate receivedAt = LocalDate.now().plusDays(2);

        // 🟢 ACT
        purchaseOrder.setSupplier(newSupplier);
        purchaseOrder.setStatus(POStatus.APPROVED);
        purchaseOrder.setApprovedAt(approvedAt);
        purchaseOrder.setReceivedAt(receivedAt);
        purchaseOrder.setBackOrder(null);

        // 🟢 ASSERT
        assertEquals(newSupplier, purchaseOrder.getSupplier());
        assertEquals(POStatus.APPROVED, purchaseOrder.getStatus());
        assertEquals(approvedAt, purchaseOrder.getApprovedAt());
        assertEquals(receivedAt, purchaseOrder.getReceivedAt());
        assertNull(purchaseOrder.getBackOrder());
    }

    @Test
    void purchaseOrder_ShouldHandleNullValues() {
        // 🟢 ARRANGE
        PurchaseOrder purchaseOrder = new PurchaseOrder();

        // 🟢 ACT
        purchaseOrder.setSupplier(null);
        purchaseOrder.setStatus(null);
        purchaseOrder.setCreatedAt(null);
        purchaseOrder.setApprovedAt(null);
        purchaseOrder.setReceivedAt(null);
        purchaseOrder.setOrderLines(null);
        purchaseOrder.setBackOrder(null);

        // 🟢 ASSERT
        assertNull(purchaseOrder.getSupplier());
        assertNull(purchaseOrder.getStatus());
        assertNull(purchaseOrder.getCreatedAt());
        assertNull(purchaseOrder.getApprovedAt());
        assertNull(purchaseOrder.getReceivedAt());
        assertNull(purchaseOrder.getOrderLines());
        assertNull(purchaseOrder.getBackOrder());
    }

    @Test
    void purchaseOrder_OrderLinesManagement() {
        // 🟢 ARRANGE
        POLine line1 = new POLine();
        line1.setId(1L);

        POLine line2 = new POLine();
        line2.setId(2L);

        // 🟢 ACT
        purchaseOrder.getOrderLines().add(line1);
        purchaseOrder.getOrderLines().add(line2);

        // 🟢 ASSERT
        assertEquals(2, purchaseOrder.getOrderLines().size());
        assertEquals(line1, purchaseOrder.getOrderLines().get(0));
        assertEquals(line2, purchaseOrder.getOrderLines().get(1));
    }

    @Test
    void onCreate_ShouldSetDefaults() {
        // 🟢 ARRANGE
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setStatus(null);
        purchaseOrder.setCreatedAt(null);

        // 🟢 ACT
        purchaseOrder.onCreate();

        // 🟢 ASSERT
        assertNotNull(purchaseOrder.getCreatedAt());
        assertEquals(POStatus.CREATED, purchaseOrder.getStatus());
    }

    @Test
    void onCreate_ShouldNotOverrideExistingValues() {
        // 🟢 ARRANGE
        LocalDate customDate = LocalDate.now().minusDays(5);
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setStatus(POStatus.APPROVED);
        purchaseOrder.setCreatedAt(customDate);

        // 🟢 ACT
        purchaseOrder.onCreate();

        // 🟢 ASSERT
        assertEquals(POStatus.APPROVED, purchaseOrder.getStatus());
        assertEquals(customDate, purchaseOrder.getCreatedAt());
    }


    @Test
    void purchaseOrder_EqualsAndHashCode_WhenDifferentIds() {
        // 🟢 ARRANGE
        PurchaseOrder po1 = new PurchaseOrder();
        po1.setId(1L);

        PurchaseOrder po2 = new PurchaseOrder();
        po2.setId(2L);

        // 🟢 ASSERT
        assertNotEquals(po1, po2);
    }

    @Test
    void purchaseOrder_Equals_WhenNull() {
        // 🟢 ARRANGE
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setId(1L);

        // 🟢 ASSERT
        assertNotEquals(null, purchaseOrder);
    }

    @Test
    void purchaseOrder_Equals_WhenSameObject() {
        // 🟢 ARRANGE
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setId(1L);

        // 🟢 ASSERT
        assertEquals(purchaseOrder, purchaseOrder);
    }

    @Test
    void purchaseOrder_Equals_WhenDifferentClass() {
        // 🟢 ARRANGE
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setId(1L);

        // 🟢 ASSERT
        assertNotEquals("not a purchase order", purchaseOrder);
    }

    @Test
    void purchaseOrder_ToString_IsNotNull() {
        // 🟢 ARRANGE
        purchaseOrder.setId(1L);

        // 🟢 ACT
        String toString = purchaseOrder.toString();

        // 🟢 ASSERT
        assertNotNull(toString);
    }
}