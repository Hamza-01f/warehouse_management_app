package com.brief.demo.repository;

import com.brief.demo.model.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {
    List<SalesOrder> findByClientId(Long clientId);
    List<SalesOrder> findByStatus(String status);
}