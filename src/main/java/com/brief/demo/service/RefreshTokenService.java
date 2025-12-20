package com.brief.demo.service;


import com.brief.demo.model.RefreshToken;
import com.brief.demo.model.User;
import com.brief.demo.repository.JwtTokenRepository;
import com.brief.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor

public class RefreshTokenService {

    private final JwtTokenRepository jwtTokenRepository;
    private final UserRepository userRepository;

    public RefreshToken saveRefreshToken(RefreshToken refreshToken, String  username){
        User user = userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("user not found : "));
        refreshToken.setUser(user);
        return jwtTokenRepository.save(refreshToken);
    }

     public RefreshToken getByToken(String token){
        return  jwtTokenRepository.findByToken(token);
     }

     public void revokeToken(String token){
        RefreshToken refreshToken = jwtTokenRepository.findByToken(token);
        refreshToken.setRevoked(true);
        jwtTokenRepository.save(refreshToken);
     }
}
