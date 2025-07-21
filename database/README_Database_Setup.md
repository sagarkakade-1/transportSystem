# 🗄️ STMS Database Setup Guide

## 📋 Overview
The Shivshakti Transport Management System uses **environment-specific databases** to ensure proper separation between development, production, and default environments.

## 🎯 Database Environments

| Environment | Database Name | Username | Password | Purpose |
|-------------|---------------|----------|----------|---------|
| **Development** | `stms_dev_db` | `stms_dev_user` | `stms_dev_password` | Development & Testing |
| **Production** | `stms_prod_db` | `stms_prod_user` | `stms_prod_password` | Live Production System |
| **Default** | `stms_db` | `stms_user` | `stms_password` | General Purpose |

## 🚀 Quick Setup (Automated)

### Option 1: Complete Setup Script
Run the automated setup script that creates all databases:

```bash
# Navigate to database directory
cd database/

# Run as PostgreSQL superuser
psql -U postgres -f setup_all_databases.sql
```

This script will:
- ✅ Create all three databases with proper encoding
- ✅ Create dedicated users for each environment
- ✅ Apply the complete schema to all databases
- ✅ Verify the setup

## 🔧 Manual Setup (Step by Step)

### Step 1: Connect to PostgreSQL
```bash
psql -U postgres
```

### Step 2: Create Development Environment
```sql
CREATE DATABASE stms_dev_db WITH ENCODING 'UTF8';
CREATE USER stms_dev_user WITH PASSWORD 'stms_dev_password';
GRANT ALL PRIVILEGES ON DATABASE stms_dev_db TO stms_dev_user;
```

### Step 3: Create Production Environment
```sql
CREATE DATABASE stms_prod_db WITH ENCODING 'UTF8';
CREATE USER stms_prod_user WITH PASSWORD 'stms_prod_password';
GRANT ALL PRIVILEGES ON DATABASE stms_prod_db TO stms_prod_user;
```

### Step 4: Create Default Environment
```sql
CREATE DATABASE stms_db WITH ENCODING 'UTF8';
CREATE USER stms_user WITH PASSWORD 'stms_password';
GRANT ALL PRIVILEGES ON DATABASE stms_db TO stms_user;
```

### Step 5: Apply Schema to Each Database
```bash
# Development
psql -U stms_dev_user -d stms_dev_db -f schema.sql

# Production
psql -U stms_prod_user -d stms_prod_db -f schema.sql

# Default
psql -U stms_user -d stms_db -f schema.sql
```

## 🏃‍♂️ Running the Application

### Development Mode
```bash
mvn spring-boot:run -Dspring.profiles.active=dev
```
- Uses `stms_dev_db` database
- DDL auto: `create-drop` (recreates tables on restart)
- Detailed logging enabled
- Debug mode enabled

### Production Mode
```bash
mvn spring-boot:run -Dspring.profiles.active=prod
```
- Uses `stms_prod_db` database
- DDL auto: `validate` (validates schema only)
- Minimal logging for performance
- Security optimized

### Default Mode
```bash
mvn spring-boot:run
```
- Uses `stms_db` database
- DDL auto: `update` (updates schema as needed)
- Balanced logging and performance

## 🔍 Verification

### Check Database Creation
```sql
-- List all STMS databases
SELECT datname FROM pg_database WHERE datname LIKE 'stms%';
```

### Check Tables in Each Database
```sql
-- Connect to each database and check tables
\c stms_dev_db;
\dt

\c stms_prod_db;
\dt

\c stms_db;
\dt
```

### Expected Tables (11 tables)
- `drivers`
- `clients`
- `shops`
- `trucks`
- `expense_categories`
- `trips`
- `builty`
- `expenses`
- `income`
- `maintenance`
- `tyre_details`
- `payments`

## 🛠️ Configuration Files

### application.properties (Default)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/stms_db
spring.datasource.username=stms_user
spring.datasource.password=stms_password
spring.jpa.hibernate.ddl-auto=update
```

### application-dev.properties (Development)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/stms_dev_db
spring.datasource.username=stms_dev_user
spring.datasource.password=stms_dev_password
spring.jpa.hibernate.ddl-auto=create-drop
```

### application-prod.properties (Production)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/stms_prod_db
spring.datasource.username=stms_prod_user
spring.datasource.password=stms_prod_password
spring.jpa.hibernate.ddl-auto=validate
```

## 🔒 Security Considerations

### Development Environment
- ⚠️ Uses `create-drop` - **data is lost on restart**
- Detailed logging may expose sensitive information
- Debug mode enabled

### Production Environment
- ✅ Uses `validate` - **schema validation only**
- Minimal logging for security
- Optimized for performance and security

### Password Security
- 🔐 Change default passwords in production
- 🔐 Use environment variables for sensitive data
- 🔐 Implement proper database access controls

## 🚨 Troubleshooting

### Common Issues

**1. Connection Refused**
```bash
# Check if PostgreSQL is running
sudo systemctl status postgresql

# Start PostgreSQL if needed
sudo systemctl start postgresql
```

**2. Authentication Failed**
```bash
# Check pg_hba.conf for authentication settings
sudo nano /etc/postgresql/*/main/pg_hba.conf
```

**3. Database Already Exists**
```sql
-- Drop and recreate if needed
DROP DATABASE IF EXISTS stms_dev_db;
-- Then recreate following the setup steps
```

**4. Permission Denied**
```sql
-- Grant additional privileges if needed
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO stms_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO stms_user;
```

## 📊 Database Monitoring

### Check Database Sizes
```sql
SELECT 
    datname as database_name,
    pg_size_pretty(pg_database_size(datname)) as size
FROM pg_database 
WHERE datname LIKE 'stms%';
```

### Check Active Connections
```sql
SELECT 
    datname,
    count(*) as active_connections
FROM pg_stat_activity 
WHERE datname LIKE 'stms%'
GROUP BY datname;
```

---

## 🎯 Next Steps

After database setup is complete:
1. ✅ Verify all databases are created and accessible
2. ✅ Test application startup in each environment
3. ✅ Proceed with Phase 3: Repository Layer Implementation

**Ready to continue with Phase 3: Repository Layer?**

