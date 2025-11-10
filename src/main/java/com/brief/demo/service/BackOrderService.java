package com.brief.demo.service;

import com.brief.demo.dto.response.BackOrderResponseDTO;
import com.brief.demo.enums.BackOrderStatus;
import com.brief.demo.exception.ResourceNotFoundException;
import com.brief.demo.mappers.BackOrderMapper;
import com.brief.demo.model.BackOrder;
import com.brief.demo.repository.BackorderRepository;
import com.brief.demo.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BackOrderService {

    private final BackorderRepository backorderRepository;
    private final BackOrderMapper backOrderMapper;
    private final PurchaseOrderService purchaseOrderService;
    private final SupplierRepository supplierRepository;

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
    public BackOrderResponseDTO fulfillBackOrder(Long backOrderId) {
        BackOrder backOrder = backorderRepository.findById(backOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Back order not found"));

        backOrder.setStatus(BackOrderStatus.FULFILLED);
        backOrder.setFulfilledAt(java.time.LocalDateTime.now());

        BackOrder updatedBackOrder = backorderRepository.save(backOrder);

        // Create purchase order for the backordered items
        createPurchaseOrderForBackOrder(backOrder);

        return backOrderMapper.toResponse(updatedBackOrder);
    }

    @Transactional
    public BackOrderResponseDTO cancelBackOrder(Long backOrderId) {
        BackOrder backOrder = backorderRepository.findById(backOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Back order not found"));

        backOrder.setStatus(BackOrderStatus.CANCELLED);
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
    public void processPendingBackOrders() {
        List<BackOrder> pendingBackOrders = backorderRepository.findByStatus(BackOrderStatus.PENDING);

        for (BackOrder backOrder : pendingBackOrders) {
            // Auto-fulfill or create purchase orders based on business rules
            fulfillBackOrder(backOrder.getId());
        }
    }
}