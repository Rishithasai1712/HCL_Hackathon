package com.retail.ordering.service;

import com.retail.ordering.dto.AppDto;
import com.retail.ordering.entity.*;
import com.retail.ordering.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public AppDto.CartResponse getCart(String email) {
        Cart cart = getOrCreateCart(email);
        return toResponse(cart);
    }

    @Transactional
    public AppDto.CartResponse addItem(String email, AppDto.CartItemRequest req) {
        Cart cart = getOrCreateCart(email);
        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .ifPresentOrElse(
                    item -> item.setQuantity(item.getQuantity() + req.getQuantity()),
                    () -> {
                        CartItem item = CartItem.builder()
                                .cart(cart).product(product).quantity(req.getQuantity()).build();
                        cart.getItems().add(item);
                    }
                );
        cartRepository.save(cart);
        return toResponse(cartRepository.findByUserId(cart.getUser().getId()).orElseThrow());
    }

    @Transactional
    public AppDto.CartResponse updateItem(String email, Long itemId, AppDto.CartItemRequest req) {
        Cart cart = getOrCreateCart(email);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        if (!item.getCart().getId().equals(cart.getId()))
            throw new RuntimeException("Item not in your cart");
        item.setQuantity(req.getQuantity());
        cartItemRepository.save(item);
        return toResponse(cartRepository.findById(cart.getId()).orElseThrow());
    }

    @Transactional
    public void removeItem(String email, Long itemId) {
        Cart cart = getOrCreateCart(email);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        if (!item.getCart().getId().equals(cart.getId()))
            throw new RuntimeException("Item not in your cart");
        cartItemRepository.delete(item);
    }

    public Cart getOrCreateCart(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));
    }

    private AppDto.CartResponse toResponse(Cart cart) {
        AppDto.CartResponse res = new AppDto.CartResponse();
        res.setCartId(cart.getId());
        List<AppDto.CartItemResponse> items = cart.getItems().stream().map(ci -> {
            AppDto.CartItemResponse ir = new AppDto.CartItemResponse();
            ir.setItemId(ci.getId());
            ir.setProductId(ci.getProduct().getId());
            ir.setProductName(ci.getProduct().getName());
            ir.setUnitPrice(ci.getProduct().getPrice());
            ir.setQuantity(ci.getQuantity());
            ir.setSubtotal(ci.getProduct().getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())));
            return ir;
        }).collect(Collectors.toList());
        res.setItems(items);
        res.setGrandTotal(items.stream().map(AppDto.CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return res;
    }
}
