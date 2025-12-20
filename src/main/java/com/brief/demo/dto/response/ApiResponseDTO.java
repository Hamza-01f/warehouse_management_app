package com.brief.demo.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data

public class ApiResponseDTO {

    private String message;
    private boolean success;


    public ApiResponseDTO(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

}
