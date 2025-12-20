package com.brief.demo.controller;

import com.brief.demo.dto.request.LoginRequestDTO;
import com.brief.demo.dto.request.RegisterRequestDTO;
import com.brief.demo.dto.response.AuthResponseDTO;
import com.brief.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private RegisterRequestDTO registerRequest;
    private LoginRequestDTO loginRequest;
    private AuthResponseDTO authResponse;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequestDTO();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password");

        loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        authResponse = new AuthResponseDTO();
//        authResponse.setMessage("Success");

    }

    @Test
    void register_ShouldReturnAuthResponse() {
        //  ARRANGE
        when(userService.register(any(RegisterRequestDTO.class))).thenReturn(authResponse);

        // ACT
        ResponseEntity<AuthResponseDTO> response = authController.register(registerRequest);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(authResponse, response.getBody());
        verify(userService, times(1)).register(registerRequest);
    }

//    @Test
//    void login_ShouldReturnAuthResponse() {
//        //  ARRANGE
//        when(userService.login(any(LoginRequestDTO.class))).thenReturn(authResponse);
//
//        //  ACT
//        ResponseEntity<AuthResponseDTO> response = authController.login(loginRequest);
//
//        //  ASSERT
//        assertNotNull(response);
//        assertEquals(200, response.getStatusCodeValue());
//        assertEquals(authResponse, response.getBody());
//        verify(userService, times(1)).login(loginRequest);
//    }
}