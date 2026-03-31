package com.backend.retailapp.model;

import com.backend.retailapp.model.enums.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.userdetails.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many orders → one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @DecimalMin(value = "0.00", message = "Total amount cannot be negative")
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.PLACED;

    @CreationTimestamp
    @Column(name = "ordered_at", updatable = false)
    private LocalDateTime orderedAt;

    // One order → many order items
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();
}