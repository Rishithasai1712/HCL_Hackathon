package com.retail.products.controller;

import com.retail.products.model.Brand;
import com.retail.products.model.Category;
import com.retail.products.model.Packaging;
import com.retail.products.repository.BrandRepository;
import com.retail.products.repository.CategoryRepository;
import com.retail.products.repository.PackagingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Public read-only endpoints for catalog metadata.
 *
 *  GET /api/brands       → all active brands
 *  GET /api/categories   → root categories with children (hierarchy)
 *  GET /api/packaging    → all packaging types
 */
@RestController
@RequiredArgsConstructor
public class CatalogController {

    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final PackagingRepository packagingRepository;

    @GetMapping("/api/brands")
    public ResponseEntity<List<Brand>> getBrands() {
        return ResponseEntity.ok(brandRepository.findByIsActiveTrue());
    }

    @GetMapping("/api/categories")
    public ResponseEntity<List<Category>> getCategories() {
        return ResponseEntity.ok(categoryRepository.findRootCategoriesWithChildren());
    }

    @GetMapping("/api/packaging")
    public ResponseEntity<List<Packaging>> getPackaging() {
        return ResponseEntity.ok(packagingRepository.findAll());
    }
}
