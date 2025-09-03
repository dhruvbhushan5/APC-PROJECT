package com.hotel.payment.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Database configuration for Payment Service
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.hotel.payment.repository")
@EnableJpaAuditing
@EnableTransactionManagement
public class DatabaseConfig {
    // Database configuration beans if needed
}
