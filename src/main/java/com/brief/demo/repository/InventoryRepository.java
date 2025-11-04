package com.brief.demo.repository;

import com.brief.demo.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findByWarehouseId(Long warehouseId);
    Optional<Inventory> findByWarehouseIdAndProductId(Long warehouseId, Long productId);
    List<Inventory> findByProductId(Long productId);

    @Query("SELECT i FROM Inventory i WHERE i.product.id = :productId AND i.quantityOnHand - i.quantityReserved >= :quantity")
    List<Inventory> findAvailableInventoryForProduct(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    @Query("SELECT SUM(i.quantityOnHand - i.quantityReserved) FROM Inventory i WHERE i.product.id = :productId")
    Integer getTotalAvailableQuantityByProductId(@Param("productId") Long productId);
}