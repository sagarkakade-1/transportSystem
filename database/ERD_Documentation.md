# 🗂️ Entity Relationship Diagram (ERD) - STMS Database

## 📊 **Database Overview**
The Shivshakti Transport Management System database is designed with **strong relational integrity** and **comprehensive foreign key relationships** to ensure data consistency and enable complex business operations.

---

## 🏗️ **Core Entity Relationships**

### **1. Master Data Entities**
- **Drivers** - Driver information and license details
- **Clients** - Customer information and credit management
- **Shops** - Service providers (fuel, repair, tyre shops)
- **Trucks** - Vehicle information with complete documentation
- **Expense Categories** - Standardized expense classification

### **2. Operational Entities**
- **Trips** - Core business operations linking trucks and drivers
- **Builty** - Invoice/billing documents linked to trips and clients
- **Expenses** - All operational costs with detailed categorization
- **Income** - Revenue tracking from various sources
- **Maintenance** - Vehicle maintenance and service records
- **Tyre Details** - Detailed tyre tracking with warranty information
- **Payments** - Payment tracking and reconciliation

---

## 🔗 **Key Relationships**

### **Primary Relationships**
```
Trucks (1) ←→ (Many) Trips
Drivers (1) ←→ (Many) Trips
Trips (1) ←→ (Many) Builty
Clients (1) ←→ (Many) Builty
Trips (1) ←→ (Many) Expenses
Trucks (1) ←→ (Many) Maintenance
```

### **Secondary Relationships**
```
Expense_Categories (1) ←→ (Many) Expenses
Shops (1) ←→ (Many) Expenses
Shops (1) ←→ (Many) Maintenance
Maintenance (1) ←→ (Many) Tyre_Details
Clients (1) ←→ (Many) Payments
Builty (1) ←→ (Many) Payments
```

---

## 📋 **Entity Details**

### **🚛 Trucks Entity**
- **Primary Key**: id (BIGSERIAL)
- **Unique Constraints**: truck_number
- **Key Relationships**: 
  - One-to-Many with Trips
  - One-to-Many with Expenses
  - One-to-Many with Maintenance
- **Business Logic**: Central asset tracking with complete documentation

### **👨‍💼 Drivers Entity**
- **Primary Key**: id (BIGSERIAL)
- **Unique Constraints**: license_number
- **Key Relationships**: One-to-Many with Trips
- **Business Logic**: Driver management with license tracking and salary management

### **🏢 Clients Entity**
- **Primary Key**: id (BIGSERIAL)
- **Key Relationships**: 
  - One-to-Many with Builty
  - One-to-Many with Income
  - One-to-Many with Payments
- **Business Logic**: Customer management with credit limit and outstanding tracking

### **🛣️ Trips Entity**
- **Primary Key**: id (BIGSERIAL)
- **Unique Constraints**: trip_number
- **Foreign Keys**: 
  - truck_id → Trucks(id)
  - driver_id → Drivers(id)
- **Key Relationships**: 
  - Many-to-One with Trucks
  - Many-to-One with Drivers
  - One-to-Many with Builty
  - One-to-Many with Expenses
- **Business Logic**: Core operational entity linking all business activities

### **📄 Builty Entity**
- **Primary Key**: id (BIGSERIAL)
- **Unique Constraints**: builty_number
- **Foreign Keys**: 
  - trip_id → Trips(id)
  - client_id → Clients(id)
- **Business Logic**: Invoice/billing document with payment tracking

### **💰 Expenses Entity**
- **Primary Key**: id (BIGSERIAL)
- **Foreign Keys**: 
  - trip_id → Trips(id) [Optional]
  - truck_id → Trucks(id)
  - category_id → Expense_Categories(id)
  - shop_id → Shops(id) [Optional]
- **Business Logic**: Comprehensive expense tracking with categorization and approval workflow

### **💵 Income Entity**
- **Primary Key**: id (BIGSERIAL)
- **Foreign Keys**: 
  - builty_id → Builty(id) [Optional]
  - client_id → Clients(id)
  - trip_id → Trips(id) [Optional]
- **Business Logic**: Revenue tracking from multiple sources

### **🔧 Maintenance Entity**
- **Primary Key**: id (BIGSERIAL)
- **Foreign Keys**: 
  - truck_id → Trucks(id)
  - shop_id → Shops(id) [Optional]
- **Key Relationships**: One-to-Many with Tyre_Details
- **Business Logic**: Vehicle maintenance tracking with warranty management

### **🛞 Tyre Details Entity**
- **Primary Key**: id (BIGSERIAL)
- **Foreign Keys**: 
  - maintenance_id → Maintenance(id)
  - truck_id → Trucks(id)
  - shop_id → Shops(id) [Optional]
- **Business Logic**: Detailed tyre tracking with warranty and position management

---

## 🎯 **Business Rules Implemented**

### **Data Integrity Rules**
1. **Referential Integrity**: All foreign keys have proper constraints
2. **Cascade Rules**: Appropriate CASCADE and RESTRICT rules for data protection
3. **Unique Constraints**: Business-critical fields have unique constraints
4. **Check Constraints**: Data validation at database level

### **Financial Rules**
1. **Balance Calculation**: Automatic balance calculation in Builty
2. **Outstanding Tracking**: Client outstanding balance management
3. **Expense Approval**: Approval workflow for expenses
4. **Payment Reconciliation**: Payment tracking against builty

### **Operational Rules**
1. **Trip Status Management**: Proper trip lifecycle management
2. **Vehicle Availability**: Truck assignment validation
3. **Driver Assignment**: Driver availability tracking
4. **Maintenance Scheduling**: Preventive maintenance tracking

---

## 📊 **Database Views for Reporting**

### **1. trip_summary**
- Comprehensive trip information with financial summary
- Profit/Loss calculation per trip

### **2. client_outstanding**
- Client-wise outstanding balance summary
- Pending builty count tracking

### **3. monthly_expense_summary**
- Month-wise expense breakdown by category
- Transaction count and amount summary

### **4. truck_performance**
- Truck-wise performance metrics
- Total trips, distance, income, expenses, and net profit

---

## 🔍 **Indexing Strategy**

### **Performance Indexes**
- **Date-based queries**: Indexes on all date fields
- **Foreign key relationships**: Indexes on all foreign key columns
- **Status-based filtering**: Indexes on status fields
- **Reporting queries**: Composite indexes for common report queries

### **Query Optimization**
- **Covering indexes** for frequently accessed data
- **Partial indexes** for filtered queries
- **Composite indexes** for multi-column searches

---

## 🚀 **Scalability Considerations**

### **Horizontal Scaling**
- **Partitioning strategy** for large tables (trips, expenses)
- **Archive strategy** for historical data
- **Read replicas** for reporting queries

### **Performance Optimization**
- **Connection pooling** configuration
- **Query optimization** with proper indexing
- **Caching strategy** for frequently accessed data

---

This ERD design ensures **strong data relationships**, **comprehensive business logic implementation**, and **optimal performance** for the Shivshakti Transport Management System.

