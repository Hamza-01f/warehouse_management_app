package com.brief.demo.repository;

import com.brief.demo.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findBySupplierId(Long supplierId);
    List<PurchaseOrder> findByStatus(com.brief.demo.enums.POStatus status);
}