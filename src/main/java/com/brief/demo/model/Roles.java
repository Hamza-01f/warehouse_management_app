package com.brief.demo.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Roles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name" , nullable = false , unique = true)
    private String roleName;

    @OneToMany(mappedBy = "userRole" , orphanRemoval = true)
    private List<User> users = new ArrayList<>();

}
