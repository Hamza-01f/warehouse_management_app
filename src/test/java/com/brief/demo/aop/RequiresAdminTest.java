package com.brief.demo.aop;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static org.junit.jupiter.api.Assertions.*;

class RequiresAdminTest {

    @Test
    void requiresAdminAnnotation_ShouldBeProperlyDefined() {
        // 🟢 ARRANGE
        Class<RequiresAdmin> annotationClass = RequiresAdmin.class;

        // 🟢 ASSERT
        assertTrue(annotationClass.isAnnotation());
        assertTrue(annotationClass.isAnnotationPresent(Retention.class));

        // Check retention policy
        Retention retention = annotationClass.getAnnotation(Retention.class);
        assertEquals(RetentionPolicy.RUNTIME, retention.value());
    }

    @Test
    void requiresAdminAnnotation_ShouldBeRuntimeRetention() {
        // 🟢 ARRANGE
        Class<RequiresAdmin> annotationClass = RequiresAdmin.class;

        // 🟢 ACT
        Retention retention = annotationClass.getAnnotation(Retention.class);

        // 🟢 ASSERT
        assertNotNull(retention);
        assertEquals(RetentionPolicy.RUNTIME, retention.value());
    }
}