package com.brief.demo.repository;

import com.brief.demo.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    List<Supplier> findByIsActiveTrue();
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name,Long id);
}