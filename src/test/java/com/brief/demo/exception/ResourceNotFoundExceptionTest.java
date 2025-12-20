package com.brief.demo.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceNotFoundExceptionTest {

    @Test
    void resourceNotFoundException_ShouldCreateWithMessage() {
        // 🟢 ARRANGE
        String message = "Resource not found";

        // 🟢 ACT
        ResourceNotFoundException exception = new ResourceNotFoundException(message);

        // 🟢 ASSERT
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void resourceNotFoundException_ShouldBeInstanceOfRuntimeException() {
        // 🟢 ARRANGE
        ResourceNotFoundException exception = new ResourceNotFoundException("Test");

        // 🟢 ASSERT
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void resourceNotFoundException_WithDifferentMessages() {
        // 🟢 ARRANGE
        String[] messages = {
                "User not found",
                "Product not found",
                "Order not found",
                "Inventory record not found",
                ""
        };

        for (String message : messages) {
            // 🟢 ACT
            ResourceNotFoundException exception = new ResourceNotFoundException(message);

            // 🟢 ASSERT
            assertEquals(message, exception.getMessage());
        }
    }

    @Test
    void resourceNotFoundException_ShouldHaveCorrectConstructor() {
        // 🟢 ARRANGE
        Class<ResourceNotFoundException> exceptionClass = ResourceNotFoundException.class;

        // 🟢 ACT & ASSERT
        assertDoesNotThrow(() -> {
            var constructor = exceptionClass.getConstructor(String.class);
            assertNotNull(constructor);
        });
    }
}