-- ============================================================
-- Retail Ordering Website - MySQL Schema
-- ============================================================

CREATE DATABASE IF NOT EXISTS retail_ordering;
USE retail_ordering;

-- USER
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('CUSTOMER', 'ADMIN') NOT NULL DEFAULT 'CUSTOMER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- BRAND
CREATE TABLE brand (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    logo_url VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- CATEGORY (self-referencing for hierarchy)
CREATE TABLE category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    parent_id BIGINT,
    CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES category(id)
);

-- PACKAGING
CREATE TABLE packaging (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    description VARCHAR(255)
);

-- PRODUCT
CREATE TABLE product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    brand_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    packaging_id BIGINT NOT NULL,
    price DECIMAL(10, 2) NOT NULL CHECK (price > 0),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_product_brand FOREIGN KEY (brand_id) REFERENCES brand(id),
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category(id),
    CONSTRAINT fk_product_packaging FOREIGN KEY (packaging_id) REFERENCES packaging(id)
);

-- INVENTORY
CREATE TABLE inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL UNIQUE,
    quantity INT NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    reserved_qty INT NOT NULL DEFAULT 0 CHECK (reserved_qty >= 0),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_inventory_product FOREIGN KEY (product_id) REFERENCES product(id)
);

-- CART
CREATE TABLE cart (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- CART_ITEM
CREATE TABLE cart_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    CONSTRAINT fk_cartitem_cart FOREIGN KEY (cart_id) REFERENCES cart(id),
    CONSTRAINT fk_cartitem_product FOREIGN KEY (product_id) REFERENCES product(id)
);

-- ORDER
CREATE TABLE `order` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status ENUM('PLACED', 'CONFIRMED', 'DELIVERED') NOT NULL DEFAULT 'PLACED',
    ordered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ORDER_ITEM
CREATE TABLE order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_orderitem_order FOREIGN KEY (order_id) REFERENCES `order`(id),
    CONSTRAINT fk_orderitem_product FOREIGN KEY (product_id) REFERENCES product(id)
);

-- ============================================================
-- Seed Data
-- ============================================================
INSERT INTO brand (name, logo_url, is_active) VALUES
('Pizza Palace', 'https://via.placeholder.com/100?text=Pizza', TRUE),
('Drink Hub', 'https://via.placeholder.com/100?text=Drinks', TRUE),
('Bread Basket', 'https://via.placeholder.com/100?text=Bread', TRUE);

INSERT INTO category (name, parent_id) VALUES
('Food', NULL),
('Drinks', NULL),
('Pizza', 1),
('Sandwiches', 1),
('Juices', 2),
('Sodas', 2);

INSERT INTO packaging (type, description) VALUES
('Box', 'Cardboard box'),
('Bottle', 'Plastic or glass bottle'),
('Packet', 'Sealed packet');

INSERT INTO product (name, brand_id, category_id, packaging_id, price, is_active) VALUES
('Margherita Pizza', 1, 3, 1, 299.00, TRUE),
('Pepperoni Pizza', 1, 3, 1, 349.00, TRUE),
('Orange Juice', 2, 5, 2, 99.00, TRUE),
('Cola Drink', 2, 6, 2, 59.00, TRUE),
('Whole Wheat Bread', 3, 4, 3, 79.00, TRUE);

INSERT INTO inventory (product_id, quantity, reserved_qty) VALUES
(1, 100, 0),
(2, 80, 0),
(3, 200, 0),
(4, 150, 0),
(5, 120, 0);
