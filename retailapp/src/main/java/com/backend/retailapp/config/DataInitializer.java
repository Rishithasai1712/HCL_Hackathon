package com.backend.retailapp.config;

import com.backend.retailapp.model.*;
import com.backend.retailapp.model.enums.PackagingType;
import com.backend.retailapp.model.enums.Role;
import com.backend.retailapp.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(
            UserRepository userRepository,
            BrandRepository brandRepository,
            CategoryRepository categoryRepository,
            PackagingRepository packagingRepository,
            ProductRepository productRepository,
            InventoryRepository inventoryRepository) {
        return args -> {
            // Seed Users
            if (userRepository.count() == 0) {
                User customer = User.builder()
                        .name("John Doe")
                        .email("john@example.com")
                        .passwordHash("password123")
                        .role(Role.CUSTOMER)
                        .build();
                userRepository.save(customer);

                User admin = User.builder()
                        .name("Admin User")
                        .email("admin@example.com")
                        .passwordHash("admin123")
                        .role(Role.ADMIN)
                        .build();
                userRepository.save(admin);
            }

            // Seed Brands
            if (brandRepository.count() == 0) {
                brandRepository.save(Brand.builder().name("Brand A").isActive(true).build());
                brandRepository.save(Brand.builder().name("Apple").isActive(true).build());
                brandRepository.save(Brand.builder().name("Samsung").isActive(true).build());
                brandRepository.save(Brand.builder().name("Sony").isActive(true).build());
                brandRepository.save(Brand.builder().name("Philips").isActive(true).build());
            }

            // Seed Categories
            if (categoryRepository.count() == 0) {
                Category electronics = Category.builder().name("Electronics").build();
                categoryRepository.save(electronics);

                categoryRepository.save(Category.builder().name("Laptops").parent(electronics).build());
                categoryRepository.save(Category.builder().name("Smartphones").parent(electronics).build());
                categoryRepository.save(Category.builder().name("Home Appliances").build());
            }

            // Seed Packaging
            if (packagingRepository.count() == 0) {
                packagingRepository.save(Packaging.builder().type(PackagingType.BOX).description("Standard box").build());
                packagingRepository.save(Packaging.builder().type(PackagingType.PACKET).description("Protective packet").build());
            }

            // Seed Products and Inventory
            if (productRepository.count() < 7) {
                Brand brandA = brandRepository.findAll().stream().filter(b -> b.getName().equals("Brand A")).findFirst().orElse(null);
                Brand apple = brandRepository.findAll().stream().filter(b -> b.getName().equals("Apple")).findFirst().orElse(null);
                Brand samsung = brandRepository.findAll().stream().filter(b -> b.getName().equals("Samsung")).findFirst().orElse(null);
                Brand sony = brandRepository.findAll().stream().filter(b -> b.getName().equals("Sony")).findFirst().orElse(null);
                Brand philips = brandRepository.findAll().stream().filter(b -> b.getName().equals("Philips")).findFirst().orElse(null);

                Category electronics = categoryRepository.findAll().stream().filter(c -> c.getName().equals("Electronics")).findFirst().orElse(null);
                Category laptops = categoryRepository.findAll().stream().filter(c -> c.getName().equals("Laptops")).findFirst().orElse(null);
                Category smartphones = categoryRepository.findAll().stream().filter(c -> c.getName().equals("Smartphones")).findFirst().orElse(null);
                Category appliances = categoryRepository.findAll().stream().filter(c -> c.getName().equals("Home Appliances")).findFirst().orElse(null);

                Packaging box = packagingRepository.findAll().stream().filter(p -> p.getType().equals(PackagingType.BOX)).findFirst().orElse(null);
                Packaging packet = packagingRepository.findAll().stream().filter(p -> p.getType().equals(PackagingType.PACKET)).findFirst().orElse(null);

                if (brandA != null && laptops != null && box != null) {
                    saveProductIfNotExists("Laptop X", BigDecimal.valueOf(1200.00), brandA, laptops, box, 10, productRepository, inventoryRepository);
                }
                if (apple != null && smartphones != null && box != null) {
                    saveProductIfNotExists("iPhone 15", BigDecimal.valueOf(999.99), apple, smartphones, box, 25, productRepository, inventoryRepository);
                }
                if (samsung != null && smartphones != null && box != null) {
                    saveProductIfNotExists("Galaxy S24", BigDecimal.valueOf(899.50), samsung, smartphones, box, 30, productRepository, inventoryRepository);
                }
                if (sony != null && electronics != null && packet != null) {
                    saveProductIfNotExists("Sony Headphones", BigDecimal.valueOf(299.00), sony, electronics, packet, 50, productRepository, inventoryRepository);
                }
                if (philips != null && appliances != null && box != null) {
                    saveProductIfNotExists("Philips Air Fryer", BigDecimal.valueOf(149.00), philips, appliances, box, 15, productRepository, inventoryRepository);
                }
                if (apple != null && electronics != null && box != null) {
                    saveProductIfNotExists("Apple Watch Series 9", BigDecimal.valueOf(399.00), apple, electronics, box, 20, productRepository, inventoryRepository);
                }
                if (samsung != null && electronics != null && box != null) {
                    saveProductIfNotExists("Samsung 27\" Monitor", BigDecimal.valueOf(249.99), samsung, electronics, box, 12, productRepository, inventoryRepository);
                }

                System.out.println("Test data check/initialization complete!");
            }
        };
    }

    private void saveProductIfNotExists(String name, BigDecimal price, Brand brand, Category category, Packaging packaging, int qty, 
                                      ProductRepository productRepository, InventoryRepository inventoryRepository) {
        if (!productRepository.findAll().stream().anyMatch(p -> p.getName().equals(name))) {
            Product product = productRepository.save(Product.builder()
                    .name(name)
                    .price(price)
                    .isActive(true)
                    .brand(brand)
                    .category(category)
                    .packaging(packaging)
                    .build());
            
            inventoryRepository.save(Inventory.builder()
                    .product(product)
                    .quantity(qty)
                    .build());
            
            System.out.println("Created product: " + name + " with ID: " + product.getId());
        }
    }
}
