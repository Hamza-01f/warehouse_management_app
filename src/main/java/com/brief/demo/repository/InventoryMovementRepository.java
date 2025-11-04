package com.brief.demo.repository;

import com.brief.demo.model.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {
    List<InventoryMovement> findByInventoryIdOrderByOccurredAtDesc(Long inventoryId);
    List<InventoryMovement> findByInventoryProductIdOrderByOccurredAtDesc(Long productId);
}