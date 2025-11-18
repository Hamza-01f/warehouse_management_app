package com.brief.demo.mappers;

import com.brief.demo.dto.request.RegisterRequestDTO;
import com.brief.demo.dto.response.AuthResponseDTO;
import com.brief.demo.model.User;
import com.brief.demo.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @InjectMocks
    private UserMapper userMapper;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .role(Role.CLIENT)
                .build();
    }

    @Test
    void toEntity_ShouldMapAllFields() {
        // 🟢 ARRANGE
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setEmail("jane.smith@example.com");
        request.setRole(Role.WAREHOUSE_MANAGER);

        // 🟢 ACT
        User result = userMapper.toEntity(request);

        // 🟢 ASSERT
        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals("jane.smith@example.com", result.getEmail());
        assertEquals(Role.WAREHOUSE_MANAGER, result.getRole());
    }

    @Test
    void toAuthResponse_ShouldMapAllFields() {
        //  ARRANGE
        String message = "Registration successful";

        //  ACT
        AuthResponseDTO response = userMapper.toAuthResponse(user, message);

        //  ASSERT
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("john.doe@example.com", response.getEmail());
        assertEquals(Role.CLIENT, response.getRole());
        assertEquals("Registration successful", response.getMessage());
    }
}