package com.backend.retailapp.dto;

import com.backend.retailapp.model.enums.OrderStatus;

public class OrderStatusUpdateRequest {
    private OrderStatus status;

    // Getters and Setters
    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
