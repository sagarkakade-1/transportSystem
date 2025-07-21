# 🚛 Shivshakti Transport Management System (STMS)

## 📋 Project Overview
A comprehensive **full-stack Transport Management Web Application** designed for Shivshakti Transport to manage every operational and financial detail of a transport business with structured data storage, deep relationships, and automated billing.

## 🔧 Technology Stack
- **Backend**: Java 8, Spring Boot, Hibernate/JPA
- **Database**: PostgreSQL with strong relational design
- **Frontend**: HTML, CSS, JavaScript, Thymeleaf
- **Reporting**: Apache POI (Excel/Word export)
- **Build Tool**: Maven

## 🎯 Main Modules

### 1️⃣ Truck & Asset Management
- Complete truck information with documentation tracking
- Insurance, RC, Permit, Fitness, PUC management
- Maintenance scheduling and tracking

### 2️⃣ Trip Management
- Trip planning and execution tracking
- Driver and truck assignment
- Real-time status updates
- Distance and fuel consumption tracking

### 3️⃣ Financial Management
- **Expense Tracking**: Fuel, Toll, Tyre, Repairs, Driver Allowance
- **Income Management**: Client payments, Trip charges, Builty-based receivables
- **Automated Calculations**: Balance computation and profit/loss analysis

### 4️⃣ Billing & Invoice Management
- Builty/Invoice generation and management
- Client-wise billing and payment tracking
- Outstanding balance management

### 5️⃣ Employee & Driver Management
- Driver information and license tracking
- Salary and advance payment management
- License expiry alerts

### 6️⃣ Maintenance & Service Log
- Comprehensive maintenance records
- Warranty tracking for parts and services
- Service scheduling and reminders

### 7️⃣ Reporting System
- One-click monthly reports
- Excel/Word export functionality
- Customizable filters (Date, Truck, Driver, Client)
- Profit/Loss analysis and balance sheets

## 🗂️ Database Design

### Core Entities
- **Trucks** → **Trips** → **Expenses** → **Income** → **Builty** → **Clients**
- **Drivers** → **Trips** → **Allowance/Advance**
- **Trucks** → **Maintenance** → **Expense Types** (Tyre, Fuel, Repair)

### Key Features
- Strong foreign key relationships
- Referential integrity constraints
- Optimized indexing for performance
- Comprehensive views for reporting

## 🚀 Getting Started

### Prerequisites
- Java 8 or higher
- PostgreSQL 12 or higher
- Maven 3.6 or higher

### Database Setup
1. Create PostgreSQL database:
   ```sql
   CREATE DATABASE stms_db;
   CREATE USER stms_user WITH PASSWORD 'stms_password';
   GRANT ALL PRIVILEGES ON DATABASE stms_db TO stms_user;
   ```

2. Run the schema script:
   ```bash
   psql -U stms_user -d stms_db -f database/schema.sql
   ```

### Application Setup
1. Clone the repository
2. Configure database connection in `application.properties`
3. Build and run the application:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. Access the application at: `http://localhost:8080/stms`

## 📊 Features Implemented

### ✅ Phase 1 - Foundation (Current)
- [x] Project structure setup
- [x] Database schema design with strong relationships
- [x] Spring Boot configuration
- [x] Entity relationship documentation

### 🔄 Phase 2 - Core Development (In Progress)
- [ ] JPA Entity classes with validations
- [ ] Repository layer with custom queries
- [ ] Service layer with business logic
- [ ] REST API controllers

### 📋 Phase 3 - Frontend & Integration (Planned)
- [ ] Thymeleaf templates and forms
- [ ] CSS styling and JavaScript functionality
- [ ] User interface for all modules

### 📈 Phase 4 - Reporting & Analytics (Planned)
- [ ] Apache POI integration for reports
- [ ] Excel/Word export functionality
- [ ] Dashboard with key metrics

## 🏗️ Architecture

### Layered Architecture
```
Controller Layer (REST APIs + Web Controllers)
    ↓
Service Layer (Business Logic + Validations)
    ↓
Repository Layer (Data Access + Custom Queries)
    ↓
Entity Layer (JPA Entities + Relationships)
    ↓
Database Layer (PostgreSQL + Views + Indexes)
```

### Design Patterns
- **Repository Pattern**: Data access abstraction
- **Service Layer Pattern**: Business logic encapsulation
- **DTO Pattern**: Data transfer between layers
- **Builder Pattern**: Complex object creation

## 📝 Development Guidelines

### Code Quality Standards
- Java 8 features (Streams, Optional, LocalDate/LocalDateTime)
- Comprehensive validation using Bean Validation
- Proper exception handling and logging
- Unit and integration testing
- Clean code principles

### Database Standards
- Strong foreign key relationships
- Proper indexing for performance
- Normalized data structure
- Audit trails for critical data

## 🤝 Contributing
This is a production-ready system designed for real-world transport business operations. All code follows enterprise-level standards and best practices.

## 📄 License
Proprietary software for Shivshakti Transport

---

**Version**: 1.0.0  
**Last Updated**: July 2024  
**Status**: Active Development

