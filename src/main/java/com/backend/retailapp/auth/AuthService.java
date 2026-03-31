package com.backend.retailapp.auth;

import com.backend.retailapp.auth.AuthDtos.*;
import com.backend.retailapp.exception.ConflictException;
import com.backend.retailapp.security.JwtUtil;
import com.backend.retailapp.user.Role;
import com.backend.retailapp.user.User;
import com.backend.retailapp.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AuthService handles:
 *   - New customer registration (always ROLE_CUSTOMER)
 *   - Login with email + password, returning a signed JWT
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    /**
     * Registers a new customer account.
     * New users via the public API always receive ROLE_CUSTOMER.
     * Admins must be seeded directly in the database or via a secured admin endpoint.
     */
    @Transactional
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email is already registered: " + request.email());
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.ROLE_CUSTOMER)        // All self-registered users are customers
                .build();

        userRepository.save(user);
        log.info("Registered new customer: {}", user.getEmail());

        String token = jwtUtil.generateToken(user);
        return AuthResponse.of(token, user);
    }

    /**
     * Authenticates an existing user and returns a JWT token.
     * Delegates credential validation to Spring's AuthenticationManager
     * which uses our DaoAuthenticationProvider + BCrypt internally.
     */
    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        String token = jwtUtil.generateToken(user);
        log.info("User logged in: {} [{}]", user.getEmail(), user.getRole());

        return AuthDtos.AuthResponse.of(token, user);
    }
}
