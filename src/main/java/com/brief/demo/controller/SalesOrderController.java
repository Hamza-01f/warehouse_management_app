package com.brief.demo.controller;

import com.brief.demo.aop.RequiresWarehouseManager;
import com.brief.demo.dto.request.SalesOrderRequestDTO;
import com.brief.demo.dto.response.ApiResponseDTO;
import com.brief.demo.dto.response.SalesOrderResponseDTO;
import com.brief.demo.service.SalesOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales-orders")
@RequiredArgsConstructor
@Tag(name = "Sales Orders", description = "Sales order management and processing")
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    @Operation(summary = "Create sales order", description = "Creates a new sales order for a client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sales order created successfully"),
            @ApiResponse(responseCode = "404", description = "Client, warehouse, or product not found")
    })
    @PostMapping
    public ResponseEntity<SalesOrderResponseDTO> createSalesOrder(
            @RequestBody SalesOrderRequestDTO request,
            @Parameter(description = "Client ID", required = true, example = "1")
            @RequestParam Long clientId) {
        SalesOrderResponseDTO response = salesOrderService.createSalesOrder(request, clientId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all sales orders", description = "Retrieves all sales orders. Requires WAREHOUSE_MANAGER role")
    @ApiResponse(responseCode = "200", description = "Sales orders retrieved successfully")
    @GetMapping
    @RequiresWarehouseManager
    public ResponseEntity<List<SalesOrderResponseDTO>> getAllSalesOrders() {
        List<SalesOrderResponseDTO> orders = salesOrderService.getAllSalesOrders();
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Get sales orders by client", description = "Retrieves sales orders for a specific client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sales orders retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<SalesOrderResponseDTO>> getSalesOrdersByClient(
            @Parameter(description = "Client ID", required = true, example = "1")
            @PathVariable Long clientId) {
        List<SalesOrderResponseDTO> orders = salesOrderService.getSalesOrdersByClient(clientId);
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Get sales order by ID", description = "Retrieves a specific sales order by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sales order found"),
            @ApiResponse(responseCode = "404", description = "Sales order not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SalesOrderResponseDTO> getSalesOrderById(
            @Parameter(description = "Sales Order ID", required = true, example = "1")
            @PathVariable Long id) {
        SalesOrderResponseDTO order = salesOrderService.getSalesOrderById(id);
        return ResponseEntity.ok(order);
    }

    @Operation(summary = "Reserve stock for order", description = "Reserves stock for a sales order. Requires WAREHOUSE_MANAGER role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock reserved successfully"),
            @ApiResponse(responseCode = "404", description = "Sales order not found"),
            @ApiResponse(responseCode = "400", description = "Cannot reserve stock for order in current status")
    })
    @PostMapping("/{id}/reserve")
    @RequiresWarehouseManager
    public ResponseEntity<ApiResponseDTO> reserveStock(
            @Parameter(description = "Sales Order ID", required = true, example = "1")
            @PathVariable Long id) {
        salesOrderService.reserveStock(id);
        return ResponseEntity.ok(new ApiResponseDTO("Stock reserved successfully", true));
    }

    @Operation(summary = "Cancel sales order", description = "Cancels a sales order and releases reserved stock")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order canceled successfully"),
            @ApiResponse(responseCode = "404", description = "Sales order not found"),
            @ApiResponse(responseCode = "400", description = "Order is already canceled")
    })
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponseDTO> cancelSalesOrder(
            @Parameter(description = "Sales Order ID", required = true, example = "1")
            @PathVariable Long id) {
        salesOrderService.cancelSalesOrder(id);
        return ResponseEntity.ok(new ApiResponseDTO("Order canceled successfully", true));
    }
}