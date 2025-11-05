package com.brief.demo.service;

import com.brief.demo.dto.request.SalesOrderRequestDTO;
import com.brief.demo.dto.response.SalesOrderResponseDTO;
import com.brief.demo.enums.OrderStatus;
import com.brief.demo.exception.ResourceNotFoundException;
import com.brief.demo.mappers.SalesOrderMapper;
import com.brief.demo.model.*;
import com.brief.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderLineRepository salesOrderLineRepository;
    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryService inventoryService;
    private final SalesOrderMapper salesOrderMapper;

    @Transactional
    public SalesOrderResponseDTO createSalesOrder(SalesOrderRequestDTO request, Long clientId) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));

        // Create sales order
        SalesOrder salesOrder = SalesOrder.builder()
                .client(client)
                .warehouse(warehouse)
                .status(OrderStatus.CREATED)
                .country(request.getCountry())
                .city(request.getCity())
                .street(request.getStreet())
                .zipCode(request.getZipCode())
                .build();

        SalesOrder savedOrder = salesOrderRepository.save(salesOrder);

        // Create order lines
        for (var lineRequest : request.getOrderLines()) {
            Product product = productRepository.findById(lineRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + lineRequest.getProductId()));

            SalesOrderLine orderLine = SalesOrderLine.builder()
                    .salesOrder(savedOrder)
                    .product(product)
                    .quantity(lineRequest.getQuantity())
                    .quantityReserved(0)
                    .quantityOrdered(lineRequest.getQuantity())
                    .price(product.getPrice())
                    .build();

            salesOrderLineRepository.save(orderLine);
        }

        // Reserve stock
        reserveStock(savedOrder.getId());

        return salesOrderMapper.toResponse(savedOrder);
    }

    @Transactional
    public void reserveStock(Long orderId) {
        SalesOrder salesOrder = salesOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sales order not found"));

        if (salesOrder.getStatus() != OrderStatus.CREATED) {
            throw new IllegalStateException("Cannot reserve stock for order in status: " + salesOrder.getStatus());
        }

        List<SalesOrderLine> orderLines = salesOrderLineRepository.findBySalesOrderId(orderId);
        boolean canFulfill = true;

        // Check availability for all lines
        for (SalesOrderLine line : orderLines) {
            if (!inventoryService.checkAvailability(line.getProduct().getId(), line.getQuantity())) {
                canFulfill = false;
                break;
            }
        }

        if (canFulfill) {
            // Reserve stock for each line
            for (SalesOrderLine line : orderLines) {
                reserveStockForLine(line, salesOrder.getWarehouse().getId());
                line.setQuantityReserved(line.getQuantity());
            }

            salesOrder.setStatus(OrderStatus.RESERVED);
            salesOrderRepository.save(salesOrder);
        } else {
            // Handle backorder scenario
            for (SalesOrderLine line : orderLines) {
                Integer available = inventoryRepository.getTotalAvailableQuantityByProductId(line.getProduct().getId());
                if (available != null) {
                    line.setQuantityReserved(Math.min(line.getQuantity(), available));
                    line.setQuantityOrdered(line.getQuantity() - line.getQuantityReserved());
                } else {
                    line.setQuantityOrdered(line.getQuantity());
                }
            }
            // Order remains in CREATED status with partial reservation
        }

        salesOrderLineRepository.saveAll(orderLines);
    }

    private void reserveStockForLine(SalesOrderLine line, Long warehouseId) {
        Integer quantityToReserve = line.getQuantity();
        List<Inventory> availableInventories = inventoryRepository
                .findAvailableInventoryForProduct(line.getProduct().getId(), quantityToReserve);

        for (Inventory inventory : availableInventories) {
            if (quantityToReserve <= 0) break;

            Integer available = inventory.getAvailable_quantity();
            Integer toReserve = Math.min(available, quantityToReserve);

            inventory.setQuantityReserved(inventory.getQuantityReserved() + toReserve);
            quantityToReserve -= toReserve;

            inventoryRepository.save(inventory);
        }
    }

    public SalesOrderResponseDTO getSalesOrderById(Long id) {
        SalesOrder salesOrder = salesOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sales order not found"));
        return salesOrderMapper.toResponse(salesOrder);
    }

    public List<SalesOrderResponseDTO> getSalesOrdersByClient(Long clientId) {
        return salesOrderRepository.findByClientId(clientId).stream()
                .map(salesOrderMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<SalesOrderResponseDTO> getAllSalesOrders() {
        return salesOrderRepository.findAll().stream()
                .map(salesOrderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public SalesOrderResponseDTO cancelSalesOrder(Long id) {
        SalesOrder salesOrder = salesOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sales order not found"));

        if (salesOrder.getStatus() == OrderStatus.CANCELED) {
            throw new IllegalStateException("Order is already canceled");
        }

        // Release reserved stock
        if (salesOrder.getStatus() == OrderStatus.RESERVED) {
            releaseReservedStock(id);
        }

        salesOrder.setStatus(OrderStatus.CANCELED);
        SalesOrder updatedOrder = salesOrderRepository.save(salesOrder);

        return salesOrderMapper.toResponse(updatedOrder);
    }

    private void releaseReservedStock(Long orderId) {
        List<SalesOrderLine> orderLines = salesOrderLineRepository.findBySalesOrderId(orderId);

        for (SalesOrderLine line : orderLines) {
            if (line.getQuantityReserved() > 0) {
                List<Inventory> inventories = inventoryRepository.findByProductId(line.getProduct().getId());
                for (Inventory inventory : inventories) {
                    if (inventory.getQuantityReserved() > 0) {
                        int toRelease = Math.min(inventory.getQuantityReserved(), line.getQuantityReserved());
                        inventory.setQuantityReserved(inventory.getQuantityReserved() - toRelease);
                        inventoryRepository.save(inventory);
                    }
                }
            }
        }
    }
}