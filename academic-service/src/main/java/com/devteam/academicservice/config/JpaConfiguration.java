package com.devteam.academicservice.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
//        (auditorAwareRef = "auditorAware")
@EnableFeignClients
public class JpaConfiguration {
}
