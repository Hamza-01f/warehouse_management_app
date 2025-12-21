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
        log.info(" user service is being called to register him : ");
        AuthResponseDTO response = userService.register(request);
        log.info(" user was registered with success : {}" , request.getFirstName());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiTokenResponse<TokenResponseDTO>> refreshToken(@RequestBody RefreshTokenRequestDTO request) {

        String refreshToken = request.getRefreshToken();
        RefreshToken token = refreshTokenService.getByToken(refreshToken);
        log.info(" refresh token was taken from database with success : ");

        if (token == null) {
            log.error(" the token was not found : {}", token);
            throw new RuntimeException("Refresh token not found");
        }

        if (token.isRevoked() || token.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.error(" the token either revoked or expired : {}",token);
            throw new RuntimeException("Refresh token expired or revoked");
        }

        refreshTokenService.revokeToken(request.getRefreshToken());
        log.info(" the token {} was revoked with seccess : ",request.getRefreshToken());
        UserDetails userDetails = userService.loadUserByUsername(token.getUser().getEmail());
        log.info(" loading the user with success : ");
        String newAccessToken = jwtUtil.generateAccessToken(userDetails);
        log.info(" generating new access token with success : ");
        RefreshToken newRefreshToken = jwtUtil.generateRefreshToken(userDetails);
        log.info(" generating new refresh token with success : ");

        TokenResponseDTO tokenResponse = TokenResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(15 * 60)
                .build();

        log.info(" building token response dto with success : ");

        ApiTokenResponse<TokenResponseDTO> response = ApiTokenResponse.success(tokenResponse , "your token is this : ");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiTokenResponse<TokenResponseDTO>> login(@RequestBody LoginRequestDTO request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail() , request.getPassword())
        );

        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        MDC.put("env" , "dev");
        MDC.put("userRole", userDetails.getAuthorities().toString());
        log.info(" the user was logged in with success with this email : {}",request.getEmail());

        String token = jwtUtil.generateAccessToken(userDetails);
        log.info(" jwt token was generated with success : ");
        RefreshToken refreshToken = jwtUtil.generateRefreshToken(userDetails);
        log.info(" refresh token was generated with success : ");
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
        refreshTokenService.revokeToken(request.getRefreshToken());
        log.info(" the refresh token {} is being revoked : ",request.getRefreshToken());
        log.info(" you logged out with success : ");
        return ResponseEntity.ok().build();
    }

}
