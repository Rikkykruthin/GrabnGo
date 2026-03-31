-- Food Ordering Application Database Schema

CREATE DATABASE IF NOT EXISTS food_ordering_app;
USE food_ordering_app;

-- Users table
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Restaurants table
CREATE TABLE restaurants (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    address VARCHAR(500),
    image_url VARCHAR(500),
    rating DECIMAL(2,1) DEFAULT 4.0,
    latitude DECIMAL(10,8) NOT NULL,
    longitude DECIMAL(11,8) NOT NULL
);

-- Menu items table
CREATE TABLE menu_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    restaurant_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    category VARCHAR(100),
    image_url VARCHAR(500),
    is_available BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
);

-- Orders table
CREATE TABLE orders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    restaurant_id INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    delivery_fee DECIMAL(10,2) DEFAULT 0.00,
    distance_km DECIMAL(10,2) DEFAULT 0.00,
    grand_total DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    delivery_address TEXT,
    user_latitude DECIMAL(10,8),
    user_longitude DECIMAL(11,8),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
);

-- Order items table
CREATE TABLE order_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    menu_item_id INT NOT NULL,
    menu_item_name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (menu_item_id) REFERENCES menu_items(id)
);

-- Seed sample restaurants
INSERT INTO restaurants (name, description, address, image_url, rating, latitude, longitude) VALUES
('Pizza Palace', 'Authentic Italian pizzas with fresh ingredients', '123 Main Street, Downtown', 'https://images.unsplash.com/photo-1513104890138-7c749659a591', 4.5, 12.9716, 77.5946),
('Burger Hub', 'Gourmet burgers and crispy fries', '456 Oak Avenue, Midtown', 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd', 4.3, 12.9352, 77.6245),
('Sushi World', 'Fresh sushi and Japanese cuisine', '789 Pine Road, Uptown', 'https://images.unsplash.com/photo-1579584425555-c3ce17fd4351', 4.7, 12.9141, 77.6411),
('Taco Fiesta', 'Mexican street food and tacos', '321 Elm Street, Westside', 'https://images.unsplash.com/photo-1565299585323-38d6b0865b47', 4.4, 13.0827, 80.2707);

-- Seed menu items for Pizza Palace
INSERT INTO menu_items (restaurant_id, name, description, price, category, image_url) VALUES
(1, 'Margherita Pizza', 'Classic tomato, mozzarella, and basil', 12.99, 'Pizza', 'https://images.unsplash.com/photo-1574071318508-1cdbab80d002'),
(1, 'Pepperoni Pizza', 'Loaded with pepperoni and cheese', 14.99, 'Pizza', 'https://images.unsplash.com/photo-1628840042765-356cda07504e'),
(1, 'Veggie Supreme', 'Bell peppers, onions, mushrooms, olives', 13.99, 'Pizza', 'https://images.unsplash.com/photo-1571997478779-2adcbbe9ab2f'),
(1, 'Garlic Bread', 'Toasted bread with garlic butter', 5.99, 'Sides', 'https://images.unsplash.com/photo-1573140401552-388e3c0b1f6e');

-- Seed menu items for Burger Hub
INSERT INTO menu_items (restaurant_id, name, description, price, category, image_url) VALUES
(2, 'Classic Burger', 'Beef patty, lettuce, tomato, onion', 9.99, 'Burgers', 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd'),
(2, 'Cheese Burger', 'Double cheese with special sauce', 10.99, 'Burgers', 'https://images.unsplash.com/photo-1572802419224-296b0aeee0d9'),
(2, 'Bacon Burger', 'Crispy bacon and cheddar cheese', 12.99, 'Burgers', 'https://images.unsplash.com/photo-1553979459-d2229ba7433b'),
(2, 'French Fries', 'Crispy golden fries', 4.99, 'Sides', 'https://images.unsplash.com/photo-1573080496219-bb080dd4f877');

-- Seed menu items for Sushi World
INSERT INTO menu_items (restaurant_id, name, description, price, category, image_url) VALUES
(3, 'California Roll', 'Crab, avocado, cucumber', 8.99, 'Rolls', 'https://images.unsplash.com/photo-1579584425555-c3ce17fd4351'),
(3, 'Salmon Nigiri', 'Fresh salmon over rice (6 pieces)', 11.99, 'Nigiri', 'https://images.unsplash.com/photo-1564489563601-c53cfc451e93'),
(3, 'Spicy Tuna Roll', 'Tuna with spicy mayo', 10.99, 'Rolls', 'https://images.unsplash.com/photo-1617196034796-73dfa7b1fd56'),
(3, 'Miso Soup', 'Traditional Japanese soup', 3.99, 'Soup', 'https://images.unsplash.com/photo-1606491956689-2ea866880c84');

-- Seed menu items for Taco Fiesta
INSERT INTO menu_items (restaurant_id, name, description, price, category, image_url) VALUES
(4, 'Beef Tacos', 'Seasoned beef with fresh toppings (3 pcs)', 8.99, 'Tacos', 'https://images.unsplash.com/photo-1565299585323-38d6b0865b47'),
(4, 'Chicken Quesadilla', 'Grilled chicken and melted cheese', 9.99, 'Quesadillas', 'https://images.unsplash.com/photo-1618040996337-56904b7850b9'),
(4, 'Veggie Burrito', 'Rice, beans, vegetables wrapped', 10.99, 'Burritos', 'https://images.unsplash.com/photo-1626700051175-6818013e1d4f'),
(4, 'Nachos Supreme', 'Loaded nachos with all toppings', 7.99, 'Appetizers', 'https://images.unsplash.com/photo-1582169296194-e4d644c48063');
