package com.brief.demo.repository;

import com.brief.demo.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JwtTokenRepository extends JpaRepository<RefreshToken , Long> {
    RefreshToken findByToken(String refreshToken);
}
