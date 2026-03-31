package com.backend.retailapp.service;

import com.backend.retailapp.dto.OrderRequest;
import com.backend.retailapp.dto.OrderResponse;
import com.backend.retailapp.dto.OrderStatusUpdateRequest;

import java.util.List;

public interface OrderService {
    OrderResponse placeOrder(OrderRequest orderRequest);
    List<OrderResponse> getOrdersByUserId(Long userId);
    OrderResponse updateOrderStatus(Long orderId, OrderStatusUpdateRequest statusUpdateRequest);
}
