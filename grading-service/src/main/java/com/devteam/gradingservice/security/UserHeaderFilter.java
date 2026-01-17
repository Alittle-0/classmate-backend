package com.devteam.gradingservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter to extract user information from HTTP headers and set Spring Security context.
 * This filter expects headers to be set by the API Gateway after JWT validation.
 *
 * Expected headers:
 * - X-User-Id: User's unique identifier
 * - X-User-Email: User's email address
 * - X-Roles: Comma-separated list of user roles
 * - X-Firstname: User's first name
 * - X-Lastname: User's last name
 */
@Component
public class UserHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            @NonNull
            HttpServletRequest request,
            @NonNull
            HttpServletResponse response,
            @NonNull
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Extract user information from headers
        String userId = request.getHeader("X-User-Id");
        String email = request.getHeader("X-User-Email");
        String role = request.getHeader("X-Role");
        String firstname = request.getHeader("X-Firstname");
        String lastname = request.getHeader("X-Lastname");

        // Only set authentication if userId is present
        if (userId != null && !userId.isEmpty()) {
            // Create user principal with extracted information
            UserPrincipal principal = new UserPrincipal(
                    userId, email, firstname, lastname, role
            );

            // Create authentication token with authorities
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    principal,
                    null,
                    principal.getAuthorities()
            );

            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }
}
