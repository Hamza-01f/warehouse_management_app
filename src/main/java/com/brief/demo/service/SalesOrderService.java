package com.brief.demo.service;

import com.brief.demo.dto.request.SalesOrderRequestDTO;
import com.brief.demo.dto.response.SalesOrderResponseDTO;
import com.brief.demo.enums.MovementType;
import com.brief.demo.enums.OrderStatus;
import com.brief.demo.enums.ShipmentStatus;
import com.brief.demo.exception.ResourceNotFoundException;
import com.brief.demo.mappers.SalesOrderMapper;
import com.brief.demo.model.*;
import com.brief.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final InventoryService inventoryService;
    private final SalesOrderMapper salesOrderMapper;
    private final BackOrderService backOrderService;
    private final ShipmentService shipmentService;
    private final InventoryRepository inventoryRepository;

    @Transactional
    public SalesOrderResponseDTO createSalesOrder(SalesOrderRequestDTO request, Long clientId) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));

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
                    .quantityFulfilled(0)
                    .backorderQuantity(0)
                    .price(product.getPrice())
                    .build();

            salesOrderLineRepository.save(orderLine);
        }

        reserveStock(savedOrder.getId());

        return salesOrderMapper.toResponse(savedOrder);
    }

    @Transactional
    public void reserveStock(Long orderId) {
        SalesOrder salesOrder = salesOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sales order not found"));

        if (!salesOrder.canBeReserved()) {
            throw new IllegalStateException("Cannot reserve stock for order in status: " + salesOrder.getStatus());
        }

        List<SalesOrderLine> orderLines = salesOrderLineRepository.findBySalesOrderId(orderId);
        boolean allLinesReserved = true;

        for (SalesOrderLine line : orderLines) {
            // Calculate
            Integer totalAvailable = getTotalAvailableQuantity(line.getProduct().getId());
            Integer quantityNeeded = line.getQuantity();

            System.out.println("Product: " + line.getProduct().getName() +
                    ", Requested: " + quantityNeeded +
                    ", Available: " + totalAvailable);

            if (totalAvailable >= quantityNeeded) {
                // Enough
                boolean reserved = inventoryService.reserveStockForOrder(line, salesOrder.getWarehouse());
                if (!reserved) {
                    allLinesReserved = false;
                }
            } else {

                Integer availableToReserve = Math.min(totalAvailable, quantityNeeded);
                boolean partiallyReserved = tryPartialReservation(line, salesOrder.getWarehouse(), availableToReserve);

                if (partiallyReserved) {
                    allLinesReserved = false;
                    Integer backorderQty = quantityNeeded - availableToReserve;
                    line.setBackorderQuantity(backorderQty);

                    System.out.println("Creating backorder for " + backorderQty + " units");
                    backOrderService.createBackOrder(line, salesOrder.getWarehouse(), backorderQty);
                } else {
                    allLinesReserved = false;

                    line.setBackorderQuantity(quantityNeeded);
                    backOrderService.createBackOrder(line, salesOrder.getWarehouse(), quantityNeeded);
                }
            }
        }

        if (allLinesReserved) {
            salesOrder.setStatus(OrderStatus.RESERVED);
            salesOrder.setReservedAt(LocalDateTime.now());
            shipmentService.createShipmentForOrder(salesOrder);
            System.out.println("Order fully reserved and shipment created");
        } else if (orderLines.stream().anyMatch(line -> line.getQuantityReserved() > 0)) {
            salesOrder.setStatus(OrderStatus.PARTIALLY_FULFILLED);
            System.out.println("Order partially fulfilled, backorders created");
        } else {
            salesOrder.setStatus(OrderStatus.CREATED);
            System.out.println("No stock available, backorders created");
        }

        salesOrderLineRepository.saveAll(orderLines);
        salesOrderRepository.save(salesOrder);
    }

    private Integer getTotalAvailableQuantity(Long productId) {
        Integer available = inventoryRepository.getTotalAvailableQuantityByProductId(productId);
        return available != null ? available : 0;
    }

    private boolean tryPartialReservation(SalesOrderLine line, Warehouse preferredWarehouse, Integer quantityToReserve) {
        if (quantityToReserve <= 0) return false;

        boolean reserved = inventoryService.reserveStockForOrder(line, preferredWarehouse);
        if (reserved && line.getQuantityReserved().equals(quantityToReserve)) {
            return true;
        }

        // try another warehouse
        List<Inventory> availableInventories = inventoryService.findAvailableInventoryForProduct(
                line.getProduct().getId(), quantityToReserve);

        Integer remainingToReserve = quantityToReserve - line.getQuantityReserved();

        for (Inventory inventory : availableInventories) {
            if (remainingToReserve <= 0) break;

            if (!inventory.getWarehouse().getId().equals(preferredWarehouse.getId())) {
                Integer available = inventory.getAvailable_Quantity();
                if (available > 0) {
                    Integer toReserve = Math.min(available, remainingToReserve);
                    inventory.reserveQuantity(toReserve);
                    inventoryRepository.save(inventory);

                    line.setQuantityReserved(line.getQuantityReserved() + toReserve);
                    remainingToReserve -= toReserve;

                    inventoryService.createMovement(inventory, MovementType.OUT, toReserve);
                }
            }
        }

        return line.getQuantityReserved() > 0;
    }

    @Transactional
    public SalesOrderResponseDTO shipOrder(Long orderId) {
        SalesOrder salesOrder = salesOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sales order not found"));

        if (!salesOrder.canBeShipped()) {
            throw new IllegalStateException("Order cannot be shipped in current status: " + salesOrder.getStatus());
        }

        // Process outbound movements for reserved quantities
        List<SalesOrderLine> orderLines = salesOrderLineRepository.findBySalesOrderId(orderId);
        for (SalesOrderLine line : orderLines) {
            if (line.getQuantityReserved() > 0) {
                inventoryService.processOutboundMovement(
                        line.getProduct().getId(),
                        salesOrder.getWarehouse().getId(),
                        line.getQuantityReserved()
                );
                line.setQuantityFulfilled(line.getQuantityReserved());
                line.setQuantityReserved(0);
            }
        }

        salesOrder.setStatus(OrderStatus.SHIPPED);
        salesOrder.setShippedAt(LocalDateTime.now());
        SalesOrder updatedOrder = salesOrderRepository.save(salesOrder);
        salesOrderLineRepository.saveAll(orderLines);

        shipmentService.updateShipmentStatusByOrder(orderId, ShipmentStatus.IN_TRANSIT);

        return salesOrderMapper.toResponse(updatedOrder);
    }

    @Transactional
    public SalesOrderResponseDTO deliverOrder(Long orderId) {
        SalesOrder salesOrder = salesOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sales order not found"));

        if (!salesOrder.canBeDelivered()) {
            throw new IllegalStateException("Order cannot be delivered in current status: " + salesOrder.getStatus());
        }

        salesOrder.setStatus(OrderStatus.DELIVERED);
        salesOrder.setDeliveredAt(LocalDateTime.now());
        SalesOrder updatedOrder = salesOrderRepository.save(salesOrder);

        shipmentService.updateShipmentStatusByOrder(orderId, com.brief.demo.enums.ShipmentStatus.DELIVERED);

        return salesOrderMapper.toResponse(updatedOrder);
    }

    @Transactional
    public SalesOrderResponseDTO cancelSalesOrder(Long id) {
        SalesOrder salesOrder = salesOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sales order not found"));

        if (salesOrder.getStatus() == OrderStatus.CANCELED) {
            throw new IllegalStateException("Order is already canceled");
        }

        if (salesOrder.getStatus() == OrderStatus.SHIPPED || salesOrder.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel shipped or delivered order");
        }

        if (salesOrder.getStatus() == OrderStatus.RESERVED || salesOrder.getStatus() == OrderStatus.PARTIALLY_FULFILLED) {
            releaseReservedStock(id);
        }

        backOrderService.cancelBackOrdersForOrder(id);

        salesOrder.setStatus(OrderStatus.CANCELED);
        SalesOrder updatedOrder = salesOrderRepository.save(salesOrder);

        return salesOrderMapper.toResponse(updatedOrder);
    }

    private void releaseReservedStock(Long orderId) {
        List<SalesOrderLine> orderLines = salesOrderLineRepository.findBySalesOrderId(orderId);
        for (SalesOrderLine line : orderLines) {
            inventoryService.releaseReservedStock(line);
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
}