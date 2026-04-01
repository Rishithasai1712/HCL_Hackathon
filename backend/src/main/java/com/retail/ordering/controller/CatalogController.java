package com.retail.ordering.controller;

import com.retail.ordering.dto.AppDto;
import com.retail.ordering.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CatalogController {

    private final ProductService productService;

    @GetMapping("/api/brands")
    public ResponseEntity<?> getBrands() {
        return ResponseEntity.ok(productService.getBrands());
    }

    @GetMapping("/api/categories")
    public ResponseEntity<?> getCategories() {
        return ResponseEntity.ok(productService.getCategories());
    }

    @GetMapping("/api/packaging")
    public ResponseEntity<?> getPackaging() {
        return ResponseEntity.ok(productService.getPackaging());
    }

    @GetMapping("/api/products")
    public ResponseEntity<Page<AppDto.ProductResponse>> getProducts(
            @RequestParam(required = false) Long brand,
            @RequestParam(required = false) Long category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(productService.getProducts(brand, category, page, size));
    }

    @GetMapping("/api/products/{id}")
    public ResponseEntity<AppDto.ProductResponse> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }
}
