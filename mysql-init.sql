-- ======================================================
-- EVENT + BUSINESS DISCOVERY DATABASE SCHEMA
-- ======================================================
CREATE DATABASE IF NOT EXISTS discovery_assistant
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE discovery_assistant;

-- Drop old tables for clean setup
DROP TABLE IF EXISTS user_feedback;
DROP TABLE IF EXISTS suggested_offer;
DROP TABLE IF EXISTS suggested_event;
DROP TABLE IF EXISTS message;
DROP TABLE IF EXISTS conversation;
DROP TABLE IF EXISTS business_info;
DROP TABLE IF EXISTS offer;
DROP TABLE IF EXISTS event_tag;
DROP TABLE IF EXISTS tag;
DROP TABLE IF EXISTS event;
DROP TABLE IF EXISTS business;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS user_interest;

-- ======================================================
-- USER TABLE
-- ======================================================
CREATE TABLE user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    phone_number VARCHAR(20) UNIQUE,
    email VARCHAR(100) UNIQUE,
    profile_pic_url VARCHAR(255),
    bio TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ======================================================
-- CATEGORY TABLE
-- Shared taxonomy for events and businesses
-- ======================================================
CREATE TABLE category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ======================================================
-- BUSINESS TABLE
-- Stores standalone business info (can exist without events)
-- ======================================================
CREATE TABLE business (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    phone_number VARCHAR(20),
    email VARCHAR(100),
    website_url VARCHAR(255),
    address VARCHAR(255),
    city VARCHAR(100),
    latitude DECIMAL(10, 7),
    longitude DECIMAL(10, 7),
    category_id INT NULL,
    image_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES category(id)
);

-- ======================================================
-- EVENT TABLE
-- Events can be hosted by businesses or exist independently
-- ======================================================
CREATE TABLE event (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category_id BIGINT,
    business_id BIGINT NULL,
    created_by BIGINT NULL,
    start_time DATETIME,
    end_time DATETIME,
    location VARCHAR(255),
    city VARCHAR(100),
    image_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES category(id),
    FOREIGN KEY (business_id) REFERENCES business(id),
    FOREIGN KEY (created_by) REFERENCES user(id),
    CONSTRAINT chk_event_creator CHECK (
        (business_id IS NOT NULL AND created_by IS NULL)
        OR (business_id IS NULL AND created_by IS NOT NULL)
    )
);

-- ======================================================
-- TAG TABLE
-- Flexible classification for both events and businesses
-- ======================================================
CREATE TABLE tag (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ======================================================
-- EVENT_TAG TABLE
-- ======================================================
CREATE TABLE event_tag (
    event_id INT NOT NULL,
    tag_id INT NOT NULL,
    PRIMARY KEY (event_id, tag_id),
    FOREIGN KEY (event_id) REFERENCES event(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE
);

-- ======================================================
-- BUSINESS_INFO TABLE
-- Structured facts about a business (menu, hours, etc.)
-- ======================================================
CREATE TABLE business_info (
    id INT AUTO_INCREMENT PRIMARY KEY,
    business_id INT NOT NULL,
    info_type ENUM('hours', 'menu', 'contact', 'policy', 'parking', 'other') DEFAULT 'other',
    question_pattern VARCHAR(255),
    answer TEXT,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (business_id) REFERENCES business(id)
);

-- ======================================================
-- OFFER TABLE
-- Promotions and deals (can exist without events)
-- ======================================================
CREATE TABLE offer (
    id INT AUTO_INCREMENT PRIMARY KEY,
    business_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    discount_code VARCHAR(50),
    start_date DATE,
    end_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (business_id) REFERENCES business(id)
);

-- ======================================================
-- CONVERSATION TABLE
-- Stores user conversation context
-- ======================================================
CREATE TABLE conversation (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMP NULL,
    context_summary TEXT,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- ======================================================
-- MESSAGE TABLE
-- Chat messages between user and system
-- ======================================================
CREATE TABLE message (
    id INT AUTO_INCREMENT PRIMARY KEY,
    conversation_id INT NOT NULL,
    sender ENUM('user', 'system') NOT NULL,
    content TEXT NOT NULL,
    message_type ENUM('text', 'image', 'audio', 'video', 'interactive') DEFAULT 'text',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conversation_id) REFERENCES conversation(id)
);

-- ======================================================
-- SUGGESTED_EVENT TABLE
-- Records AI event suggestions per conversation
-- ======================================================
CREATE TABLE suggested_event (
    id INT AUTO_INCREMENT PRIMARY KEY,
    conversation_id INT NOT NULL,
    event_id INT NOT NULL,
    suggested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conversation_id) REFERENCES conversation(id),
    FOREIGN KEY (event_id) REFERENCES event(id)
);

-- ======================================================
-- SUGGESTED_OFFER TABLE
-- Records AI offer suggestions per conversation
-- ======================================================
CREATE TABLE suggested_offer (
    id INT AUTO_INCREMENT PRIMARY KEY,
    conversation_id INT NOT NULL,
    offer_id INT NOT NULL,
    suggested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conversation_id) REFERENCES conversation(id),
    FOREIGN KEY (offer_id) REFERENCES offer(id)
);

-- ======================================================
-- USER_FEEDBACK TABLE
-- Tracks user reactions to suggestions
-- ======================================================
CREATE TABLE user_feedback (
    id INT AUTO_INCREMENT PRIMARY KEY,
    suggestion_type ENUM('event', 'offer') NOT NULL,
    suggestion_id INT NOT NULL,
    user_id INT NOT NULL,
    feedback_type ENUM('useful', 'not_useful', 'interested', 'attending', 'ignored') NOT NULL,
    feedback_text TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- ======================================================
-- USER_INTEREST TABLE
-- Tracks user preferences by category, tag, or location
-- ======================================================
CREATE TABLE user_interest (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    category_id INT NULL,
    tag_id INT NULL,
    location VARCHAR(255) NULL,
    interest_weight FLOAT DEFAULT 1.0,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (category_id) REFERENCES category(id),
    FOREIGN KEY (tag_id) REFERENCES tag(id)
);
