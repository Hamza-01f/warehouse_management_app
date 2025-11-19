package com.brief.demo.dto.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestDTOTest {

    @Test
    void loginRequestDTO_ShouldHaveCorrectFields() {
        // 🟢 ARRANGE
        LoginRequestDTO dto = new LoginRequestDTO();

        // 🟢 ACT
        dto.setEmail("test@example.com");
        dto.setPassword("password123");

        // 🟢 ASSERT
        assertEquals("test@example.com", dto.getEmail());
        assertEquals("password123", dto.getPassword());
    }

    @Test
    void loginRequestDTO_ShouldHandleNullValues() {
        // 🟢 ARRANGE
        LoginRequestDTO dto = new LoginRequestDTO();

        // 🟢 ACT
        dto.setEmail(null);
        dto.setPassword(null);

        // 🟢 ASSERT
        assertNull(dto.getEmail());
        assertNull(dto.getPassword());
    }

    @Test
    void loginRequestDTO_EqualsAndHashCode() {
        // 🟢 ARRANGE
        LoginRequestDTO dto1 = new LoginRequestDTO();
        dto1.setEmail("test@example.com");
        dto1.setPassword("password123");

        LoginRequestDTO dto2 = new LoginRequestDTO();
        dto2.setEmail("test@example.com");
        dto2.setPassword("password123");

        // 🟢 ASSERT
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
}