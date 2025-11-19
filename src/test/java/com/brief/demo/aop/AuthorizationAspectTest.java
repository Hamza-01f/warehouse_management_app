package com.brief.demo.aop;

import com.brief.demo.enums.Role;
import com.brief.demo.exception.UnauthorizedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthorizationAspectTest {

    @InjectMocks
    private AuthorizationAspect authorizationAspect;

    @Test
    void checkAdminAccess_WhenUserIsAdmin_ShouldNotThrowException() {
        // 🟢 ARRANGE & ACT & ASSERT
        // Current implementation always returns ADMIN, so this should pass
        assertDoesNotThrow(() -> authorizationAspect.checkAdminAccess());
    }

    @Test
    void checkWarehouseManagerAccess_WhenUserIsAdmin_ShouldNotThrowException() {
        // 🟢 ARRANGE & ACT & ASSERT
        // Current implementation always returns ADMIN, so this should pass
        assertDoesNotThrow(() -> authorizationAspect.checkWarehouseManagerAccess());
    }

    @Test
    void aspectAnnotations_ShouldBeProperlyConfigured() {
        // 🟢 ARRANGE
        Class<?> aspectClass = authorizationAspect.getClass();

        // 🟢 ASSERT
        assertTrue(aspectClass.isAnnotationPresent(org.aspectj.lang.annotation.Aspect.class));
        assertTrue(aspectClass.isAnnotationPresent(org.springframework.stereotype.Component.class));
    }

    @Test
    void methodSignatures_ShouldBeCorrect() {
        // 🟢 ARRANGE
        Class<?> aspectClass = authorizationAspect.getClass();

        // 🟢 ACT & ASSERT
        assertDoesNotThrow(() -> {
            var method = aspectClass.getDeclaredMethod("checkAdminAccess");
            assertEquals(void.class, method.getReturnType());
        });

        assertDoesNotThrow(() -> {
            var method = aspectClass.getDeclaredMethod("checkWarehouseManagerAccess");
            assertEquals(void.class, method.getReturnType());
        });
    }
}