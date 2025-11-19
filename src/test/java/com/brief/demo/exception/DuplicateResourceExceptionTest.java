package com.brief.demo.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DuplicateResourceExceptionTest {

    @Test
    void duplicateResourceException_ShouldCreateWithMessage() {
        // 🟢 ARRANGE
        String message = "Resource already exists";

        // 🟢 ACT
        DuplicateResourceException exception = new DuplicateResourceException(message);

        // 🟢 ASSERT
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void duplicateResourceException_ShouldBeInstanceOfRuntimeException() {
        // 🟢 ARRANGE
        DuplicateResourceException exception = new DuplicateResourceException("Test");

        // 🟢 ASSERT
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void duplicateResourceException_WithDifferentMessages() {
        // 🟢 ARRANGE
        String[] messages = {
                "User already exists",
                "Product SKU must be unique",
                "Warehouse name already taken",
                ""
        };

        for (String message : messages) {
            // 🟢 ACT
            DuplicateResourceException exception = new DuplicateResourceException(message);

            // 🟢 ASSERT
            assertEquals(message, exception.getMessage());
        }
    }

    @Test
    void duplicateResourceException_ShouldHaveCorrectConstructor() {
        // 🟢 ARRANGE
        Class<DuplicateResourceException> exceptionClass = DuplicateResourceException.class;

        // 🟢 ACT & ASSERT
        assertDoesNotThrow(() -> {
            var constructor = exceptionClass.getConstructor(String.class);
            assertNotNull(constructor);
        });
    }
}