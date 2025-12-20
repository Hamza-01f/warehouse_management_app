package com.brief.demo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {

    private Inventory inventory;
    private Warehouse warehouse;
    private Product product;

    @BeforeEach
    void setUp() {
        warehouse = new Warehouse();
        warehouse.setId(1L);

        product = new Product();
        product.setId(1L);

        inventory = Inventory.builder()
                .warehouse(warehouse)
                .product(product)
                .quantityOnHand(100)
                .quantityReserved(20)
                .build();
    }

    @Test
    void inventory_ShouldCreateWithDefaultValues() {
        // 🟢 ARRANGE
        Inventory newInventory = new Inventory();

        // 🟢 ASSERT
        assertEquals(0, newInventory.getQuantityOnHand());
        assertEquals(0, newInventory.getQuantityReserved());
    }

    @Test
    void inventory_ShouldHaveCorrectFields() {
        // 🟢 ASSERT
        assertEquals(warehouse, inventory.getWarehouse());
        assertEquals(product, inventory.getProduct());
        assertEquals(100, inventory.getQuantityOnHand());
        assertEquals(20, inventory.getQuantityReserved());
    }

    @Test
    void getAvailableQuantity_ShouldCalculateCorrectly() {
        // 🟢 ARRANGE
        inventory.setQuantityOnHand(100);
        inventory.setQuantityReserved(30);

        // 🟢 ACT
        Integer availableQuantity = inventory.getAvailable_Quantity();

        // 🟢 ASSERT
        assertEquals(70, availableQuantity);
    }

    @Test
    void getAvailableQuantity_WhenZeroInventory_ShouldReturnZero() {
        // 🟢 ARRANGE
        inventory.setQuantityOnHand(0);
        inventory.setQuantityReserved(0);

        // 🟢 ACT
        Integer availableQuantity = inventory.getAvailable_Quantity();

        // 🟢 ASSERT
        assertEquals(0, availableQuantity);
    }

    @Test
    void getAvailableQuantity_WhenNoReservations_ShouldEqualQuantityOnHand() {
        // 🟢 ARRANGE
        inventory.setQuantityOnHand(50);
        inventory.setQuantityReserved(0);

        // 🟢 ACT
        Integer availableQuantity = inventory.getAvailable_Quantity();

        // 🟢 ASSERT
        assertEquals(50, availableQuantity);
    }

    @Test
    void reserveQuantity_ShouldIncreaseReservedQuantity() {
        // 🟢 ARRANGE
        inventory.setQuantityOnHand(100);
        inventory.setQuantityReserved(10);

        // 🟢 ACT
        inventory.reserveQuantity(20);

        // 🟢 ASSERT
        assertEquals(30, inventory.getQuantityReserved());
        assertEquals(70, inventory.getAvailable_Quantity());
    }

    @Test
    void reserveQuantity_WhenInsufficientAvailable_ShouldThrowException() {
        // 🟢 ARRANGE
        inventory.setQuantityOnHand(100);
        inventory.setQuantityReserved(90); // Only 10 available

        // 🟢 ACT & ASSERT
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> inventory.reserveQuantity(20));

        assertEquals("Insufficient available quantity", exception.getMessage());
        assertEquals(90, inventory.getQuantityReserved()); // Should remain unchanged
    }

    @Test
    void releaseReservedQuantity_ShouldDecreaseReservedQuantity() {
        // 🟢 ARRANGE
        inventory.setQuantityOnHand(100);
        inventory.setQuantityReserved(30);

        // 🟢 ACT
        inventory.releaseReservedQuantity(15);

        // 🟢 ASSERT
        assertEquals(15, inventory.getQuantityReserved());
        assertEquals(85, inventory.getAvailable_Quantity());
    }

    @Test
    void releaseReservedQuantity_WhenReleasingMoreThanReserved_ShouldThrowException() {
        // 🟢 ARRANGE
        inventory.setQuantityOnHand(100);
        inventory.setQuantityReserved(10);

        // 🟢 ACT & ASSERT
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> inventory.releaseReservedQuantity(15));

        assertEquals("Cannot release more than reserved quantity", exception.getMessage());
        assertEquals(10, inventory.getQuantityReserved()); // Should remain unchanged
    }

    @Test
    void adjustQuantityOnHand_ShouldUpdateQuantity() {
        // 🟢 ARRANGE
        inventory.setQuantityOnHand(100);
        inventory.setQuantityReserved(20);

        // 🟢 ACT
        inventory.adjustQuantityOnHand(150);

        // 🟢 ASSERT
        assertEquals(150, inventory.getQuantityOnHand());
        assertEquals(130, inventory.getAvailable_Quantity());
    }

    @Test
    void adjustQuantityOnHand_WhenNewQuantityLessThanReserved_ShouldThrowException() {
        // 🟢 ARRANGE
        inventory.setQuantityOnHand(100);
        inventory.setQuantityReserved(50);

        // 🟢 ACT & ASSERT
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> inventory.adjustQuantityOnHand(40));

        assertEquals("Quantity on hand cannot be less than reserved quantity", exception.getMessage());
        assertEquals(100, inventory.getQuantityOnHand()); // Should remain unchanged
    }

    @Test
    void getAvailableQuantity_ShouldRecalculateAfterChanges() {
        // 🟢 ARRANGE
        inventory.setQuantityOnHand(80);
        inventory.setQuantityReserved(25);

        // 🟢 ACT & ASSERT
        assertEquals(55, inventory.getAvailable_Quantity());

        // Change values and verify recalculation
        inventory.setQuantityOnHand(100);
        inventory.setQuantityReserved(40);
        assertEquals(60, inventory.getAvailable_Quantity());
    }


    @Test
    void inventory_EqualsAndHashCode_WhenDifferentIds() {
        // 🟢 ARRANGE
        Inventory inventory1 = new Inventory();
        inventory1.setId(1L);

        Inventory inventory2 = new Inventory();
        inventory2.setId(2L);

        // 🟢 ASSERT
        assertNotEquals(inventory1, inventory2);
    }

    @Test
    void inventory_Equals_WhenNull() {
        // 🟢 ARRANGE
        Inventory inventory = new Inventory();
        inventory.setId(1L);

        // 🟢 ASSERT
        assertNotEquals(null, inventory);
    }

    @Test
    void inventory_Equals_WhenSameObject() {
        // 🟢 ARRANGE
        Inventory inventory = new Inventory();
        inventory.setId(1L);

        // 🟢 ASSERT
        assertEquals(inventory, inventory);
    }

    @Test
    void inventory_Equals_WhenDifferentClass() {
        // 🟢 ARRANGE
        Inventory inventory = new Inventory();
        inventory.setId(1L);

        // 🟢 ASSERT
        assertNotEquals("not an inventory", inventory);
    }

    @Test
    void inventory_ToString_IsNotNull() {
        // 🟢 ARRANGE
        inventory.setId(1L);

        // 🟢 ACT
        String toString = inventory.toString();

        // 🟢 ASSERT
        assertNotNull(toString);
    }

    @Test
    void getAvailableQuantity_MethodUpdatesAvailableQuantityField() {
        // 🟢 ARRANGE
        inventory.setQuantityOnHand(80);
        inventory.setQuantityReserved(25);

        // 🟢 ACT
        inventory.getAvailableQuantity(); // This should update available_quantity field

        // 🟢 ASSERT - We can't directly test the private field, but we can test the public method
        assertEquals(55, inventory.getAvailable_Quantity());
    }
}