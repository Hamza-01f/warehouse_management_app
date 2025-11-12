package com.brief.demo.service;

import com.brief.demo.dto.request.PurchaseOrderRequestDTO;
import com.brief.demo.dto.request.PurchaseOrderUpdateDTO;
import com.brief.demo.dto.response.PurchaseOrderResponseDTO;
import com.brief.demo.enums.POStatus;
import com.brief.demo.exception.ResourceNotFoundException;
import com.brief.demo.mappers.PurchaseOrderMapper;
import com.brief.demo.model.*;
import com.brief.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final POLineRepository poLineRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final PurchaseOrderMapper purchaseOrderMapper;
    private final InventoryService inventoryService;
    private final InventoryRepository inventoryRepository;
    private final WarehouseRepository warehouseRepository;
    private final BackorderRepository backOrderRepository;

    @Lazy
    @Autowired
    private BackOrderService backOrderService;


    @Transactional
    public PurchaseOrderResponseDTO createPurchaseOrder(PurchaseOrderRequestDTO request) {
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));

        PurchaseOrder purchaseOrder = PurchaseOrder.builder()
                .supplier(supplier)
                .status(POStatus.CREATED)
                .build();

        // If created from backorder, link them
//        if (request.getBackOrderId() != null) {
//            BackOrder backOrder = backOrderRepository.findById(request.getBackOrderId())
//                    .orElseThrow(() -> new ResourceNotFoundException("Back order not found"));
//            purchaseOrder.setBackOrder(backOrder);
//        }

        PurchaseOrder savedPO = purchaseOrderRepository.save(purchaseOrder);

        // Create PO lines
        for (var lineRequest : request.getOrderLines()) {
            Product product = productRepository.findById(lineRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            POLine poLine = POLine.builder()
                    .purchaseOrder(savedPO)
                    .product(product)
                    .quantity(lineRequest.getQuantity())
                    .price(lineRequest.getPrice())
                    .quantityReceived(0)
                    .build();

            poLineRepository.save(poLine);
        }

        return purchaseOrderMapper.toResponse(savedPO);
    }

    @Transactional
    public PurchaseOrderResponseDTO approvePurchaseOrder(Long id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found"));

        if (purchaseOrder.getStatus() != POStatus.CREATED) {
            throw new IllegalStateException("Only CREATED purchase orders can be approved");
        }

        purchaseOrder.setStatus(POStatus.APPROVED);
        purchaseOrder.setApprovedAt(LocalDate.now());
        PurchaseOrder updatedPO = purchaseOrderRepository.save(purchaseOrder);

        return purchaseOrderMapper.toResponse(updatedPO);
    }

    @Transactional
    public PurchaseOrderResponseDTO updatePurchaseOrderStatus(Long id, PurchaseOrderUpdateDTO request) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found"));

        purchaseOrder.setStatus(request.getStatus());

        if (request.getStatus() == POStatus.APPROVED) {
            purchaseOrder.setApprovedAt(LocalDate.now());
        } else if (request.getStatus() == POStatus.RECEIVED) {
            purchaseOrder.setReceivedAt(LocalDate.now());
            receivePurchaseOrder(id);
        }

        PurchaseOrder updatedPO = purchaseOrderRepository.save(purchaseOrder);
        return purchaseOrderMapper.toResponse(updatedPO);
    }

    @Transactional
    public void receivePurchaseOrder(Long poId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found"));

        if (purchaseOrder.getStatus() != POStatus.APPROVED) {
            throw new IllegalStateException("Only APPROVED purchase orders can be received");
        }

        List<POLine> poLines = poLineRepository.findByPurchaseOrderId(poId);

        Warehouse targetWarehouse = determineTargetWarehouse(purchaseOrder);

        for (POLine line : poLines) {
            Inventory inventory = inventoryRepository.findByWarehouseIdAndProductId(
                            targetWarehouse.getId(), line.getProduct().getId())
                    .orElse(Inventory.builder()
                            .warehouse(targetWarehouse)
                            .product(line.getProduct())
                            .quantityOnHand(0)
                            .quantityReserved(0)
                            .build());

            inventory.setQuantityOnHand(inventory.getQuantityOnHand() + line.getQuantity());
            inventoryRepository.save(inventory);

            inventoryService.processInboundMovement(
                    line.getProduct().getId(),
                    targetWarehouse.getId(),
                    line.getQuantity()
            );

            line.setQuantityReceived(line.getQuantity());
            poLineRepository.save(line);

            if (purchaseOrder.getBackOrder() != null) {
                BackOrder backOrder = purchaseOrder.getBackOrder();
                backOrderService.fulfillBackOrder(backOrder.getId());
            }
        }

        purchaseOrder.setStatus(POStatus.RECEIVED);
        purchaseOrder.setReceivedAt(LocalDate.now());
        purchaseOrderRepository.save(purchaseOrder);
    }

    private Warehouse determineTargetWarehouse(PurchaseOrder purchaseOrder) {
        if (purchaseOrder.getBackOrder() != null) {
            return purchaseOrder.getBackOrder().getWarehouse();
        }

        List<Warehouse> warehouses = warehouseRepository.findByIsActiveTrue();
        if (warehouses.isEmpty()) {
            throw new IllegalStateException("No active warehouses available");
        }
        return warehouses.get(0);
    }

    @Transactional
    public PurchaseOrderResponseDTO receivePartialPurchaseOrder(Long poId, List<Long> lineIds, List<Integer> quantities) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found"));

        if (purchaseOrder.getStatus() != POStatus.APPROVED) {
            throw new IllegalStateException("Only APPROVED purchase orders can be partially received");
        }

        Warehouse targetWarehouse = determineTargetWarehouse(purchaseOrder);

        for (int i = 0; i < lineIds.size(); i++) {
            Long lineId = lineIds.get(i);
            Integer quantity = quantities.get(i);

            POLine poLine = poLineRepository.findById(lineId)
                    .orElseThrow(() -> new ResourceNotFoundException("PO line not found"));

            if (quantity > (poLine.getQuantity() - poLine.getQuantityReceived())) {
                throw new IllegalStateException("Cannot receive more than ordered quantity");
            }

            inventoryService.processInboundMovement(
                    poLine.getProduct().getId(),
                    targetWarehouse.getId(),
                    quantity
            );

            poLine.setQuantityReceived(poLine.getQuantityReceived() + quantity);
            poLineRepository.save(poLine);
        }

        boolean allFullyReceived = poLineRepository.findByPurchaseOrderId(poId)
                .stream()
                .allMatch(line -> line.getQuantityReceived().equals(line.getQuantity()));

        if (allFullyReceived) {
            purchaseOrder.setStatus(POStatus.RECEIVED);
            purchaseOrder.setReceivedAt(LocalDate.now());
        } else {
            purchaseOrder.setStatus(POStatus.PARTIALLY_RECEIVED);
        }

        PurchaseOrder updatedPO = purchaseOrderRepository.save(purchaseOrder);
        return purchaseOrderMapper.toResponse(updatedPO);
    }

    public PurchaseOrderResponseDTO getPurchaseOrderById(Long id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found"));
        return purchaseOrderMapper.toResponse(purchaseOrder);
    }

    public List<PurchaseOrderResponseDTO> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll().stream()
                .map(purchaseOrderMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<PurchaseOrderResponseDTO> getPurchaseOrdersBySupplier(Long supplierId) {
        return purchaseOrderRepository.findBySupplierId(supplierId).stream()
                .map(purchaseOrderMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<PurchaseOrderResponseDTO> getPurchaseOrdersByStatus(POStatus status) {
        return purchaseOrderRepository.findByStatus(status).stream()
                .map(purchaseOrderMapper::toResponse)
                .collect(Collectors.toList());
    }
}