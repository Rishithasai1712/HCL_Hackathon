package com.retail.products.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

// ─── Request DTO ────────────────────────────────────────────────────────────

public class ProductDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CreateRequest {
        @NotBlank(message = "Product name is required")
        private String name;

        @NotNull(message = "Brand is required")
        private Long brandId;

        @NotNull(message = "Category is required")
        private Long categoryId;

        @NotNull(message = "Packaging is required")
        private Long packagingId;

        @NotNull
        @DecimalMin(value = "0.01", message = "Price must be positive")
        private BigDecimal price;

        private String imageUrl;
        private String description;
        private boolean active = true;

        // Initial stock when creating a product
        private int initialStock = 0;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class UpdateRequest {
        private String name;
        private Long brandId;
        private Long categoryId;
        private Long packagingId;
        private BigDecimal price;
        private String imageUrl;
        private String description;
        private Boolean active;
    }

    // ─── Response DTO ────────────────────────────────────────────────────────

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long id;
        private String name;
        private String brandName;
        private Long brandId;
        private String categoryName;
        private Long categoryId;
        private String packagingType;
        private Long packagingId;
        private BigDecimal price;
        private boolean active;
        private String imageUrl;
        private String description;
        private int availableStock;
    }

    // ─── Page/Filter params ──────────────────────────────────────────────────

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class FilterParams {
        private Long brandId;
        private Long categoryId;
        private String keyword;
        private int page = 0;
        private int size = 10;
        private String sortBy = "id";
        private String direction = "asc";
    }
}
