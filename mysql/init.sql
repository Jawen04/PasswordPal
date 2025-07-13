CREATE DATABASE IF NOT EXISTS usersDB;
USE usersDB;

-- Users table for your password manager users
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL  -- hashed password for your service login
);

-- Table to store multiple external logins per user
CREATE TABLE user_accounts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    service_name VARCHAR(100) NOT NULL,
    login_username VARCHAR(100) NOT NULL,
    login_password VARCHAR(255) NOT NULL,  -- encrypted password for the external service
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

INSERT INTO users (username, password) VALUES ('admin', 'adminpass');