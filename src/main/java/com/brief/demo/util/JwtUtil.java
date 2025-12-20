package com.brief.demo.util;

import com.brief.demo.model.RefreshToken;
import com.brief.demo.service.RefreshTokenService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private static final String SECRET_KEY = "my-super-secret-key-my-super-secret-key";
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 15 * 60 * 1000;
    private  final RefreshTokenService refreshTokenService;


    public String generateAccessToken(UserDetails userDetails){
        Map<String , Object> claims = new HashMap<>();
        claims.put("roles" , userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList());

        return Jwts.builder().setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256 , SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    public RefreshToken generateRefreshToken(UserDetails userDetails){

        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();

    return refreshTokenService.saveRefreshToken(refreshToken , userDetails.getUsername());
    }
}
