-- Seed Users
INSERT IGNORE INTO users (id, name, email, password_hash, role, created_at) VALUES (1, 'John Doe', 'john@example.com', 'password123', 'CUSTOMER', CURRENT_TIMESTAMP);
INSERT IGNORE INTO users (id, name, email, password_hash, role, created_at) VALUES (2, 'Admin User', 'admin@example.com', 'admin123', 'ADMIN', CURRENT_TIMESTAMP);

-- Seed Brands
INSERT IGNORE INTO brands (id, name, logo_url, is_active) VALUES (1, 'Brand A', 'http://logo.com/a', true);
INSERT IGNORE INTO brands (id, name, logo_url, is_active) VALUES (2, 'Brand B', 'http://logo.com/b', true);

-- Seed Categories
INSERT IGNORE INTO categories (id, name, parent_id) VALUES (1, 'Electronics', NULL);
INSERT IGNORE INTO categories (id, name, parent_id) VALUES (2, 'Laptops', 1);

-- Seed Packaging
INSERT IGNORE INTO packaging (id, type, description) VALUES (1, 'BOX', 'Standard cardboard box');
INSERT IGNORE INTO packaging (id, type, description) VALUES (2, 'PACKET', 'Plastic packet');

-- Seed Products
INSERT IGNORE INTO products (id, name, price, is_active, brand_id, category_id, packaging_id) VALUES (1, 'Laptop X', 1200.00, true, 1, 2, 1);
INSERT IGNORE INTO products (id, name, price, is_active, brand_id, category_id, packaging_id) VALUES (2, 'Mouse Y', 25.00, true, 2, 1, 2);

-- Seed Inventory
INSERT IGNORE INTO inventory (id, product_id, quantity, reserved_qty, updated_at) VALUES (1, 1, 10, 0, CURRENT_TIMESTAMP);
INSERT IGNORE INTO inventory (id, product_id, quantity, reserved_qty, updated_at) VALUES (2, 2, 50, 0, CURRENT_TIMESTAMP);
