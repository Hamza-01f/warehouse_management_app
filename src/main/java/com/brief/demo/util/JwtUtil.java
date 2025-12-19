package com.brief.demo.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "my-super-secret-key-my-super-secret-key";

    private static final long EXPIRATION_TIME = 1000 * 60 * 60;

    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserDetails userDetails){
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles" , userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey() , SignatureAlgorithm.HS256)
                .compact();
    }
}
