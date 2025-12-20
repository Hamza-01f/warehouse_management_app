package com.brief.demo.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnauthorizedExceptionTest {

    @Test
    void unauthorizedException_ShouldCreateWithMessage() {
        // 🟢 ARRANGE
        String message = "Access denied";

        // 🟢 ACT
        UnauthorizedException exception = new UnauthorizedException(message);

        // 🟢 ASSERT
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void unauthorizedException_ShouldBeInstanceOfRuntimeException() {
        // 🟢 ARRANGE
        UnauthorizedException exception = new UnauthorizedException("Test");

        // 🟢 ASSERT
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void unauthorizedException_WithDifferentMessages() {
        // 🟢 ARRANGE
        String[] messages = {
                "Invalid credentials",
                "Admin access required",
                "Session expired",
                "Insufficient permissions",
                ""
        };

        for (String message : messages) {
            // 🟢 ACT
            UnauthorizedException exception = new UnauthorizedException(message);

            // 🟢 ASSERT
            assertEquals(message, exception.getMessage());
        }
    }

    @Test
    void unauthorizedException_ShouldHaveCorrectConstructor() {
        // 🟢 ARRANGE
        Class<UnauthorizedException> exceptionClass = UnauthorizedException.class;

        // 🟢 ACT & ASSERT
        assertDoesNotThrow(() -> {
            var constructor = exceptionClass.getConstructor(String.class);
            assertNotNull(constructor);
        });
    }
}