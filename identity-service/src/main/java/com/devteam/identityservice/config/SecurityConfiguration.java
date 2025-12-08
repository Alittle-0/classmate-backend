package com.devteam.identityservice.config;

import com.devteam.identityservice.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    private static final String[] PUBLIC_URLS = {
            "/api/v1/identity/auth/**",
            "/api/v1/identity/health",
            "/api/v1/identity/me/**",

            "/api/v1/identity/v2/api-docs",
            "/api/v1/identity/v3/api-docs",
            "/api/v1/identity/v3/api-docs/**",
            "/api/v1/identity/swagger-resources",
            "/api/v1/identity/swagger-resources/**",
            "/api/v1/identity/configuration/ui",
            "/api/v1/identity/configuration/security",
            "/api/v1/identity/swagger-ui/**",
            "/api/v1/identity/webjars/**",
            "/api/v1/identity/swagger-ui.html"
    };

    private static final String HEALTH_CHECK_URL = "/api/v1/identity/health";

    private final JwtFilter jwtFilter;

    public SecurityConfiguration(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

//    "/api/v1/identity/health" || "/api/v1/identity/auth/**"
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_URLS).permitAll()
//                        .requestMatchers(HEALTH_CHECK_URL).permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(this.jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


}
