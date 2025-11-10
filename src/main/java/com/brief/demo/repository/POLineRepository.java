package com.brief.demo.repository;

import com.brief.demo.model.POLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface POLineRepository extends JpaRepository<POLine, Long> {
    List<POLine> findByPurchaseOrderId(Long purchaseOrderId);
    List<POLine> findByProductId(Long productId);
}