package com.retail.ordering.dto;

import com.retail.ordering.entity.Order;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class AppDto {

    // ---- Product ----
    @Data
    public static class ProductResponse {
        private Long id;
        private String name;
        private String brandName;
        private String categoryName;
        private String packagingType;
        private BigDecimal price;
        private boolean active;
        private int stockQuantity;
    }

    @Data
    public static class ProductRequest {
        @NotBlank private String name;
        @NotNull private Long brandId;
        @NotNull private Long categoryId;
        @NotNull private Long packagingId;
        @NotNull @Positive private BigDecimal price;
        private boolean active = true;
    }

    // ---- Cart ----
    @Data
    public static class CartItemRequest {
        @NotNull private Long productId;
        @NotNull @Min(1) private Integer quantity;
    }

    @Data
    public static class CartResponse {
        private Long cartId;
        private List<CartItemResponse> items;
        private BigDecimal grandTotal;
    }

    @Data
    public static class CartItemResponse {
        private Long itemId;
        private Long productId;
        private String productName;
        private BigDecimal unitPrice;
        private int quantity;
        private BigDecimal subtotal;
    }

    // ---- Order ----
    @Data
    public static class OrderResponse {
        private Long id;
        private BigDecimal totalAmount;
        private String status;
        private LocalDateTime orderedAt;
        private List<OrderItemResponse> items;
    }

    @Data
    public static class OrderItemResponse {
        private Long productId;
        private String productName;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
    }

    // ---- Inventory ----
    @Data
    public static class InventoryUpdateRequest {
        @NotNull @Min(0) private Integer quantity;
    }

    @Data
    public static class InventoryResponse {
        private Long productId;
        private String productName;
        private int quantity;
        private int reservedQty;
        private LocalDateTime updatedAt;
    }

    // ---- Order Status Update ----
    @Data
    public static class OrderStatusRequest {
        @NotNull private Order.Status status;
    }
}
