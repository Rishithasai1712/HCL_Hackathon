package com.retail.products.controller;

import com.retail.products.dto.ProductDto;
import com.retail.products.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Public product catalog — no auth required.
 * Endpoints:
 *   GET /api/products                  → paginated + filtered list
 *   GET /api/products/{id}             → single product detail
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Browse products with optional filters.
     *
     * Query params:
     *   brand      (Long)   – filter by brand id
     *   category   (Long)   – filter by category id
     *   keyword    (String) – search by name
     *   page       (int)    – 0-indexed page number (default 0)
     *   size       (int)    – page size (default 10)
     *   sortBy     (String) – field to sort (default: id)
     *   direction  (String) – asc | desc (default: asc)
     */
    @GetMapping
    public ResponseEntity<Page<ProductDto.Response>> getProducts(
            @RequestParam(required = false) Long brand,
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        ProductDto.FilterParams params = ProductDto.FilterParams.builder()
                .brandId(brand)
                .categoryId(category)
                .keyword(keyword)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .direction(direction)
                .build();

        return ResponseEntity.ok(productService.getProducts(params));
    }

    /**
     * Get a single product's full detail.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto.Response> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
}
