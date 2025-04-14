package com.bandi.customeraccountmanage.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.bandi.customeraccountmanage.repository")
@EntityScan(basePackages = "com.bandi.customeraccountmanage.entity")
public class DatabaseConfig {
    // The configuration is handled by Spring Boot auto-configuration
    // Additional custom configuration can be added here if needed
}