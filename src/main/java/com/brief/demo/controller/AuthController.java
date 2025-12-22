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
import com.brief.demo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private  final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;


    @PostMapping("/register")
    @PreAuthorize("hasAuthority('USER_CREATE')")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequestDTO request) {

        log.info("Register request received");

        AuthResponseDTO response = userService.register(request);

        log.info("User registered successfully");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiTokenResponse<TokenResponseDTO>> refreshToken(@RequestBody RefreshTokenRequestDTO request) {

        log.info("Refresh token request received");
        RefreshToken token = refreshTokenService.getByToken(request.getRefreshToken());

        if (token == null) {
            log.warn("Refresh token not found");
            throw new RuntimeException("Refresh token not found");
        }

        if (token.isRevoked() || token.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.warn("Refresh token expired or revoked");
            throw new RuntimeException("Refresh token expired or revoked");
        }

        refreshTokenService.revokeToken(request.getRefreshToken());
        log.info("Old refresh token revoked");
        UserDetails userDetails = userService.loadUserByUsername(token.getUser().getEmail());
        String newAccessToken = jwtUtil.generateAccessToken(userDetails);
        RefreshToken newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

        log.info("New tokens generated");

        TokenResponseDTO tokenResponse = TokenResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(15 * 60)
                .build();


        ApiTokenResponse<TokenResponseDTO> response = ApiTokenResponse.success(tokenResponse , "your token is this : ");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiTokenResponse<TokenResponseDTO>> login(@RequestBody LoginRequestDTO request) {

        log.info("Login attempt started");

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail() , request.getPassword())
        );

        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        log.info("User authenticated successfully");

        String token = jwtUtil.generateAccessToken(userDetails);
        RefreshToken refreshToken = jwtUtil.generateRefreshToken(userDetails);

        log.info("Access and refresh tokens generated");

        TokenResponseDTO tokenResponse = TokenResponseDTO
                .builder()
                .accessToken(token)
                .refreshToken(refreshToken.getToken())
                .expiresIn( 15 * 60 )
                .tokenType("Bearer")
                .build();
        log.info(" log in response dto was build with success : ");
        MDC.clear();
        ApiTokenResponse<TokenResponseDTO> response = ApiTokenResponse.success(tokenResponse , " you token is being retrieved with success : ");
       return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshTokenRequestDTO request) {

        log.info("Logout request received");

        refreshTokenService.revokeToken(request.getRefreshToken());

        log.info("User logged out successfully");

        return ResponseEntity.ok().build();
    }

}
