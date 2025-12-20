package com.brief.demo.model;

import com.brief.demo.enums.MovementType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InventoryMovementTest {

    private InventoryMovement inventoryMovement;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        inventory = new Inventory();
        inventory.setId(1L);

        inventoryMovement = InventoryMovement.builder()
                .inventory(inventory)
                .type(MovementType.IN)
                .quantity(50)
                .build();
    }

    @Test
    void inventoryMovement_ShouldCreateWithDefaultValues() {
        // 🟢 ARRANGE
        InventoryMovement newMovement = new InventoryMovement();

        // 🟢 ACT - Manually call the @PrePersist logic
        newMovement.onCreate();

        // 🟢 ASSERT
        assertNotNull(newMovement.getOccurredAt());
    }

    @Test
    void inventoryMovement_ShouldHaveCorrectFields() {
        // 🟢 ASSERT
        assertEquals(inventory, inventoryMovement.getInventory());
        assertEquals(MovementType.IN, inventoryMovement.getType());
        assertEquals(50, inventoryMovement.getQuantity());
        assertNotNull(inventoryMovement.getOccurredAt());
    }

    @Test
    void inventoryMovement_ShouldHandleAllMovementTypes() {
        // 🟢 ARRANGE
        MovementType[] movementTypes = {MovementType.IN, MovementType.OUT, MovementType.ADJ};

        for (MovementType type : movementTypes) {
            // 🟢 ACT
            InventoryMovement movement = InventoryMovement.builder()
                    .inventory(inventory)
                    .type(type)
                    .quantity(10)
                    .build();

            // 🟢 ASSERT
            assertEquals(type, movement.getType());
        }
    }

    @Test
    void inventoryMovement_ShouldUpdateFieldsCorrectly() {
        // 🟢 ARRANGE
        Inventory newInventory = new Inventory();
        newInventory.setId(2L);

        LocalDateTime newOccurredAt = LocalDateTime.now().minusDays(1);

        // 🟢 ACT
        inventoryMovement.setInventory(newInventory);
        inventoryMovement.setType(MovementType.OUT);
        inventoryMovement.setQuantity(25);
        inventoryMovement.setOccurredAt(newOccurredAt);

        // 🟢 ASSERT
        assertEquals(newInventory, inventoryMovement.getInventory());
        assertEquals(MovementType.OUT, inventoryMovement.getType());
        assertEquals(25, inventoryMovement.getQuantity());
        assertEquals(newOccurredAt, inventoryMovement.getOccurredAt());
    }

    @Test
    void inventoryMovement_WhenOccurredAtIsNull_ShouldSetDefaultOnPersist() {
        // 🟢 ARRANGE
        InventoryMovement movement = new InventoryMovement();
        movement.setOccurredAt(null);

        // 🟢 ACT - Simulate @PrePersist
        movement.onCreate();

        // 🟢 ASSERT
        assertNotNull(movement.getOccurredAt());
    }

    @Test
    void inventoryMovement_WhenOccurredAtIsSet_ShouldNotOverride() {
        // 🟢 ARRANGE
        LocalDateTime customTime = LocalDateTime.now().minusHours(5);
        InventoryMovement movement = new InventoryMovement();
        movement.setOccurredAt(customTime);

        // 🟢 ACT - Simulate @PrePersist
        movement.onCreate();

        // 🟢 ASSERT
        assertEquals(customTime, movement.getOccurredAt());
    }


    @Test
    void inventoryMovement_EqualsAndHashCode_WhenDifferentIds() {
        // 🟢 ARRANGE
        InventoryMovement movement1 = new InventoryMovement();
        movement1.setId(1L);

        InventoryMovement movement2 = new InventoryMovement();
        movement2.setId(2L);

        // 🟢 ASSERT
        assertNotEquals(movement1, movement2);
    }

    @Test
    void inventoryMovement_Equals_WhenNull() {
        // 🟢 ARRANGE
        InventoryMovement movement = new InventoryMovement();
        movement.setId(1L);

        // 🟢 ASSERT
        assertNotEquals(null, movement);
    }

    @Test
    void inventoryMovement_Equals_WhenSameObject() {
        // 🟢 ARRANGE
        InventoryMovement movement = new InventoryMovement();
        movement.setId(1L);

        // 🟢 ASSERT
        assertEquals(movement, movement);
    }

    @Test
    void inventoryMovement_Equals_WhenDifferentClass() {
        // 🟢 ARRANGE
        InventoryMovement movement = new InventoryMovement();
        movement.setId(1L);

        // 🟢 ASSERT
        assertNotEquals("not an inventory movement", movement);
    }

    @Test
    void inventoryMovement_ToString_IsNotNull() {
        // 🟢 ARRANGE
        inventoryMovement.setId(1L);

        // 🟢 ACT
        String toString = inventoryMovement.toString();

        // 🟢 ASSERT
        assertNotNull(toString);
    }
}