// BackorderRepository.java
package com.brief.demo.repository;

import com.brief.demo.model.BackOrder;
import com.brief.demo.enums.BackOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BackorderRepository extends JpaRepository<BackOrder, Long> {
    List<BackOrder> findByStatus(BackOrderStatus status);
    List<BackOrder> findByProductId(Long productId);
    List<BackOrder> findBySalesOrderLineId(Long salesOrderLineId);

    @Query("SELECT bo FROM BackOrder bo WHERE bo.salesOrderLine.salesOrder.id = :orderId")
    List<BackOrder> findBySalesOrderId(@Param("orderId") Long orderId);

    Optional<BackOrder> findBySalesOrderLineIdAndProductId(Long salesOrderLineId, Long productId);
    List<BackOrder> findByStatusAndProductId(BackOrderStatus status, Long productId);

    @Query("SELECT bo FROM BackOrder bo WHERE bo.status = 'PENDING' AND bo.autoPurchaseOrder = true")
    List<BackOrder> findPendingAutoPurchaseOrders();

    List<BackOrder> findByWarehouseIdAndStatus(Long warehouseId, BackOrderStatus status);
}