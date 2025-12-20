package com.brief.demo.repository;

import com.brief.demo.model.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {
    List<InventoryMovement> findByInventoryIdOrderByOccurredAtDesc(Long inventoryId);
    List<InventoryMovement> findByInventoryProductIdOrderByOccurredAtDesc(Long productId);

    @Query("SELECT im FROM InventoryMovement im WHERE im.inventory.warehouse.id = :warehouseId ORDER BY im.occurredAt DESC")
    List<InventoryMovement> findByWarehouseId(@Param("warehouseId") Long warehouseId);
}