package com.brief.demo.model;


import jakarta.persistence.*;
import lombok.*;

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
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Roles> roles = new HashSet<>();
}
