package com.retail.ordering.service;

import com.retail.ordering.dto.AppDto;
import com.retail.ordering.entity.*;
import com.retail.ordering.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final CartRepository cartRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public AppDto.OrderResponse placeOrder(String email) {
        Cart cart = cartService.getOrCreateCart(email);
        if (cart.getItems().isEmpty()) throw new RuntimeException("Cart is empty");

        // Validate and deduct inventory
        for (CartItem ci : cart.getItems()) {
            Inventory inv = inventoryRepository.findByProductId(ci.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("No inventory for " + ci.getProduct().getName()));
            if (inv.getQuantity() < ci.getQuantity())
                throw new RuntimeException("Insufficient stock for " + ci.getProduct().getName());
            inv.setQuantity(inv.getQuantity() - ci.getQuantity());
            inventoryRepository.save(inv);
        }

        BigDecimal total = cart.getItems().stream()
                .map(ci -> ci.getProduct().getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .user(cart.getUser())
                .totalAmount(total)
                .status(Order.Status.PLACED)
                .build();

        List<OrderItem> orderItems = cart.getItems().stream().map(ci ->
                OrderItem.builder()
                        .order(order)
                        .product(ci.getProduct())
                        .quantity(ci.getQuantity())
                        .unitPrice(ci.getProduct().getPrice())
                        .build()
        ).collect(Collectors.toList());
        order.setItems(orderItems);

        Order saved = orderRepository.save(order);

        // Clear cart
        cart.getItems().clear();
        cartRepository.save(cart);

        return toResponse(saved);
    }

    public List<AppDto.OrderResponse> getOrderHistory(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return orderRepository.findByUserIdOrderByOrderedAtDesc(user.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public AppDto.OrderResponse getOrderById(String email, Long orderId) {
        User user = userRepository.findByEmail(email).orElseThrow();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (!order.getUser().getId().equals(user.getId()))
            throw new RuntimeException("Access denied");
        return toResponse(order);
    }

    // Admin
    public List<AppDto.OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public AppDto.OrderResponse updateStatus(Long orderId, Order.Status status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        return toResponse(orderRepository.save(order));
    }

    private AppDto.OrderResponse toResponse(Order o) {
        AppDto.OrderResponse res = new AppDto.OrderResponse();
        res.setId(o.getId());
        res.setTotalAmount(o.getTotalAmount());
        res.setStatus(o.getStatus().name());
        res.setOrderedAt(o.getOrderedAt());
        res.setItems(o.getItems().stream().map(oi -> {
            AppDto.OrderItemResponse ir = new AppDto.OrderItemResponse();
            ir.setProductId(oi.getProduct().getId());
            ir.setProductName(oi.getProduct().getName());
            ir.setQuantity(oi.getQuantity());
            ir.setUnitPrice(oi.getUnitPrice());
            ir.setSubtotal(oi.getUnitPrice().multiply(BigDecimal.valueOf(oi.getQuantity())));
            return ir;
        }).collect(Collectors.toList()));
        return res;
    }
}
