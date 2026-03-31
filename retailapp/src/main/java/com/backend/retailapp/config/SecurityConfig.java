package com.backend.retailapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for testing
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/cart/**").permitAll() // Allow cart APIs
                .requestMatchers("/h2-console/**").permitAll() // Allow H2 console
                .anyRequest().permitAll() // Allow everything else for now (optional)
            )
            .headers(headers -> headers.frameOptions(frame -> frame.disable())); // Required for H2 console
        
        return http.build();
    }
}
