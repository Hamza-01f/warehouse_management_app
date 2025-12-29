package com.brief.demo.controller;

import com.brief.demo.dto.request.LoginRequestDTO;
import com.brief.demo.dto.request.RefreshTokenRequestDTO;
import com.brief.demo.dto.request.RegisterRequestDTO;
import com.brief.demo.dto.response.ApiResponseDTO;
import com.brief.demo.dto.response.ApiTokenResponse;
import com.brief.demo.dto.response.AuthResponseDTO;
import com.brief.demo.dto.response.TokenResponseDTO;
import com.brief.demo.model.RefreshToken;
import com.brief.demo.service.RefreshTokenService;
import com.brief.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private  final RefreshTokenService refreshTokenService;



    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshTokenRequestDTO request) {

        log.info("Logout request received");

        refreshTokenService.revokeToken(request.getRefreshToken());

        log.info("User logged out successfully");

        return ResponseEntity.ok().build();
    }

}
