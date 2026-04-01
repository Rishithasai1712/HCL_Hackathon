package com.retail.ordering.service;

import com.retail.ordering.dto.AuthDto;
import com.retail.ordering.entity.User;
import com.retail.ordering.repository.UserRepository;
import com.retail.ordering.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;

    public AuthDto.AuthResponse register(AuthDto.RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail()))
            throw new IllegalArgumentException("Email already registered");

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .role(User.Role.CUSTOMER)
                .build();
        userRepository.save(user);

        String access = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        String refresh = jwtUtil.generateRefreshToken(user.getEmail());
        return new AuthDto.AuthResponse(access, refresh, user.getRole().name(),
                                        user.getName(), user.getEmail());
    }

    public AuthDto.AuthResponse login(AuthDto.LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

        User user = userRepository.findByEmail(req.getEmail()).orElseThrow();
        String access = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        String refresh = jwtUtil.generateRefreshToken(user.getEmail());
        return new AuthDto.AuthResponse(access, refresh, user.getRole().name(),
                                        user.getName(), user.getEmail());
    }

    public AuthDto.AuthResponse refresh(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken))
            throw new IllegalArgumentException("Invalid refresh token");
        String email = jwtUtil.extractEmail(refreshToken);
        User user = userRepository.findByEmail(email).orElseThrow();
        String newAccess = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        String newRefresh = jwtUtil.generateRefreshToken(user.getEmail());
        return new AuthDto.AuthResponse(newAccess, newRefresh, user.getRole().name(),
                                        user.getName(), user.getEmail());
    }
}
