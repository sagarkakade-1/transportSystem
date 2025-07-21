-- ===============================================
-- STMS Database Setup Script for All Environments
-- Run this script as PostgreSQL superuser (postgres)
-- ===============================================

-- ===============================================
-- 1. DEVELOPMENT ENVIRONMENT SETUP
-- ===============================================
\echo 'Setting up Development Environment...'

-- Drop existing development database if exists
DROP DATABASE IF EXISTS stms_dev_db;
DROP USER IF EXISTS stms_dev_user;

-- Create development database and user
CREATE DATABASE stms_dev_db
    WITH ENCODING 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    TEMPLATE template0;

CREATE USER stms_dev_user WITH PASSWORD 'stms_dev_password';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE stms_dev_db TO stms_dev_user;
ALTER USER stms_dev_user CREATEDB;

\echo 'Development database created successfully!'

-- ===============================================
-- 2. PRODUCTION ENVIRONMENT SETUP
-- ===============================================
\echo 'Setting up Production Environment...'

-- Drop existing production database if exists
DROP DATABASE IF EXISTS stms_prod_db;
DROP USER IF EXISTS stms_prod_user;

-- Create production database and user
CREATE DATABASE stms_prod_db
    WITH ENCODING 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    TEMPLATE template0;

CREATE USER stms_prod_user WITH PASSWORD 'stms_prod_password';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE stms_prod_db TO stms_prod_user;

\echo 'Production database created successfully!'

-- ===============================================
-- 3. DEFAULT ENVIRONMENT SETUP
-- ===============================================
\echo 'Setting up Default Environment...'

-- Drop existing default database if exists
DROP DATABASE IF EXISTS stms_db;
DROP USER IF EXISTS stms_user;

-- Create default database and user
CREATE DATABASE stms_db
    WITH ENCODING 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    TEMPLATE template0;

CREATE USER stms_user WITH PASSWORD 'stms_password';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE stms_db TO stms_user;

\echo 'Default database created successfully!'

-- ===============================================
-- 4. APPLY SCHEMA TO ALL DATABASES
-- ===============================================

\echo 'Applying schema to Development database...'
\c stms_dev_db stms_dev_user;
\i schema.sql

\echo 'Applying schema to Production database...'
\c stms_prod_db stms_prod_user;
\i schema.sql

\echo 'Applying schema to Default database...'
\c stms_db stms_user;
\i schema.sql

-- ===============================================
-- 5. VERIFICATION
-- ===============================================
\echo 'Verifying database setup...'

-- Check Development Database
\c stms_dev_db;
SELECT 'Development DB - Tables Count: ' || COUNT(*) as status FROM information_schema.tables WHERE table_schema = 'public';

-- Check Production Database  
\c stms_prod_db;
SELECT 'Production DB - Tables Count: ' || COUNT(*) as status FROM information_schema.tables WHERE table_schema = 'public';

-- Check Default Database
\c stms_db;
SELECT 'Default DB - Tables Count: ' || COUNT(*) as status FROM information_schema.tables WHERE table_schema = 'public';

\echo '==============================================='
\echo 'STMS Database Setup Complete!'
\echo '==============================================='
\echo 'Development: stms_dev_db (user: stms_dev_user)'
\echo 'Production:  stms_prod_db (user: stms_prod_user)'
\echo 'Default:     stms_db (user: stms_user)'
\echo '==============================================='

