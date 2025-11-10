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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public PurchaseOrderResponseDTO createPurchaseOrder(PurchaseOrderRequestDTO request) {
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));

        PurchaseOrder purchaseOrder = PurchaseOrder.builder()
                .supplier(supplier)
                .status(POStatus.CREATED)
                .build();

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
                    .build();

            poLineRepository.save(poLine);
        }

        return purchaseOrderMapper.toResponse(savedPO);
    }

    @Transactional
    public PurchaseOrderResponseDTO updatePurchaseOrderStatus(Long id, PurchaseOrderUpdateDTO request) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found"));

        purchaseOrder.setStatus(request.getStatus());
        PurchaseOrder updatedPO = purchaseOrderRepository.save(purchaseOrder);

        // If received, update inventory
        if (request.getStatus() == POStatus.RECEIVED) {
            receivePurchaseOrder(id);
        }

        return purchaseOrderMapper.toResponse(updatedPO);
    }

    @Transactional
    public void receivePurchaseOrder(Long poId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found"));

        List<POLine> poLines = poLineRepository.findByPurchaseOrderId(poId);

        for (POLine line : poLines) {
            List<com.brief.demo.model.Warehouse> warehouses = warehouseRepository.findByIsActiveTrue();
            if (!warehouses.isEmpty()) {
                com.brief.demo.model.Warehouse warehouse = warehouses.get(0);

                // Find or create inventory
                Inventory inventory = inventoryRepository.findByWarehouseIdAndProductId(warehouse.getId(), line.getProduct().getId())
                        .orElse(Inventory.builder()
                                .warehouse(warehouse)
                                .product(line.getProduct())
                                .quantityOnHand(0)
                                .quantityReserved(0)
                                .build());

                inventory.setQuantityOnHand(inventory.getQuantityOnHand() + line.getQuantity());
                inventoryRepository.save(inventory);

                // Create inbound movement
//                inventoryService.createMovement(
//                        com.brief.demo.dto.request.InventoryMovementRequestDTO.builder()
//                                .inventoryId(inventory.getId())
//                                .type(com.brief.demo.enums.MovementType.IN)
//                                .quantity(line.getQuantity())
//                                .reason("Purchase order reception: " + poId)
//                                .build()
//                );
            }
        }

        purchaseOrder.setStatus(POStatus.RECEIVED);
        purchaseOrderRepository.save(purchaseOrder);
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
}