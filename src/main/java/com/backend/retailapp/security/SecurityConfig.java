package com.backend.retailapp.security;

import com.backend.retailapp.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * SecurityConfig — defines the complete security filter chain.
 *
 * Access rules (from project spec):
 *   PUBLIC       → /api/auth/**      (register, login, refresh)
 *   PUBLIC       → GET /api/products/** and catalog endpoints
 *   CUSTOMER     → /api/cart/**, /api/orders/**
 *   ADMIN        → /api/admin/**
 *   AUTHENTICATED → everything else
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity          // enables @PreAuthorize / @Secured on controller methods
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserRepository userRepository;

    // ─── Security Filter Chain ────────────────────────────────────────────────

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Disable CSRF — not needed for stateless JWT APIs
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Configure CORS (React dev server on :3000)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 3. Stateless session — no HttpSession
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. Route-level authorization rules
                .authorizeHttpRequests(auth -> auth

                        // ── Public endpoints ──────────────────────────────────────────
                        .requestMatchers("/api/auth/**").permitAll()

                        // Catalog — products, brands, categories, packaging are public
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/brands/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/packaging/**").permitAll()

                        // ── Role-restricted endpoints ─────────────────────────────────
                        // Cart and orders: customers only
                        .requestMatchers("/api/cart/**").hasRole("CUSTOMER")
                        .requestMatchers("/api/orders/**").hasRole("CUSTOMER")

                        // Admin panel: admin only
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // ── Anything else must be authenticated ───────────────────────
                        .anyRequest().authenticated()
                )

                // 5. Custom exception handling for 401/403
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(401);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"error\":\"Unauthorized\",\"message\":\"" +
                                            authException.getMessage() + "\"}"
                            );
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(403);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"error\":\"Forbidden\"," +
                                            "\"message\":\"You do not have permission to access this resource\"}"
                            );
                        })
                )

                // 6. Wire our JWT filter before Spring's username/password filter
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ─── Authentication Beans ─────────────────────────────────────────────────

    /**
     * UserDetailsService — loads User from DB by email.
     * Used by DaoAuthenticationProvider and directly by JwtAuthFilter.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return email -> userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email: " + email));
    }

    /**
     * DaoAuthenticationProvider wires our UserDetailsService + BCrypt encoder.
     */


    @Autowired
    private UserDetailsService userDetailsService; // ✅ CORRECT
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailsService); // ✅ correct
        provider.setPasswordEncoder(passwordEncoder());     // ✅ correct

        return provider;
    }

    /**
     * AuthenticationManager — exposed as a Bean so AuthController can inject it.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * BCrypt with default strength 10 rounds.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ─── CORS ────────────────────────────────────────────────────────────────

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Allow React dev server; replace with your production origin(s)
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
