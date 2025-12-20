package com.brief.demo.dto.request;

import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;


@Builder
@Data
public class RefreshTokenRequestDTO {
    private String refreshToken;
}
