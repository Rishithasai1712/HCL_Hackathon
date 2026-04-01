package com.retail.ordering.service;

import com.retail.ordering.dto.AppDto;
import com.retail.ordering.entity.*;
import com.retail.ordering.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final PackagingRepository packagingRepository;
    private final InventoryRepository inventoryRepository;

    public Page<AppDto.ProductResponse> getProducts(Long brandId, Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findActiveWithFilters(brandId, categoryId, pageable)
                .map(this::toResponse);
    }

    public AppDto.ProductResponse getById(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return toResponse(p);
    }

    public AppDto.ProductResponse createProduct(AppDto.ProductRequest req) {
        Brand brand = brandRepository.findById(req.getBrandId()).orElseThrow();
        Category cat = categoryRepository.findById(req.getCategoryId()).orElseThrow();
        Packaging pkg = packagingRepository.findById(req.getPackagingId()).orElseThrow();

        Product product = Product.builder()
                .name(req.getName()).brand(brand).category(cat)
                .packaging(pkg).price(req.getPrice()).isActive(req.isActive())
                .build();
        product = productRepository.save(product);

        // Create inventory entry
        Inventory inv = Inventory.builder().product(product).quantity(0).reservedQty(0).build();
        inventoryRepository.save(inv);

        return toResponse(product);
    }

    public AppDto.ProductResponse updateProduct(Long id, AppDto.ProductRequest req) {
        Product product = productRepository.findById(id).orElseThrow();
        product.setName(req.getName());
        product.setBrand(brandRepository.findById(req.getBrandId()).orElseThrow());
        product.setCategory(categoryRepository.findById(req.getCategoryId()).orElseThrow());
        product.setPackaging(packagingRepository.findById(req.getPackagingId()).orElseThrow());
        product.setPrice(req.getPrice());
        product.setActive(req.isActive());
        return toResponse(productRepository.save(product));
    }

    public List<Brand> getBrands() { return brandRepository.findByIsActiveTrue(); }
    public List<Category> getCategories() { return categoryRepository.findAll(); }
    public List<Packaging> getPackaging() { return packagingRepository.findAll(); }

    private AppDto.ProductResponse toResponse(Product p) {
        AppDto.ProductResponse r = new AppDto.ProductResponse();
        r.setId(p.getId());
        r.setName(p.getName());
        r.setBrandName(p.getBrand().getName());
        r.setCategoryName(p.getCategory().getName());
        r.setPackagingType(p.getPackaging().getType());
        r.setPrice(p.getPrice());
        r.setActive(p.isActive());
        int stock = p.getInventory() != null ? p.getInventory().getQuantity() : 0;
        r.setStockQuantity(stock);
        return r;
    }
}
