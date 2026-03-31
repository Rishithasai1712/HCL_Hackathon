package com.backend.retailapp.dto;

import lombok.Data;

@Data
public class CartItemRequest {
    private Long userId;
    private Long productId;
    private Integer quantity;
    private Long cartItemId; // For updates
}