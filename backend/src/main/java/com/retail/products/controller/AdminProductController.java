package com.retail.products.controller;

import com.retail.products.dto.ProductDto;
import com.retail.products.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin-only product management.
 * Requires ROLE_ADMIN.
 *
 * Endpoints:
 *   POST /api/admin/products            → create product
 *   PUT  /api/admin/products/{id}       → update product
 *   GET  /api/admin/products            → view all (incl. inactive)
 */
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductDto.Response>> getAllProducts(
            @RequestParam(required = false) Long brand,
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        ProductDto.FilterParams params = ProductDto.FilterParams.builder()
                .brandId(brand).categoryId(category).keyword(keyword)
                .page(page).size(size).sortBy(sortBy).direction(direction)
                .build();
        return ResponseEntity.ok(productService.getAllProductsAdmin(params));
    }

    @PostMapping
    public ResponseEntity<ProductDto.Response> createProduct(
            @Valid @RequestBody ProductDto.CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto.Response> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductDto.UpdateRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }
}
