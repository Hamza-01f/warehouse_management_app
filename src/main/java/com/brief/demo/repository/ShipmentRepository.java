package com.brief.demo.repository;

import com.brief.demo.model.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    Optional<Shipment> findBySalesOrderId(Long salesOrderId);
    List<Shipment> findByStatus(String status);
}