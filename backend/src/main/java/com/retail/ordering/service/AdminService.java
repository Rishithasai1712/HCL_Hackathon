package com.retail.ordering.service;

import com.retail.ordering.dto.AppDto;
import com.retail.ordering.entity.Inventory;
import com.retail.ordering.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final InventoryRepository inventoryRepository;

    public List<AppDto.InventoryResponse> getAllInventory() {
        return inventoryRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public AppDto.InventoryResponse updateInventory(Long productId, int quantity) {
        Inventory inv = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
        inv.setQuantity(quantity);
        inv.setUpdatedAt(LocalDateTime.now());
        return toResponse(inventoryRepository.save(inv));
    }

    private AppDto.InventoryResponse toResponse(Inventory inv) {
        AppDto.InventoryResponse r = new AppDto.InventoryResponse();
        r.setProductId(inv.getProduct().getId());
        r.setProductName(inv.getProduct().getName());
        r.setQuantity(inv.getQuantity());
        r.setReservedQty(inv.getReservedQty());
        r.setUpdatedAt(inv.getUpdatedAt());
        return r;
    }
}
