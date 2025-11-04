package com.brief.demo.repository;

import com.brief.demo.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    List<Warehouse> findByIsActiveTrue();
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name , Long id);
}
