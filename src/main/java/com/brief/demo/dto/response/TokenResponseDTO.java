package com.brief.demo.dto.response;

import com.brief.demo.model.RefreshToken;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Builder
@Data
public class TokenResponseDTO {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Integer expiresIn;
}
