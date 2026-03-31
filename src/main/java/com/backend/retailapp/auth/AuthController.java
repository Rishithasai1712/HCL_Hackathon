package com.backend.retailapp.auth;


import com.backend.retailapp.auth.AuthDtos.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController exposes the two public authentication endpoints:
 *
 *   POST /api/auth/register  — creates a new ROLE_CUSTOMER account
 *   POST /api/auth/login     — validates credentials and returns a JWT
 *
 * Both endpoints are permitted without authentication in SecurityConfig.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new customer.
     *
     * Request body:
     *   { "name": "Alice", "email": "alice@example.com", "password": "secret123" }
     *
     * Response (201):
     *   { "token": "eyJ...", "type": "Bearer", "userId": 1,
     *     "name": "Alice", "email": "alice@example.com", "role": "ROLE_CUSTOMER" }
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Login with existing credentials.
     *
     * Request body:
     *   { "email": "alice@example.com", "password": "secret123" }
     *
     * Response (200):
     *   { "token": "eyJ...", "type": "Bearer", "userId": 1,
     *     "name": "Alice", "email": "alice@example.com", "role": "ROLE_CUSTOMER" }
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
