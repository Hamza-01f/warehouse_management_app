package com.brief.demo.exception;

import com.brief.demo.dto.response.ApiResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExceptionIntegrationTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void allExceptionHandlers_ShouldReturnCorrectStatusCodes() {
        // 🟢 ARRANGE
        ResourceNotFoundException notFoundEx = new ResourceNotFoundException("Not found");
        DuplicateResourceException duplicateEx = new DuplicateResourceException("Duplicate");
        UnauthorizedException unauthorizedEx = new UnauthorizedException("Unauthorized");
        Exception genericEx = new Exception("Generic error");

        // 🟢 ACT
        ResponseEntity<ApiResponseDTO> notFoundResponse = globalExceptionHandler.handleResourceNotFound(notFoundEx);
        ResponseEntity<ApiResponseDTO> duplicateResponse = globalExceptionHandler.handleDuplicateResource(duplicateEx);
        ResponseEntity<ApiResponseDTO> unauthorizedResponse = globalExceptionHandler.handleUnauthorized(unauthorizedEx);
        ResponseEntity<ApiResponseDTO> genericResponse = globalExceptionHandler.handleGenericException(genericEx);

        // 🟢 ASSERT
        assertEquals(HttpStatus.NOT_FOUND, notFoundResponse.getStatusCode());
        assertEquals(HttpStatus.CONFLICT, duplicateResponse.getStatusCode());
        assertEquals(HttpStatus.UNAUTHORIZED, unauthorizedResponse.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, genericResponse.getStatusCode());
    }

    @Test
    void exceptionMessages_ShouldBePreservedInResponses() {
        // 🟢 ARRANGE
        String notFoundMsg = "User with id 123 not found";
        String duplicateMsg = "Product with SKU ABC123 already exists";
        String unauthorizedMsg = "Admin role required for this operation";

        ResourceNotFoundException notFoundEx = new ResourceNotFoundException(notFoundMsg);
        DuplicateResourceException duplicateEx = new DuplicateResourceException(duplicateMsg);
        UnauthorizedException unauthorizedEx = new UnauthorizedException(unauthorizedMsg);

        // 🟢 ACT
        ApiResponseDTO notFoundBody = globalExceptionHandler.handleResourceNotFound(notFoundEx).getBody();
        ApiResponseDTO duplicateBody = globalExceptionHandler.handleDuplicateResource(duplicateEx).getBody();
        ApiResponseDTO unauthorizedBody = globalExceptionHandler.handleUnauthorized(unauthorizedEx).getBody();

        // 🟢 ASSERT
        assertNotNull(notFoundBody);
        assertNotNull(duplicateBody);
        assertNotNull(unauthorizedBody);

        assertEquals(notFoundMsg, notFoundBody.getMessage());
        assertEquals(duplicateMsg, duplicateBody.getMessage());
        assertEquals(unauthorizedMsg, unauthorizedBody.getMessage());

        assertFalse(notFoundBody.isSuccess());
        assertFalse(duplicateBody.isSuccess());
        assertFalse(unauthorizedBody.isSuccess());
    }

    @Test
    void exceptionHierarchy_ShouldBeCorrect() {
        // 🟢 ARRANGE & ACT
        ResourceNotFoundException resourceEx = new ResourceNotFoundException("test");
        DuplicateResourceException duplicateEx = new DuplicateResourceException("test");
        UnauthorizedException unauthorizedEx = new UnauthorizedException("test");

        // 🟢 ASSERT
        assertTrue(resourceEx instanceof RuntimeException);
        assertTrue(duplicateEx instanceof RuntimeException);
        assertTrue(unauthorizedEx instanceof RuntimeException);
    }
}