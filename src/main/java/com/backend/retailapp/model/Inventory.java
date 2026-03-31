package com.backend.retailapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One inventory → one product
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @Min(value = 0, message = "Quantity cannot be negative")
    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 0;

    @Min(value = 0, message = "Reserved quantity cannot be negative")
    @Column(name = "reserved_qty", nullable = false)
    @Builder.Default
    private Integer reservedQty = 0;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Convenience: available stock = quantity - reservedQty
    @Transient
    public int getAvailableQuantity() {
        return quantity - reservedQty;
    }
}