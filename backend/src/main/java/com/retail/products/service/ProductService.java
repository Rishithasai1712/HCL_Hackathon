package com.retail.products.service;

import com.retail.products.dto.ProductDto;
import com.retail.products.exception.ResourceNotFoundException;
import com.retail.products.model.*;
import com.retail.products.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final PackagingRepository packagingRepository;
    private final InventoryRepository inventoryRepository;

    // ─── Public catalog (active products) ───────────────────────────────────

    @Transactional(readOnly = true)
    public Page<ProductDto.Response> getProducts(ProductDto.FilterParams params) {
        Pageable pageable = buildPageable(params);
        return productRepository
                .findWithFilters(params.getBrandId(), params.getCategoryId(), params.getKeyword(), pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ProductDto.Response getProductById(Long id) {
        Product product = productRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        if (!product.isActive()) throw new ResourceNotFoundException("Product", id);
        return toResponse(product);
    }

    // ─── Admin operations ────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<ProductDto.Response> getAllProductsAdmin(ProductDto.FilterParams params) {
        Pageable pageable = buildPageable(params);
        return productRepository
                .findAllWithFilters(params.getBrandId(), params.getCategoryId(), params.getKeyword(), pageable)
                .map(this::toResponse);
    }

    @Transactional
    public ProductDto.Response createProduct(ProductDto.CreateRequest req) {
        Brand brand = brandRepository.findById(req.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand", req.getBrandId()));
        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", req.getCategoryId()));
        Packaging packaging = packagingRepository.findById(req.getPackagingId())
                .orElseThrow(() -> new ResourceNotFoundException("Packaging", req.getPackagingId()));

        Product product = Product.builder()
                .name(req.getName())
                .brand(brand)
                .category(category)
                .packaging(packaging)
                .price(req.getPrice())
                .imageUrl(req.getImageUrl())
                .description(req.getDescription())
                .active(req.isActive())
                .build();

        product = productRepository.save(product);

        // Create inventory record
        Inventory inventory = Inventory.builder()
                .product(product)
                .quantity(req.getInitialStock())
                .reservedQty(0)
                .build();
        inventoryRepository.save(inventory);

        return toResponse(product);
    }

    @Transactional
    public ProductDto.Response updateProduct(Long id, ProductDto.UpdateRequest req) {
        Product product = productRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        if (req.getName() != null) product.setName(req.getName());
        if (req.getPrice() != null) product.setPrice(req.getPrice());
        if (req.getImageUrl() != null) product.setImageUrl(req.getImageUrl());
        if (req.getDescription() != null) product.setDescription(req.getDescription());
        if (req.getActive() != null) product.setActive(req.getActive());

        if (req.getBrandId() != null) {
            product.setBrand(brandRepository.findById(req.getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand", req.getBrandId())));
        }
        if (req.getCategoryId() != null) {
            product.setCategory(categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", req.getCategoryId())));
        }
        if (req.getPackagingId() != null) {
            product.setPackaging(packagingRepository.findById(req.getPackagingId())
                    .orElseThrow(() -> new ResourceNotFoundException("Packaging", req.getPackagingId())));
        }

        return toResponse(productRepository.save(product));
    }

    // ─── Mapper ──────────────────────────────────────────────────────────────

    private ProductDto.Response toResponse(Product p) {
        int stock = inventoryRepository.findByProductId(p.getId())
                .map(Inventory::getAvailableQty)
                .orElse(0);

        return ProductDto.Response.builder()
                .id(p.getId())
                .name(p.getName())
                .brandId(p.getBrand().getId())
                .brandName(p.getBrand().getName())
                .categoryId(p.getCategory().getId())
                .categoryName(p.getCategory().getName())
                .packagingId(p.getPackaging().getId())
                .packagingType(p.getPackaging().getType())
                .price(p.getPrice())
                .active(p.isActive())
                .imageUrl(p.getImageUrl())
                .description(p.getDescription())
                .availableStock(stock)
                .build();
    }

    private Pageable buildPageable(ProductDto.FilterParams params) {
        Sort sort = params.getDirection().equalsIgnoreCase("desc")
                ? Sort.by(params.getSortBy()).descending()
                : Sort.by(params.getSortBy()).ascending();
        return PageRequest.of(params.getPage(), params.getSize(), sort);
    }
}
