package com.brief.demo.exception;

import com.brief.demo.dto.response.ApiResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDTO> handleResourceNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(
                new ApiResponseDTO(ex.getMessage(), false),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponseDTO> handleDuplicateResource(DuplicateResourceException ex) {
        return new ResponseEntity<>(
                new ApiResponseDTO(ex.getMessage(), false),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponseDTO> handleUnauthorized(UnauthorizedException ex) {
        return new ResponseEntity<>(
                new ApiResponseDTO(ex.getMessage(), false),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO> handleGenericException(Exception ex) {
        return new ResponseEntity<>(
                new ApiResponseDTO("An error occurred: " + ex.getMessage(), false),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
