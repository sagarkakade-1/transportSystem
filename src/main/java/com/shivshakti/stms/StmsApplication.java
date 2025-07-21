package com.shivshakti.stms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main Application Class for Shivshakti Transport Management System
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableTransactionManagement
public class StmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(StmsApplication.class, args);
        System.out.println("=================================================");
        System.out.println("🚛 Shivshakti Transport Management System Started");
        System.out.println("🌐 Access URL: http://localhost:8080");
        System.out.println("=================================================");
    }
}

