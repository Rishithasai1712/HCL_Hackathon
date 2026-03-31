package com.backend.retailapp.service;

import com.backend.retailapp.dto.*;
import com.backend.retailapp.exceptions.OrderNotFoundException;
import com.backend.retailapp.model.Order;
import com.backend.retailapp.model.OrderItem;
import com.backend.retailapp.model.Product;
import com.backend.retailapp.model.User;
import com.backend.retailapp.model.enums.OrderStatus;
import com.backend.retailapp.repository.OrderRepository;
import com.backend.retailapp.repository.ProductRepository;
import com.backend.retailapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryService inventoryService;

    @Override
    @Transactional
    public OrderResponse placeOrder(OrderRequest orderRequest) {
        // Find user
        User user = userRepository.findById(orderRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + orderRequest.getUserId()));

        // Create order
        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PLACED)
                .totalAmount(BigDecimal.ZERO)
                .orderItems(new ArrayList<>())
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : orderRequest.getItems()) {
            // Find product
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + itemRequest.getProductId()));

            // Reserve stock (Requirement A: Validate stock and reserve)
            inventoryService.reserveStock(product, itemRequest.getQuantity());

            // Create order item
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(product.getPrice())
                    .build();

            order.getOrderItems().add(orderItem);
            totalAmount = totalAmount.add(orderItem.getUnitPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        return mapToOrderResponse(savedOrder);
    }

    @Override
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatusUpdateRequest statusUpdateRequest) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        OrderStatus oldStatus = order.getStatus();
        OrderStatus newStatus = statusUpdateRequest.getStatus();

        if (oldStatus == newStatus) {
            return mapToOrderResponse(order);
        }

        // Logic for confirming or cancelling stock
        if (newStatus == OrderStatus.CONFIRMED && oldStatus == OrderStatus.PLACED) {
            // Requirement B: Reduce actual stock and reserved stock
            for (OrderItem item : order.getOrderItems()) {
                inventoryService.confirmStock(item.getProduct(), item.getQuantity());
            }
        } else if (newStatus == OrderStatus.CANCELLED && oldStatus == OrderStatus.PLACED) {
            // Requirement C: Release reserved stock
            for (OrderItem item : order.getOrderItems()) {
                inventoryService.releaseStock(item.getProduct(), item.getQuantity());
            }
        }

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        return mapToOrderResponse(updatedOrder);
    }

    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUser().getId());
        response.setStatus(order.getStatus());
        response.setOrderDate(order.getOrderedAt());

        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(item -> {
                    OrderItemResponse itemResponse = new OrderItemResponse();
                    itemResponse.setProductId(item.getProduct().getId());
                    itemResponse.setProductName(item.getProduct().getName());
                    itemResponse.setQuantity(item.getQuantity());
                    itemResponse.setPrice(item.getUnitPrice());
                    return itemResponse;
                })
                .collect(Collectors.toList());

        response.setItems(itemResponses);
        return response;
    }
}
