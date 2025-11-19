package com.brief.demo.dto.request;

import com.brief.demo.enums.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestDTOTest {

    @Test
    void registerRequestDTO_ShouldHaveCorrectFields() {
        // 🟢 ARRANGE
        RegisterRequestDTO dto = new RegisterRequestDTO();

        // 🟢 ACT
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setPassword("password123");
        dto.setRole(Role.CLIENT);

        // 🟢 ASSERT
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("john.doe@example.com", dto.getEmail());
        assertEquals("password123", dto.getPassword());
        assertEquals(Role.CLIENT, dto.getRole());
    }

    @Test
    void registerRequestDTO_ShouldHandleNullValues() {
        // 🟢 ARRANGE
        RegisterRequestDTO dto = new RegisterRequestDTO();

        // 🟢 ACT
        dto.setFirstName(null);
        dto.setLastName(null);
        dto.setEmail(null);
        dto.setPassword(null);
        dto.setRole(null);

        // 🟢 ASSERT
        assertNull(dto.getFirstName());
        assertNull(dto.getLastName());
        assertNull(dto.getEmail());
        assertNull(dto.getPassword());
        assertNull(dto.getRole());
    }

    @Test
    void registerRequestDTO_WithAllRoles() {
        // 🟢 ARRANGE
        Role[] roles = {Role.ADMIN, Role.WAREHOUSE_MANAGER, Role.CLIENT};

        for (Role role : roles) {
            RegisterRequestDTO dto = new RegisterRequestDTO();
            dto.setFirstName("John");
            dto.setLastName("Doe");
            dto.setEmail("john.doe@example.com");
            dto.setPassword("password123");
            dto.setRole(role);

            // 🟢 ASSERT
            assertEquals(role, dto.getRole(), "Should set role correctly: " + role);
        }
    }
}