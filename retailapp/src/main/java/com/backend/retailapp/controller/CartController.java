package com.backend.retailapp.controller;

import com.backend.retailapp.dto.CartItemRequest;
import com.backend.retailapp.dto.CartResponseDTO;
import com.backend.retailapp.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // POST /api/cart/add
    @PostMapping("/add")
    public ResponseEntity<CartResponseDTO> addToCart(@RequestBody CartItemRequest request) {
        CartResponseDTO cart = cartService.addToCart(request.getUserId(), request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(cart);
    }

    // PUT /api/cart/update
    @PutMapping("/update")
    public ResponseEntity<CartResponseDTO> updateCartItem(@RequestBody CartItemRequest request) {
        CartResponseDTO cart = cartService.updateCartItem(request.getCartItemId(), request.getQuantity());
        return ResponseEntity.ok(cart);
    }

    // DELETE /api/cart/remove/{id}
    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<String> removeCartItem(@PathVariable Long cartItemId) {
        cartService.removeCartItem(cartItemId);
        return ResponseEntity.ok("Cart item removed successfully");
    }

    // GET /api/cart?userId={userId}
    @GetMapping
    public ResponseEntity<CartResponseDTO> getCart(@RequestParam Long userId) {
        CartResponseDTO cart = cartService.getCartByUser(userId);
        return ResponseEntity.ok(cart);
    }
}