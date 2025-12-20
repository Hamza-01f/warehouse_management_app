package com.brief.demo.exception;

import com.brief.demo.dto.response.ApiResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void handleResourceNotFound_ShouldReturnNotFoundResponse() {
        // 🟢 ARRANGE
        ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");

        // 🟢 ACT
        ResponseEntity<ApiResponseDTO> response = globalExceptionHandler.handleResourceNotFound(exception);

        // 🟢 ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        ApiResponseDTO body = response.getBody();
        assertNotNull(body);
        assertEquals("Resource not found", body.getMessage());
        assertFalse(body.isSuccess());
    }

    @Test
    void handleDuplicateResource_ShouldReturnConflictResponse() {
        // 🟢 ARRANGE
        DuplicateResourceException exception = new DuplicateResourceException("Duplicate resource");

        // 🟢 ACT
        ResponseEntity<ApiResponseDTO> response = globalExceptionHandler.handleDuplicateResource(exception);

        // 🟢 ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

        ApiResponseDTO body = response.getBody();
        assertNotNull(body);
        assertEquals("Duplicate resource", body.getMessage());
        assertFalse(body.isSuccess());
    }

    @Test
    void handleUnauthorized_ShouldReturnUnauthorizedResponse() {
        // 🟢 ARRANGE
        UnauthorizedException exception = new UnauthorizedException("Unauthorized access");

        // 🟢 ACT
        ResponseEntity<ApiResponseDTO> response = globalExceptionHandler.handleUnauthorized(exception);

        // 🟢 ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        ApiResponseDTO body = response.getBody();
        assertNotNull(body);
        assertEquals("Unauthorized access", body.getMessage());
        assertFalse(body.isSuccess());
    }

    @Test
    void handleGenericException_ShouldReturnInternalServerErrorResponse() {
        // 🟢 ARRANGE
        Exception exception = new Exception("Generic error occurred");

        // 🟢 ACT
        ResponseEntity<ApiResponseDTO> response = globalExceptionHandler.handleGenericException(exception);

        // 🟢 ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        ApiResponseDTO body = response.getBody();
        assertNotNull(body);
        assertEquals("An error occurred: Generic error occurred", body.getMessage());
        assertFalse(body.isSuccess());
    }

    @Test
    void handleGenericException_WithNullMessage_ShouldHandleGracefully() {
        // 🟢 ARRANGE
        Exception exception = new Exception();

        // 🟢 ACT
        ResponseEntity<ApiResponseDTO> response = globalExceptionHandler.handleGenericException(exception);

        // 🟢 ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        ApiResponseDTO body = response.getBody();
        assertNotNull(body);
        assertTrue(body.getMessage().startsWith("An error occurred: "));
        assertFalse(body.isSuccess());
    }

    @Test
    void handleGenericException_WithRuntimeException_ShouldHandleGracefully() {
        // 🟢 ARRANGE
        RuntimeException exception = new RuntimeException("Runtime error");

        // 🟢 ACT
        ResponseEntity<ApiResponseDTO> response = globalExceptionHandler.handleGenericException(exception);

        // 🟢 ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        ApiResponseDTO body = response.getBody();
        assertNotNull(body);
        assertEquals("An error occurred: Runtime error", body.getMessage());
        assertFalse(body.isSuccess());
    }

    @Test
    void globalExceptionHandler_ShouldBeAnnotatedWithRestControllerAdvice() {
        // 🟢 ARRANGE
        Class<?> handlerClass = globalExceptionHandler.getClass();

        // 🟢 ASSERT
        assertTrue(handlerClass.isAnnotationPresent(org.springframework.web.bind.annotation.RestControllerAdvice.class));
    }
}