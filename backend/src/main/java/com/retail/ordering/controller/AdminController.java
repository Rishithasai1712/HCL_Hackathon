package com.retail.ordering.controller;

import com.retail.ordering.dto.AppDto;
import com.retail.ordering.entity.Order;
import com.retail.ordering.service.AdminService;
import com.retail.ordering.service.OrderService;
import com.retail.ordering.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final ProductService productService;
    private final AdminService adminService;
    private final OrderService orderService;

    // ---- Products ----
    @PostMapping("/products")
    public ResponseEntity<AppDto.ProductResponse> createProduct(
            @Valid @RequestBody AppDto.ProductRequest req) {
        return ResponseEntity.ok(productService.createProduct(req));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<AppDto.ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody AppDto.ProductRequest req) {
        return ResponseEntity.ok(productService.updateProduct(id, req));
    }

    // ---- Inventory ----
    @GetMapping("/inventory")
    public ResponseEntity<List<AppDto.InventoryResponse>> getInventory() {
        return ResponseEntity.ok(adminService.getAllInventory());
    }

    @PutMapping("/inventory/{productId}")
    public ResponseEntity<AppDto.InventoryResponse> updateInventory(
            @PathVariable Long productId,
            @Valid @RequestBody AppDto.InventoryUpdateRequest req) {
        return ResponseEntity.ok(adminService.updateInventory(productId, req.getQuantity()));
    }

    // ---- Orders ----
    @GetMapping("/orders")
    public ResponseEntity<List<AppDto.OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<AppDto.OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody AppDto.OrderStatusRequest req) {
        return ResponseEntity.ok(orderService.updateStatus(id, req.getStatus()));
    }
}
