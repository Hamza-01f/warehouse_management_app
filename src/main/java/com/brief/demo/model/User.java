package com.brief.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "user" , fetch = FetchType.LAZY , orphanRemoval = true)
    private List<RefreshToken> refreshTokens = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_role_id" , nullable = false)
    private Roles userRole;


    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}