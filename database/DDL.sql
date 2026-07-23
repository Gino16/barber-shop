-- ============================================
-- Barbería - Esquema de Base de Datos
-- ============================================

-- ==================== ITEMS ====================
-- Service and product catalog
CREATE TABLE items
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    category    VARCHAR(50)  NOT NULL, -- 'SERVICE', 'PRODUCT'
    price       DECIMAL(10, 2),
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ==================== EMPLOYEES ====================
-- Staff members
CREATE TABLE employees
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(100) UNIQUE,
    phone      VARCHAR(20),
    role       VARCHAR(50)  NOT NULL, -- 'BARBER', 'STYLIST', 'MANAGER', 'ADMIN'
    salary     DECIMAL(10, 2),
    is_active  BOOLEAN      NOT NULL DEFAULT TRUE,
    hired_date DATE         NOT NULL DEFAULT CURRENT_DATE,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ==================== CUSTOMERS ====================
-- Client registry
CREATE TABLE customers
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(100),
    phone      VARCHAR(20),
    address    TEXT,
    notes      TEXT,
    is_active  BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ==================== APPOINTMENTS ====================
-- Service bookings
CREATE TABLE appointments
(
    id           SERIAL PRIMARY KEY,
    customer_id  INTEGER     REFERENCES customers (id) ON DELETE SET NULL,
    employee_id  INTEGER     REFERENCES employees (id) ON DELETE SET NULL,
    item_id      INTEGER REFERENCES items (id),
    scheduled_at TIMESTAMP   NOT NULL,
    completed_at TIMESTAMP,
    status       VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- 'PENDING', 'COMPLETED', 'CANCELLED'
    notes        TEXT,
    created_at   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ==================== SALES ====================
-- Sale transactions
CREATE TABLE sales
(
    id             SERIAL PRIMARY KEY,
    customer_id    INTEGER        REFERENCES customers (id) ON DELETE SET NULL,
    employee_id    INTEGER        REFERENCES employees (id) ON DELETE SET NULL,
    sold_at        TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_amount   DECIMAL(10, 2) NOT NULL DEFAULT 0,
    payment_method VARCHAR(20)    NOT NULL, -- 'CASH', 'TRANSFER', 'CARD'
    discount       DECIMAL(10, 2)          DEFAULT 0,
    notes          TEXT,
    created_at     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ==================== SALE_ITEMS ====================
-- Sale details (a sale can contain multiple services/products)
CREATE TABLE sale_items
(
    id              SERIAL PRIMARY KEY,
    sale_id         INTEGER REFERENCES sales (id) ON DELETE CASCADE,
    item_id         INTEGER REFERENCES items (id),
    quantity        INTEGER        NOT NULL DEFAULT 1,
    unit_price      DECIMAL(10, 2) NOT NULL,
    subtotal_amount DECIMAL(10, 2) NOT NULL
);

-- ==================== INVENTORY ====================
-- Product stock tracking
CREATE TABLE inventory
(
    id           SERIAL PRIMARY KEY,
    item_id      INTEGER UNIQUE REFERENCES items (id) ON DELETE CASCADE,
    quantity     INTEGER   NOT NULL DEFAULT 0,
    min_quantity INTEGER   NOT NULL DEFAULT 5,
    cost_price   DECIMAL(10, 2),
    updated_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ==================== AUDIT_LOG ====================
-- Auditoría de cambios
CREATE TABLE audit_log
(
    id          SERIAL PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL, -- 'ITEM', 'CUSTOMER', 'EMPLOYEE', 'SALE', 'APPOINTMENT'
    entity_id   INTEGER     NOT NULL,
    action      VARCHAR(20) NOT NULL, -- 'CREATE', 'UPDATE', 'DELETE'
    old_values  JSONB,
    new_values  JSONB,
    user_id     INTEGER,
    user_name   VARCHAR(100),
    ip_address  VARCHAR(45),
    timestamp   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ==================== DAILY_REPORTS ====================
-- Reportería diaria
CREATE TABLE daily_reports
(
    id                     SERIAL PRIMARY KEY,
    report_date            DATE           NOT NULL UNIQUE,
    total_sales            DECIMAL(10, 2) NOT NULL DEFAULT 0,
    total_transactions     INTEGER        NOT NULL DEFAULT 0,
    cash_sales             DECIMAL(10, 2) NOT NULL DEFAULT 0,
    card_sales             DECIMAL(10, 2) NOT NULL DEFAULT 0,
    transfer_sales         DECIMAL(10, 2) NOT NULL DEFAULT 0,
    services_sales         DECIMAL(10, 2) NOT NULL DEFAULT 0,
    products_sales         DECIMAL(10, 2) NOT NULL DEFAULT 0,
    total_customers        INTEGER        NOT NULL DEFAULT 0,
    new_customers          INTEGER        NOT NULL DEFAULT 0,
    average_transaction    DECIMAL(10, 2),
    top_product_id         INTEGER        REFERENCES items (id) ON DELETE SET NULL,
    top_product_name       VARCHAR(100),
    top_product_quantity   INTEGER,
    total_appointments     INTEGER        NOT NULL DEFAULT 0,
    completed_appointments INTEGER        NOT NULL DEFAULT 0,
    created_at             TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ==================== INDEXES ====================
CREATE INDEX idx_items_category ON items (category);
CREATE INDEX idx_items_is_active ON items (is_active);
CREATE INDEX idx_employees_role ON employees (role);
CREATE INDEX idx_employees_is_active ON employees (is_active);
CREATE INDEX idx_customers_is_active ON customers (is_active);
CREATE INDEX idx_appointments_status ON appointments (status);
CREATE INDEX idx_appointments_scheduled_at ON appointments (scheduled_at);
CREATE INDEX idx_appointments_customer_id ON appointments (customer_id);
CREATE INDEX idx_appointments_employee_id ON appointments (employee_id);
CREATE INDEX idx_sales_customer_id ON sales (customer_id);
CREATE INDEX idx_sales_employee_id ON sales (employee_id);
CREATE INDEX idx_sales_sold_at ON sales (sold_at);
CREATE INDEX idx_sales_payment_method ON sales (payment_method);
CREATE INDEX idx_sale_items_sale_id ON sale_items (sale_id);
CREATE INDEX idx_audit_log_entity ON audit_log (entity_type, entity_id);
CREATE INDEX idx_audit_log_timestamp ON audit_log (timestamp);
CREATE INDEX idx_daily_reports_report_date ON daily_reports (report_date);

