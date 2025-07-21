-- ===============================================
-- Shivshakti Transport Management System Database Schema
-- PostgreSQL Database Schema with Strong Relationships
-- ===============================================

-- Create Database (Run this separately if needed)
-- CREATE DATABASE stms_db;
-- CREATE USER stms_user WITH PASSWORD 'stms_password';
-- GRANT ALL PRIVILEGES ON DATABASE stms_db TO stms_user;

-- ===============================================
-- CORE MASTER TABLES
-- ===============================================

-- Drivers Table
CREATE TABLE IF NOT EXISTS drivers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    license_number VARCHAR(50) UNIQUE NOT NULL,
    license_expiry_date DATE NOT NULL,
    contact_number VARCHAR(15) NOT NULL,
    alternate_contact VARCHAR(15),
    address TEXT,
    date_of_birth DATE,
    salary DECIMAL(10,2) DEFAULT 0.00,
    advance_paid DECIMAL(10,2) DEFAULT 0.00,
    is_active BOOLEAN DEFAULT TRUE,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Clients Table
CREATE TABLE IF NOT EXISTS clients (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    company_name VARCHAR(150),
    contact_person VARCHAR(100),
    contact_number VARCHAR(15) NOT NULL,
    email VARCHAR(100),
    address TEXT,
    gst_number VARCHAR(20),
    pan_number VARCHAR(15),
    credit_limit DECIMAL(12,2) DEFAULT 0.00,
    outstanding_balance DECIMAL(12,2) DEFAULT 0.00,
    is_active BOOLEAN DEFAULT TRUE,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Shops Table (For Maintenance, Repairs, Tyre Purchase)
CREATE TABLE IF NOT EXISTS shops (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    shop_type VARCHAR(50) NOT NULL, -- FUEL, REPAIR, TYRE, GENERAL
    contact_person VARCHAR(100),
    contact_number VARCHAR(15),
    address TEXT,
    gst_number VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Trucks Table
CREATE TABLE IF NOT EXISTS trucks (
    id BIGSERIAL PRIMARY KEY,
    truck_number VARCHAR(20) UNIQUE NOT NULL,
    model VARCHAR(50) NOT NULL,
    make VARCHAR(50) NOT NULL,
    capacity_tons DECIMAL(5,2) NOT NULL,
    fuel_type VARCHAR(20) DEFAULT 'DIESEL', -- DIESEL, PETROL, CNG
    purchase_date DATE,
    purchase_price DECIMAL(12,2),
    
    -- Insurance Details
    insurance_company VARCHAR(100),
    insurance_policy_number VARCHAR(50),
    insurance_expiry_date DATE,
    insurance_amount DECIMAL(10,2),
    
    -- RC Book Details
    rc_number VARCHAR(50),
    rc_expiry_date DATE,
    
    -- Permit Details
    permit_number VARCHAR(50),
    permit_expiry_date DATE,
    permit_type VARCHAR(50), -- NATIONAL, STATE, DISTRICT
    
    -- Fitness Details
    fitness_certificate_number VARCHAR(50),
    fitness_expiry_date DATE,
    
    -- PUC Details
    puc_certificate_number VARCHAR(50),
    puc_expiry_date DATE,
    
    current_mileage DECIMAL(10,2) DEFAULT 0.00,
    is_active BOOLEAN DEFAULT TRUE,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===============================================
-- OPERATIONAL TABLES
-- ===============================================

-- Trips Table
CREATE TABLE IF NOT EXISTS trips (
    id BIGSERIAL PRIMARY KEY,
    trip_number VARCHAR(50) UNIQUE NOT NULL,
    truck_id BIGINT NOT NULL REFERENCES trucks(id) ON DELETE RESTRICT,
    driver_id BIGINT NOT NULL REFERENCES drivers(id) ON DELETE RESTRICT,
    
    -- Trip Details
    source_location VARCHAR(100) NOT NULL,
    destination_location VARCHAR(100) NOT NULL,
    distance_km DECIMAL(8,2),
    
    -- Trip Dates
    start_date DATE NOT NULL,
    end_date DATE,
    actual_start_datetime TIMESTAMP,
    actual_end_datetime TIMESTAMP,
    
    -- Trip Status
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, RUNNING, COMPLETED, CANCELLED
    
    -- Fuel Details
    fuel_used_liters DECIMAL(8,2) DEFAULT 0.00,
    fuel_cost DECIMAL(10,2) DEFAULT 0.00,
    
    -- Additional Details
    goods_type VARCHAR(100),
    total_weight_tons DECIMAL(8,2),
    remarks TEXT,
    
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Builty/Invoice Table
CREATE TABLE IF NOT EXISTS builty (
    id BIGSERIAL PRIMARY KEY,
    builty_number VARCHAR(50) UNIQUE NOT NULL,
    trip_id BIGINT NOT NULL REFERENCES trips(id) ON DELETE RESTRICT,
    client_id BIGINT NOT NULL REFERENCES clients(id) ON DELETE RESTRICT,
    
    -- Builty Details
    builty_date DATE NOT NULL,
    goods_type VARCHAR(100) NOT NULL,
    weight_tons DECIMAL(8,2) NOT NULL,
    rate_per_ton DECIMAL(8,2),
    
    -- Financial Details
    total_charges DECIMAL(12,2) NOT NULL,
    advance_received DECIMAL(12,2) DEFAULT 0.00,
    balance_amount DECIMAL(12,2) NOT NULL,
    
    -- Additional Charges
    loading_charges DECIMAL(8,2) DEFAULT 0.00,
    unloading_charges DECIMAL(8,2) DEFAULT 0.00,
    detention_charges DECIMAL(8,2) DEFAULT 0.00,
    other_charges DECIMAL(8,2) DEFAULT 0.00,
    
    -- Status
    payment_status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, PARTIAL, PAID
    
    remarks TEXT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===============================================
-- FINANCIAL TABLES
-- ===============================================

-- Expense Categories
CREATE TABLE IF NOT EXISTS expense_categories (
    id BIGSERIAL PRIMARY KEY,
    category_name VARCHAR(50) UNIQUE NOT NULL, -- FUEL, TOLL, TYRE, REPAIR, DRIVER_ALLOWANCE, MAINTENANCE
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE
);

-- Expenses Table
CREATE TABLE IF NOT EXISTS expenses (
    id BIGSERIAL PRIMARY KEY,
    expense_number VARCHAR(50) UNIQUE NOT NULL,
    trip_id BIGINT REFERENCES trips(id) ON DELETE SET NULL,
    truck_id BIGINT REFERENCES trucks(id) ON DELETE RESTRICT,
    category_id BIGINT NOT NULL REFERENCES expense_categories(id) ON DELETE RESTRICT,
    shop_id BIGINT REFERENCES shops(id) ON DELETE SET NULL,
    
    -- Expense Details
    expense_date DATE NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    description TEXT NOT NULL,
    
    -- Invoice Details
    invoice_number VARCHAR(50),
    invoice_date DATE,
    
    -- GST Details
    gst_amount DECIMAL(8,2) DEFAULT 0.00,
    gst_percentage DECIMAL(5,2) DEFAULT 0.00,
    
    -- Additional Details
    quantity DECIMAL(8,2),
    unit_price DECIMAL(8,2),
    unit VARCHAR(20), -- LITERS, KG, PIECES, etc.
    
    -- Approval Status
    is_approved BOOLEAN DEFAULT FALSE,
    approved_by VARCHAR(100),
    approved_date TIMESTAMP,
    
    remarks TEXT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Income Table
CREATE TABLE IF NOT EXISTS income (
    id BIGSERIAL PRIMARY KEY,
    income_number VARCHAR(50) UNIQUE NOT NULL,
    builty_id BIGINT REFERENCES builty(id) ON DELETE SET NULL,
    client_id BIGINT NOT NULL REFERENCES clients(id) ON DELETE RESTRICT,
    trip_id BIGINT REFERENCES trips(id) ON DELETE SET NULL,
    
    -- Income Details
    income_date DATE NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    income_type VARCHAR(50) NOT NULL, -- TRIP_PAYMENT, ADVANCE, DETENTION, OTHER
    
    -- Payment Details
    payment_mode VARCHAR(20) DEFAULT 'CASH', -- CASH, CHEQUE, BANK_TRANSFER, UPI
    cheque_number VARCHAR(50),
    bank_name VARCHAR(100),
    transaction_reference VARCHAR(100),
    
    description TEXT,
    remarks TEXT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===============================================
-- MAINTENANCE TABLES
-- ===============================================

-- Maintenance Records
CREATE TABLE IF NOT EXISTS maintenance (
    id BIGSERIAL PRIMARY KEY,
    maintenance_number VARCHAR(50) UNIQUE NOT NULL,
    truck_id BIGINT NOT NULL REFERENCES trucks(id) ON DELETE RESTRICT,
    shop_id BIGINT REFERENCES shops(id) ON DELETE SET NULL,
    
    -- Maintenance Details
    maintenance_date DATE NOT NULL,
    maintenance_type VARCHAR(50) NOT NULL, -- SERVICE, REPAIR, TYRE_CHANGE, OIL_CHANGE
    description TEXT NOT NULL,
    
    -- Cost Details
    total_cost DECIMAL(10,2) NOT NULL,
    labour_cost DECIMAL(8,2) DEFAULT 0.00,
    parts_cost DECIMAL(8,2) DEFAULT 0.00,
    
    -- Mileage at Maintenance
    mileage_at_maintenance DECIMAL(10,2),
    
    -- Next Service Details
    next_service_mileage DECIMAL(10,2),
    next_service_date DATE,
    
    -- Warranty Details
    warranty_period_months INTEGER DEFAULT 0,
    warranty_expiry_date DATE,
    
    remarks TEXT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tyre Details Table (Detailed Tyre Tracking)
CREATE TABLE IF NOT EXISTS tyre_details (
    id BIGSERIAL PRIMARY KEY,
    maintenance_id BIGINT NOT NULL REFERENCES maintenance(id) ON DELETE CASCADE,
    truck_id BIGINT NOT NULL REFERENCES trucks(id) ON DELETE RESTRICT,
    shop_id BIGINT REFERENCES shops(id) ON DELETE SET NULL,
    
    -- Tyre Details
    tyre_company VARCHAR(50) NOT NULL,
    tyre_model VARCHAR(50),
    tyre_size VARCHAR(20) NOT NULL,
    tyre_position VARCHAR(20) NOT NULL, -- FRONT_LEFT, FRONT_RIGHT, REAR_LEFT, REAR_RIGHT, SPARE
    
    -- Purchase Details
    purchase_date DATE NOT NULL,
    purchase_price DECIMAL(8,2) NOT NULL,
    invoice_number VARCHAR(50),
    
    -- Warranty Details
    warranty_months INTEGER DEFAULT 0,
    warranty_expiry_date DATE,
    warranty_km INTEGER DEFAULT 0,
    
    -- Installation Details
    installation_date DATE,
    mileage_at_installation DECIMAL(10,2),
    
    -- GST Details
    gst_amount DECIMAL(6,2) DEFAULT 0.00,
    gst_percentage DECIMAL(5,2) DEFAULT 0.00,
    
    is_active BOOLEAN DEFAULT TRUE,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===============================================
-- PAYMENT TRACKING
-- ===============================================

-- Payments Table (Detailed Payment Tracking)
CREATE TABLE IF NOT EXISTS payments (
    id BIGSERIAL PRIMARY KEY,
    payment_number VARCHAR(50) UNIQUE NOT NULL,
    builty_id BIGINT REFERENCES builty(id) ON DELETE SET NULL,
    client_id BIGINT NOT NULL REFERENCES clients(id) ON DELETE RESTRICT,
    
    -- Payment Details
    payment_date DATE NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    payment_type VARCHAR(20) NOT NULL, -- ADVANCE, PARTIAL, FULL
    payment_mode VARCHAR(20) DEFAULT 'CASH', -- CASH, CHEQUE, BANK_TRANSFER, UPI
    
    -- Bank/Cheque Details
    cheque_number VARCHAR(50),
    cheque_date DATE,
    bank_name VARCHAR(100),
    branch_name VARCHAR(100),
    transaction_reference VARCHAR(100),
    
    -- Status
    status VARCHAR(20) DEFAULT 'RECEIVED', -- RECEIVED, CLEARED, BOUNCED
    cleared_date DATE,
    
    remarks TEXT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===============================================
-- INDEXES FOR PERFORMANCE
-- ===============================================

-- Primary Indexes
CREATE INDEX IF NOT EXISTS idx_trips_truck_id ON trips(truck_id);
CREATE INDEX IF NOT EXISTS idx_trips_driver_id ON trips(driver_id);
CREATE INDEX IF NOT EXISTS idx_trips_status ON trips(status);
CREATE INDEX IF NOT EXISTS idx_trips_start_date ON trips(start_date);

CREATE INDEX IF NOT EXISTS idx_builty_trip_id ON builty(trip_id);
CREATE INDEX IF NOT EXISTS idx_builty_client_id ON builty(client_id);
CREATE INDEX IF NOT EXISTS idx_builty_date ON builty(builty_date);
CREATE INDEX IF NOT EXISTS idx_builty_payment_status ON builty(payment_status);

CREATE INDEX IF NOT EXISTS idx_expenses_trip_id ON expenses(trip_id);
CREATE INDEX IF NOT EXISTS idx_expenses_truck_id ON expenses(truck_id);
CREATE INDEX IF NOT EXISTS idx_expenses_category_id ON expenses(category_id);
CREATE INDEX IF NOT EXISTS idx_expenses_date ON expenses(expense_date);

CREATE INDEX IF NOT EXISTS idx_income_client_id ON income(client_id);
CREATE INDEX IF NOT EXISTS idx_income_builty_id ON income(builty_id);
CREATE INDEX IF NOT EXISTS idx_income_date ON income(income_date);

CREATE INDEX IF NOT EXISTS idx_maintenance_truck_id ON maintenance(truck_id);
CREATE INDEX IF NOT EXISTS idx_maintenance_date ON maintenance(maintenance_date);

CREATE INDEX IF NOT EXISTS idx_payments_client_id ON payments(client_id);
CREATE INDEX IF NOT EXISTS idx_payments_builty_id ON payments(builty_id);
CREATE INDEX IF NOT EXISTS idx_payments_date ON payments(payment_date);

-- ===============================================
-- INSERT DEFAULT DATA
-- ===============================================

-- Insert Default Expense Categories
INSERT INTO expense_categories (category_name, description) VALUES
('FUEL', 'Fuel expenses for trucks'),
('TOLL', 'Toll tax payments'),
('TYRE', 'Tyre purchase and replacement'),
('REPAIR', 'Vehicle repair and maintenance'),
('DRIVER_ALLOWANCE', 'Driver daily allowance and payments'),
('MAINTENANCE', 'Regular maintenance and servicing'),
('INSURANCE', 'Insurance premium payments'),
('PERMIT', 'Permit and license renewals'),
('OTHER', 'Other miscellaneous expenses')
ON CONFLICT (category_name) DO NOTHING;

-- ===============================================
-- VIEWS FOR REPORTING
-- ===============================================

-- Trip Summary View
CREATE OR REPLACE VIEW trip_summary AS
SELECT 
    t.id,
    t.trip_number,
    tr.truck_number,
    d.name as driver_name,
    t.source_location,
    t.destination_location,
    t.start_date,
    t.end_date,
    t.status,
    t.distance_km,
    COALESCE(SUM(b.total_charges), 0) as total_income,
    COALESCE(SUM(e.amount), 0) as total_expenses,
    COALESCE(SUM(b.total_charges), 0) - COALESCE(SUM(e.amount), 0) as profit_loss
FROM trips t
LEFT JOIN trucks tr ON t.truck_id = tr.id
LEFT JOIN drivers d ON t.driver_id = d.id
LEFT JOIN builty b ON t.id = b.trip_id
LEFT JOIN expenses e ON t.id = e.trip_id
GROUP BY t.id, tr.truck_number, d.name;

-- Client Outstanding View
CREATE OR REPLACE VIEW client_outstanding AS
SELECT 
    c.id,
    c.name,
    c.company_name,
    COALESCE(SUM(b.balance_amount), 0) as total_outstanding,
    COUNT(b.id) as pending_builty_count
FROM clients c
LEFT JOIN builty b ON c.id = b.client_id AND b.payment_status != 'PAID'
GROUP BY c.id, c.name, c.company_name;

-- Monthly Expense Summary View
CREATE OR REPLACE VIEW monthly_expense_summary AS
SELECT 
    DATE_TRUNC('month', expense_date) as month_year,
    ec.category_name,
    COUNT(*) as transaction_count,
    SUM(amount) as total_amount
FROM expenses e
JOIN expense_categories ec ON e.category_id = ec.id
GROUP BY DATE_TRUNC('month', expense_date), ec.category_name
ORDER BY month_year DESC, total_amount DESC;

-- Truck Performance View
CREATE OR REPLACE VIEW truck_performance AS
SELECT 
    tr.id,
    tr.truck_number,
    COUNT(t.id) as total_trips,
    COALESCE(SUM(t.distance_km), 0) as total_distance,
    COALESCE(SUM(b.total_charges), 0) as total_income,
    COALESCE(SUM(e.amount), 0) as total_expenses,
    COALESCE(SUM(b.total_charges), 0) - COALESCE(SUM(e.amount), 0) as net_profit
FROM trucks tr
LEFT JOIN trips t ON tr.id = t.truck_id
LEFT JOIN builty b ON t.id = b.trip_id
LEFT JOIN expenses e ON t.id = e.trip_id
GROUP BY tr.id, tr.truck_number
ORDER BY net_profit DESC;

