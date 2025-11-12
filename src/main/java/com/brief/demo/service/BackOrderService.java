package com.brief.demo.service;

import com.brief.demo.dto.response.BackOrderResponseDTO;
import com.brief.demo.enums.BackOrderStatus;
import com.brief.demo.exception.ResourceNotFoundException;
import com.brief.demo.mappers.BackOrderMapper;
import com.brief.demo.model.*;
import com.brief.demo.repository.BackorderRepository;
import com.brief.demo.repository.SalesOrderLineRepository;
import com.brief.demo.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BackOrderService {

    private final BackorderRepository backorderRepository;
    private final BackOrderMapper backOrderMapper;

    private final SupplierRepository supplierRepository;
    private final InventoryService inventoryService;
    private final SalesOrderLineRepository salesOrderLineRepository;

    private final PurchaseOrderService purchaseOrderService;

    @Transactional
    public void createBackOrder(SalesOrderLine salesOrderLine, Warehouse warehouse, Integer quantity) {
        BackOrder backOrder = BackOrder.builder()
                .salesOrderLine(salesOrderLine)
                .product(salesOrderLine.getProduct())
                .warehouse(warehouse)
                .quantity(quantity)
                .status(BackOrderStatus.PENDING)
                .autoPurchaseOrder(true)
                .build();

        backorderRepository.save(backOrder);
    }

    @Transactional
    public BackOrderResponseDTO activateBackOrder(Long backOrderId) {
        BackOrder backOrder = backorderRepository.findById(backOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Back order not found"));

        if (backOrder.getStatus() != BackOrderStatus.PENDING) {
            throw new IllegalStateException("Only pending backorders can be activated");
        }

        if (backOrder.getAutoPurchaseOrder()) {
            createPurchaseOrderForBackOrder(backOrder);
            backOrder.setStatus(BackOrderStatus.AUTO_PURCHASE_CREATED);
        }

        BackOrder updatedBackOrder = backorderRepository.save(backOrder);
        return backOrderMapper.toResponse(updatedBackOrder);
    }

    @Transactional
    public BackOrderResponseDTO fulfillBackOrder(Long backOrderId) {
        BackOrder backOrder = backorderRepository.findById(backOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Back order not found"));

        // Add stock to inventory
        inventoryService.processInboundMovement(
                backOrder.getProduct().getId(),
                backOrder.getWarehouse().getId(),
                backOrder.getQuantity()
        );

        // Update sales order line
        if (backOrder.getSalesOrderLine() != null) {
            SalesOrderLine line = backOrder.getSalesOrderLine();
            line.setBackorderQuantity(line.getBackorderQuantity() - backOrder.getQuantity());
            line.setQuantityReserved(line.getQuantityReserved() + backOrder.getQuantity());
            salesOrderLineRepository.save(line);

            // Try to reserve the newly available stock
            inventoryService.reserveStockForOrder(line, backOrder.getWarehouse());
        }

        backOrder.setStatus(BackOrderStatus.FULFILLED);
        backOrder.setFulfilledAt(java.time.LocalDateTime.now());

        BackOrder updatedBackOrder = backorderRepository.save(backOrder);
        return backOrderMapper.toResponse(updatedBackOrder);
    }
    private void createPurchaseOrderForBackOrder(BackOrder backOrder) {
        var suppliers = supplierRepository.findByIsActiveTrue();
        if (!suppliers.isEmpty()) {
            var supplier = suppliers.get(0);

            var poRequest = new com.brief.demo.dto.request.PurchaseOrderRequestDTO();
            poRequest.setSupplierId(supplier.getId());

            var poLine = new com.brief.demo.dto.request.PurchaseOrderRequestDTO.POLineRequestDTO();
            poLine.setProductId(backOrder.getProduct().getId());
            poLine.setQuantity(backOrder.getQuantity());
            poLine.setPrice(backOrder.getProduct().getPrice());

            poRequest.setOrderLines(List.of(poLine));

            purchaseOrderService.createPurchaseOrder(poRequest);
        }
    }

    @Transactional
    public void cancelBackOrdersForOrder(Long orderId) {
        List<BackOrder> backOrders = backorderRepository.findBySalesOrderId(orderId);
        for (BackOrder backOrder : backOrders) {
            if (backOrder.getStatus() == BackOrderStatus.PENDING) {
                backOrder.setStatus(BackOrderStatus.CANCELLED);
            }
        }
        backorderRepository.saveAll(backOrders);
    }

    @Transactional
    public void processPendingBackOrders() {
        List<BackOrder> pendingBackOrders = backorderRepository.findByStatus(BackOrderStatus.PENDING);
        for (BackOrder backOrder : pendingBackOrders) {
            if (backOrder.getAutoPurchaseOrder()) {
                activateBackOrder(backOrder.getId());
            }
        }
    }

    public List<BackOrderResponseDTO> getAllBackOrders() {
        return backorderRepository.findAll().stream()
                .map(backOrderMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<BackOrderResponseDTO> getBackOrdersByStatus(BackOrderStatus status) {
        return backorderRepository.findByStatus(status).stream()
                .map(backOrderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BackOrderResponseDTO cancelBackOrder(Long backOrderId) {
        BackOrder backOrder = backorderRepository.findById(backOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Back order not found"));

        backOrder.setStatus(BackOrderStatus.CANCELLED);
        BackOrder updatedBackOrder = backorderRepository.save(backOrder);

        return backOrderMapper.toResponse(updatedBackOrder);
    }


}