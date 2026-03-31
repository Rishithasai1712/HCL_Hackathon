package com.backend.retailapp.auth;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Objects for the Auth API.
 * Using Java records for immutability and brevity.
 */
public final class AuthDtos {

    private AuthDtos() {}

    // ─── Requests ─────────────────────────────────────────────────────────────

    public record RegisterRequest(
            @NotBlank(message = "Name is required")
            String name,

            @Email(message = "Enter a valid email")
            @NotBlank(message = "Email is required")
            String email,

            @NotBlank(message = "Password is required")
            @Size(min = 8, message = "Password must be at least 8 characters")
            String password
    ) {}

    public record LoginRequest(
            @Email
            @NotBlank(message = "Email is required")
            String email,

            @NotBlank(message = "Password is required")
            String password
    ) {}

    // ─── Responses ────────────────────────────────────────────────────────────

    public record AuthResponse(
            String token,
            String type,       // always "Bearer"
            Long userId,
            String name,
            String email,
            String role
    ) {
        public static AuthResponse of(String token, com.backend.retailapp.user.User user) {
            return new AuthResponse(
                    token, "Bearer",
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole().name()
            );
        }
    }

    public record MessageResponse(String message) {}

    public record ErrorResponse(String error, String message) {}
}
