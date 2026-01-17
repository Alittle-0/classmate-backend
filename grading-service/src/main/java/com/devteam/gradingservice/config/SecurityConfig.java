package com.devteam.gradingservice.config;

import com.devteam.gradingservice.security.UserHeaderFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration for Academic Service.
 *
 * This service does NOT validate JWT tokens directly.
 * Instead, it trusts the API Gateway to validate tokens and pass user information via headers.
 *
 * The UserHeaderFilter extracts user info from headers and sets the Spring Security context,
 * enabling role-based authorization using @PreAuthorize annotations.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Enable @PreAuthorize, @PostAuthorize, etc.
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserHeaderFilter userHeaderFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Disable CSRF as this is a stateless API
                .csrf(AbstractHttpConfigurer::disable)

                // Allow all requests - authorization is handled by @PreAuthorize annotations
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )

                // Stateless session - no session will be created or used
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Add our custom filter to extract user info from headers
                .addFilterBefore(this.userHeaderFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }
}
