package com.hotel.room.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Database configuration for Room Service
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.hotel.room.repository")
@EnableJpaAuditing
@EnableTransactionManagement
public class DatabaseConfig {
    // Database configuration beans if needed
}
