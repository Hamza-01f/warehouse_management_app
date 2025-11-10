package com.brief.demo.controller;

//import com.brief.demo.aop.RequiresWarehouseManager;
import com.brief.demo.aop.RequiresWarehouseManager;
import com.brief.demo.dto.request.SalesOrderRequestDTO;
import com.brief.demo.dto.response.ApiResponseDTO;
import com.brief.demo.dto.response.SalesOrderResponseDTO;
import com.brief.demo.service.SalesOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales-orders")
@RequiredArgsConstructor
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    @PostMapping
    public ResponseEntity<SalesOrderResponseDTO> createSalesOrder(
            @RequestBody SalesOrderRequestDTO request,
            @RequestParam Long clientId) {
        SalesOrderResponseDTO response = salesOrderService.createSalesOrder(request, clientId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @RequiresWarehouseManager
    public ResponseEntity<List<SalesOrderResponseDTO>> getAllSalesOrders() {
        List<SalesOrderResponseDTO> orders = salesOrderService.getAllSalesOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<SalesOrderResponseDTO>> getSalesOrdersByClient(@PathVariable Long clientId) {
        List<SalesOrderResponseDTO> orders = salesOrderService.getSalesOrdersByClient(clientId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalesOrderResponseDTO> getSalesOrderById(@PathVariable Long id) {
        SalesOrderResponseDTO order = salesOrderService.getSalesOrderById(id);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{id}/reserve")
    @RequiresWarehouseManager
    public ResponseEntity<ApiResponseDTO> reserveStock(@PathVariable Long id) {
        salesOrderService.reserveStock(id);
        return ResponseEntity.ok(new ApiResponseDTO("Stock reserved successfully", true));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponseDTO> cancelSalesOrder(@PathVariable Long id) {
        salesOrderService.cancelSalesOrder(id);
        return ResponseEntity.ok(new ApiResponseDTO("Order canceled successfully", true));
    }
}