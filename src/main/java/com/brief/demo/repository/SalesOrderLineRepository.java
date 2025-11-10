package com.brief.demo.repository;

import com.brief.demo.model.SalesOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesOrderLineRepository extends JpaRepository<SalesOrderLine, Long> {
    List<SalesOrderLine> findBySalesOrderId(Long salesOrderId);
    List<SalesOrderLine> findByProductIdAndQuantityReservedGreaterThan(Long productId, Integer quantity);
    List<SalesOrderLine> findByProductSku(String sku);
}