package com.brief.demo.repository;

import com.brief.demo.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolesRepository extends JpaRepository<Roles , Long> {
    Roles findByRoleName(String role);
}
