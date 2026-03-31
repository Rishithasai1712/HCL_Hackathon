package com.backend.retailapp.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.context.ApplicationContext;
import java.io.IOException;

/**
 * JwtAuthFilter — executed ONCE per request, before UsernamePasswordAuthenticationFilter.
 *
 * Flow:
 *   1. Extract "Bearer <token>" from Authorization header
 *   2. Validate the token with JwtUtil
 *   3. Load UserDetails from DB via UserDetailsService
 *   4. Set Authentication in SecurityContext → Spring treats the request as authenticated
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    @Autowired
    private ApplicationContext context;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // Skip if no Bearer token is present
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7); // Strip "Bearer "
        String userEmail = null;

        try {
            userEmail = jwtUtil.extractUsername(jwt);
        } catch (Exception e) {
            log.debug("Could not extract username from JWT: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        // Only authenticate if we have an email and no existing authentication in context
        if (StringUtils.hasText(userEmail)
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetailsService userDetailsService =
                    context.getBean(UserDetailsService.class);

            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(userEmail);

            if (jwtUtil.isTokenValid(jwt, userDetails)) {
                // Build authentication token and attach it to the SecurityContext
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,                       // credentials — null for JWT (stateless)
                                userDetails.getAuthorities()
                        );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.debug("Authenticated user '{}' with role '{}'",
                        userEmail, userDetails.getAuthorities());
            }
        }

        filterChain.doFilter(request, response);
    }
}
