package com.brief.demo.repository;

import com.brief.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product , Long> {
    Optional<Product> findBySku(String sku);
    List<Product> findByIsActiveTrue();
    boolean existsBySku(String sku);
    boolean existsBySkuAndIdNot(String sku , Long id);
}
