-- Create database
CREATE DATABASE IF NOT EXISTS urbancart;
USE urbancart;

-- Users table
CREATE TABLE Users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role ENUM('BUYER', 'SELLER', 'ADMIN') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- Products table
CREATE TABLE Products (
    id INT PRIMARY KEY AUTO_INCREMENT,
    seller_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    image_path VARCHAR(500),
    list_type ENUM('BUY', 'BID', 'LEND') NOT NULL,
    price DECIMAL(10,2), -- For BUY and LEND (per day price)
    starting_price DECIMAL(10,2), -- For BID
    current_bid DECIMAL(10,2), -- For BID (highest current bid)
    current_bidder_id INT, -- For BID
    auction_end_time DATETIME, -- For BID
    availability_status ENUM('AVAILABLE', 'OUT_OF_STOCK', 'ON_LOAN') DEFAULT 'AVAILABLE',
    available_from DATE, -- For LEND
    available_until DATE, -- For LEND
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (seller_id) REFERENCES Users(id),
    FOREIGN KEY (current_bidder_id) REFERENCES Users(id)
);

-- Cart items table
CREATE TABLE CartItems (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT DEFAULT 1,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(id),
    FOREIGN KEY (product_id) REFERENCES Products(id),
    UNIQUE KEY unique_cart_item (user_id, product_id)
);

-- Orders table
CREATE TABLE Orders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2) NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED') DEFAULT 'PENDING',
    shipping_address TEXT,
    payment_method VARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES Users(id)
);

-- Order items table
CREATE TABLE OrderItems (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES Orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES Products(id)
);

-- Price history table
CREATE TABLE PriceHistory (
    id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    change_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES Products(id)
);

-- Price alerts table
CREATE TABLE PriceAlerts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    target_price DECIMAL(10,2) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(id),
    FOREIGN KEY (product_id) REFERENCES Products(id),
    UNIQUE KEY unique_alert (user_id, product_id)
);

-- Bids table
CREATE TABLE Bids (
    id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT NOT NULL,
    user_id INT NOT NULL,
    bid_amount DECIMAL(10,2) NOT NULL,
    bid_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_winning BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (product_id) REFERENCES Products(id),
    FOREIGN KEY (user_id) REFERENCES Users(id)
);

-- Loans table (for rentals)
CREATE TABLE Loans (
    id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT NOT NULL,
    user_id INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    total_rental_fee DECIMAL(10,2) NOT NULL,
    status ENUM('PENDING', 'ACTIVE', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES Products(id),
    FOREIGN KEY (user_id) REFERENCES Users(id)
);

-- Notifications table
CREATE TABLE Notifications (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    type ENUM('PRICE_ALERT', 'BID_WON', 'AUCTION_ENDED', 'SYSTEM') DEFAULT 'SYSTEM',
    FOREIGN KEY (user_id) REFERENCES Users(id)
);

-- Insert sample data

-- Users (password is 'password123' encrypted)
INSERT INTO Users (username, password, email, role) VALUES
('admin', '$2a$10$NLMK3e3kbb6V5dQ7JQ8b/.Q8bQ8bQ8bQ8bQ8bQ8bQ8bQ8bQ8bQ8b', 'admin@urbankart.com', 'ADMIN'),
('seller1', '$2a$10$NLMK3e3kbb6V5dQ7JQ8b/.Q8bQ8bQ8bQ8bQ8bQ8bQ8bQ8bQ8bQ8b', 'seller1@urbankart.com', 'SELLER'),
('seller2', '$2a$10$NLMK3e3kbb6V5dQ7JQ8b/.Q8bQ8bQ8bQ8bQ8bQ8bQ8bQ8bQ8bQ8b', 'seller2@urbankart.com', 'SELLER'),
('buyer1', '$2a$10$NLMK3e3kbb6V5dQ7JQ8b/.Q8bQ8bQ8bQ8bQ8bQ8bQ8bQ8bQ8bQ8b', 'buyer1@urbankart.com', 'BUYER'),
('buyer2', '$2a$10$NLMK3e3kbb6V5dQ7JQ8b/.Q8bQ8bQ8bQ8bQ8bQ8bQ8bQ8bQ8bQ8b', 'buyer2@urbankart.com', 'BUYER'),
('buyer3', '$2a$10$NLMK3e3kbb6V5dQ7JQ8b/.Q8bQ8bQ8bQ8bQ8bQ8bQ8bQ8bQ8bQ8b', 'buyer3@urbankart.com', 'BUYER');

-- Products
INSERT INTO Products (seller_id, name, description, category, image_path, list_type, price, starting_price, current_bid, auction_end_time, availability_status) VALUES
(2, 'Wireless Headphones', 'High-quality wireless headphones with noise cancellation', 'Electronics', '/images/headphones.jpg', 'BUY', 99.99, NULL, NULL, NULL, 'AVAILABLE'),
(2, 'Smartphone', 'Latest smartphone with advanced camera', 'Electronics', '/images/smartphone.jpg', 'BID', NULL, 299.99, 320.00, DATE_ADD(NOW(), INTERVAL 7 DAY), 'AVAILABLE'),
(3, 'Designer Handbag', 'Genuine leather handbag', 'Fashion', '/images/handbag.jpg', 'LEND', 15.00, NULL, NULL, NULL, 'AVAILABLE'),
(3, 'Vintage Watch', 'Classic vintage wristwatch', 'Accessories', '/images/watch.jpg', 'BID', NULL, 150.00, 165.00, DATE_ADD(NOW(), INTERVAL 3 DAY), 'AVAILABLE'),
(2, 'Laptop', 'Gaming laptop with RTX graphics', 'Electronics', '/images/laptop.jpg', 'BUY', 899.99, NULL, NULL, NULL, 'AVAILABLE'),
(3, 'Camera', 'Professional DSLR camera', 'Electronics', '/images/camera.jpg', 'LEND', 25.00, NULL, NULL, NULL, 'AVAILABLE'),
(2, 'Bookshelf', 'Wooden bookshelf with 5 shelves', 'Furniture', '/images/bookshelf.jpg', 'BUY', 129.99, NULL, NULL, NULL, 'AVAILABLE'),
(3, 'Mountain Bike', 'Professional mountain bike', 'Sports', '/images/bike.jpg', 'LEND', 20.00, NULL, NULL, NULL, 'AVAILABLE'),
(2, 'Gaming Console', 'Latest gaming console', 'Electronics', '/images/console.jpg', 'BID', NULL, 199.99, 210.00, DATE_ADD(NOW(), INTERVAL 5 DAY), 'AVAILABLE'),
(3, 'Coffee Maker', 'Automatic coffee maker', 'Home', '/images/coffee.jpg', 'BUY', 49.99, NULL, NULL, NULL, 'AVAILABLE');

-- Price History
INSERT INTO PriceHistory (product_id, price) VALUES
(1, 109.99),
(1, 99.99),
(5, 949.99),
(5, 899.99),
(7, 149.99),
(7, 129.99),
(10, 59.99),
(10, 49.99);

-- Price Alerts
INSERT INTO PriceAlerts (user_id, product_id, target_price) VALUES
(4, 1, 90.00),
(4, 5, 850.00),
(5, 7, 120.00);

-- Bids
INSERT INTO Bids (product_id, user_id, bid_amount, is_winning) VALUES
(2, 4, 320.00, TRUE),
(4, 5, 165.00, TRUE),
(9, 4, 210.00, TRUE);

-- Cart Items
INSERT INTO CartItems (user_id, product_id, quantity) VALUES
(4, 1, 1),
(4, 10, 2),
(5, 7, 1);

-- Create indexes for better performance
CREATE INDEX idx_products_seller ON Products(seller_id);
CREATE INDEX idx_products_category ON Products(category);
CREATE INDEX idx_products_list_type ON Products(list_type);
CREATE INDEX idx_bids_product ON Bids(product_id);
CREATE INDEX idx_bids_user ON Bids(user_id);
CREATE INDEX idx_orders_user ON Orders(user_id);
CREATE INDEX idx_price_history_product ON PriceHistory(product_id);
CREATE INDEX idx_price_alerts_user ON PriceAlerts(user_id);