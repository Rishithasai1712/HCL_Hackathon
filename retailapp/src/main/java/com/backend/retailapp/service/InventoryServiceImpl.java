package com.backend.retailapp.service;

import com.backend.retailapp.exceptions.InsufficientStockException;
import com.backend.retailapp.model.Inventory;
import com.backend.retailapp.model.Product;
import com.backend.retailapp.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Override
    @Transactional
    public void reserveStock(Product product, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProduct(product)
                .orElseThrow(() -> new RuntimeException("Inventory not found for product: " + product.getName()));

        // Requirement: Check Inventory.availableQuantity = quantity - reservedQty
        if (inventory.getAvailableQuantity() < quantity) {
            throw new InsufficientStockException("Insufficient stock for product: " + product.getName() + 
                    ". Available: " + inventory.getAvailableQuantity() + ", Requested: " + quantity);
        }

        // Requirement: If enough stock → reserve items (increase reservedQty)
        inventory.setReservedQty(inventory.getReservedQty() + quantity);
        inventoryRepository.save(inventory);
    }

    @Override
    @Transactional
    public void confirmStock(Product product, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProduct(product)
                .orElseThrow(() -> new RuntimeException("Inventory not found for product: " + product.getName()));

        // Requirement: Reduce actual stock (quantity -= ordered qty)
        inventory.setQuantity(inventory.getQuantity() - quantity);
        
        // Requirement: Reduce reserved stock (reservedQty -= ordered qty)
        inventory.setReservedQty(inventory.getReservedQty() - quantity);
        
        inventoryRepository.save(inventory);
    }

    @Override
    @Transactional
    public void releaseStock(Product product, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProduct(product)
                .orElseThrow(() -> new RuntimeException("Inventory not found for product: " + product.getName()));

        // Requirement: Only release reserved stock (reservedQty -= ordered qty)
        inventory.setReservedQty(inventory.getReservedQty() - quantity);
        
        inventoryRepository.save(inventory);
    }
}
