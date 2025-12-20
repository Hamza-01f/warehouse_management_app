package com.brief.demo.model;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_token")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false , unique = true)
    private String token;

   @ManyToMany(fetch =  FetchType.LAZY)
   @JoinColumn(name = "user_id" , nullable = false)
   private User user;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    public boolean revoked;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
