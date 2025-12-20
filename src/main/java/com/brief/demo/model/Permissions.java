package com.brief.demo.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permissions")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Permissions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "permission_name" , nullable = false)
    private String name;

    @ManyToMany(mappedBy = "permissions")
    private Set<User> users = new HashSet<>();
}
