package com.shivshakti.stms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class for Shivshakti Transport Management System (STMS)
 * 
 * This is a comprehensive transport management system that handles:
 * - Driver management with license tracking
 * - Client management with credit analysis
 * - Truck management with document tracking
 * - Trip management with complete lifecycle
 * - Builty/Invoice management with payment tracking
 * - Expense management with approval workflows
 * - Income management with payment reconciliation
 * - Maintenance management with scheduling
 * 
 * @author STMS Development Team
 * @version 1.0.0
 * @since 2024-12-21
 */
@SpringBootApplication
@EnableJpaRepositories
@EnableTransactionManagement
public class StmsApplication {

    public static void main(String[] args) {
        System.out.println("===============================================");
        System.out.println("🚛 SHIVSHAKTI TRANSPORT MANAGEMENT SYSTEM 🚛");
        System.out.println("===============================================");
        System.out.println("Version: 1.0.0");
        System.out.println("Starting application...");
        System.out.println("===============================================");
        
        SpringApplication.run(StmsApplication.class, args);
        
        System.out.println("===============================================");
        System.out.println("✅ STMS Application Started Successfully!");
        System.out.println("🌐 Access the application at: http://localhost:8080/stms");
        System.out.println("📊 Health Check: http://localhost:8080/stms/actuator/health");
        System.out.println("📖 API Documentation: http://localhost:8080/stms/swagger-ui.html");
        System.out.println("===============================================");
    }
}

