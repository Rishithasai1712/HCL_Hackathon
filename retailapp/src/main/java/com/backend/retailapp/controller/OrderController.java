package com.backend.retailapp.controller;

import com.backend.retailapp.dto.OrderRequest;
import com.backend.retailapp.dto.OrderResponse;
import com.backend.retailapp.dto.OrderStatusUpdateRequest;
import com.backend.retailapp.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest) {
        OrderResponse orderResponse = orderService.placeOrder(orderRequest);
        return ResponseEntity.ok(orderResponse);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUserId(@PathVariable Long userId) {
        List<OrderResponse> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable Long orderId, @RequestBody OrderStatusUpdateRequest statusUpdateRequest) {
        OrderResponse orderResponse = orderService.updateOrderStatus(orderId, statusUpdateRequest);
        return ResponseEntity.ok(orderResponse);
    }
}
