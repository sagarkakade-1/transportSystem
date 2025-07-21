package com.shivshakti.stms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Database Configuration Class
 * Configures JPA repositories and transaction management
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.shivshakti.stms.repository")
@EnableTransactionManagement
public class DatabaseConfig {
    
    // Additional database configuration can be added here if needed
    // For example: custom DataSource configuration, connection pool settings, etc.
}

