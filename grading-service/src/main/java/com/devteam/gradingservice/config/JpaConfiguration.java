package com.devteam.gradingservice.config;

import com.devteam.gradingservice.security.SecurityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaConfiguration {

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            String userId = SecurityUtils.getCurrentUser().getUserId();
            return Optional.ofNullable(userId);
        };
    }
}
