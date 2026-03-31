package com.backend.retailapp.service;

import com.backend.retailapp.model.Product;

public interface InventoryService {
    void reserveStock(Product product, Integer quantity);
    void confirmStock(Product product, Integer quantity);
    void releaseStock(Product product, Integer quantity);
}
