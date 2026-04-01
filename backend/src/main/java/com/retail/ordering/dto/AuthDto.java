package com.retail.ordering.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class AuthDto {

    @Data
    public static class RegisterRequest {
        @NotBlank private String name;
        @Email @NotBlank private String email;
        @NotBlank @Size(min = 6) private String password;
    }

    @Data
    public static class LoginRequest {
        @Email @NotBlank private String email;
        @NotBlank private String password;
    }

    @Data
    public static class AuthResponse {
        private String accessToken;
        private String refreshToken;
        private String role;
        private String name;
        private String email;

        public AuthResponse(String accessToken, String refreshToken,
                            String role, String name, String email) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.role = role;
            this.name = name;
            this.email = email;
        }
    }

    @Data
    public static class RefreshRequest {
        @NotBlank private String refreshToken;
    }
}
