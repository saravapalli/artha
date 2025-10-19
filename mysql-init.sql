-- ======================================================
-- EVENT DISCOVERY BACKEND DATABASE SCHEMA
-- ======================================================
-- Purpose: Store users, events, conversations, and personalized recommendations
-- Use Case: Supports AI-driven event suggestions and feedback tracking
-- ======================================================

-- Use UTF8MB4 for full Unicode support (emojis, special characters)
CREATE DATABASE IF NOT EXISTS event_assistant
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE event_assistant;

-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS user_feedback;
DROP TABLE IF EXISTS suggested_event;
DROP TABLE IF EXISTS message;
DROP TABLE IF EXISTS conversation;
DROP TABLE IF EXISTS event_tag;
DROP TABLE IF EXISTS tag;
DROP TABLE IF EXISTS event;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS user;

-- ======================================================
-- USER TABLE
-- Stores information about each user
-- ======================================================
-- Use Cases:
-- 1. Identify users and their preferences
-- 2. Link conversations, events, and feedback to a user
CREATE TABLE user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),                           -- User's full name
    phone_number VARCHAR(20) UNIQUE,            -- Unique identifier via phone
    email VARCHAR(100) UNIQUE,                  -- Optional email for notifications
    profile_pic_url VARCHAR(255),               -- Optional avatar/profile image
    bio TEXT,                                   -- Optional user description
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ======================================================
-- CATEGORY TABLE
-- Stores high-level event categories (e.g., Music, Sports)
-- ======================================================
-- Use Cases:
-- 1. Classify events for filtering/searching
-- 2. Link user interests to categories
CREATE TABLE category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,          -- Category name
    description TEXT,                           -- Optional category description
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ======================================================
-- EVENT TABLE
-- Stores detailed information about events
-- ======================================================
-- Use Cases:
-- 1. Maintain event information (title, description, time, location)
-- 2. Link events to categories and tags
-- 3. Track who created the event (admin/user)
CREATE TABLE event (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,                -- Event title
    description TEXT,                           -- Detailed description
    location VARCHAR(255),                       -- Physical or virtual address
    city VARCHAR(100),                           -- City where event takes place
    start_time DATETIME,                         -- Event start
    end_time DATETIME,                           -- Event end
    category_id INT,                             -- Main category
    created_by INT,                              -- User who added event
    image_url VARCHAR(255),                      -- Optional event image
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES category(id),
    FOREIGN KEY (created_by) REFERENCES user(id)
);

-- ======================================================
-- TAG TABLE
-- Flexible labels for events
-- ======================================================
-- Use Cases:
-- 1. Enable multi-dimensional search (e.g., "outdoor", "free", "yoga")
-- 2. Help AI infer user preferences
CREATE TABLE tag (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,          -- Tag name
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ======================================================
-- EVENT_TAG TABLE
-- Links events to multiple tags (many-to-many)
-- ======================================================
-- Use Cases:
-- 1. Map events to multiple tags
-- 2. Support flexible filtering and recommendation queries
CREATE TABLE event_tag (
    event_id INT NOT NULL,
    tag_id INT NOT NULL,
    PRIMARY KEY (event_id, tag_id),
    FOREIGN KEY (event_id) REFERENCES event(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE
);

-- ======================================================
-- CONVERSATION TABLE
-- Stores each user conversation with the system
-- ======================================================
-- Use Cases:
-- 1. Track context for AI recommendations
-- 2. Maintain conversation history for analytics
CREATE TABLE conversation (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,                        -- Reference to user
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Conversation start time
    ended_at TIMESTAMP NULL,                     -- Conversation end
    context_summary TEXT,                        -- Optional AI-generated summary
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- ======================================================
-- MESSAGE TABLE
-- Stores messages within a conversation
-- ======================================================
-- Use Cases:
-- 1. Log messages for context
-- 2. Feed conversation history to AI for better recommendations
CREATE TABLE message (
    id INT AUTO_INCREMENT PRIMARY KEY,
    conversation_id INT NOT NULL,                -- Reference to conversation
    sender ENUM('user', 'system') NOT NULL,     -- Message sender type
    content TEXT NOT NULL,                       -- Message content
    message_type ENUM('text', 'image', 'audio', 'video', 'interactive') DEFAULT 'text',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conversation_id) REFERENCES conversation(id)
);

-- ======================================================
-- SUGGESTED_EVENT TABLE
-- Records events suggested to a user during a conversation
-- ======================================================
-- Use Cases:
-- 1. Track which events were suggested in each conversation
-- 2. Link suggestions to feedback for learning preferences
CREATE TABLE suggested_event (
    id INT AUTO_INCREMENT PRIMARY KEY,
    conversation_id INT NOT NULL,
    event_id INT NOT NULL,
    suggested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Time of suggestion
    FOREIGN KEY (conversation_id) REFERENCES conversation(id),
    FOREIGN KEY (event_id) REFERENCES event(id)
);

-- ======================================================
-- USER_FEEDBACK TABLE
-- Captures user feedback on suggested events
-- ======================================================
-- Use Cases:
-- 1. Capture if a user found the suggestion useful or interesting
-- 2. Update AI user profiles based on feedback
CREATE TABLE user_feedback (
    id INT AUTO_INCREMENT PRIMARY KEY,
    suggested_event_id INT NOT NULL,
    user_id INT NOT NULL,
    feedback_type ENUM('useful', 'not_useful', 'interested', 'attending', 'ignored') NOT NULL,
    feedback_text TEXT,                          -- Optional textual feedback
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (suggested_event_id) REFERENCES suggested_event(id),
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- ======================================================
-- USER_INTEREST TABLE
-- Stores learned user preferences over time
-- ======================================================
-- Use Cases:
-- 1. Track user interests by tag and category
-- 2. Provide personalized event recommendations
-- 3. Adjust recommendation weightings based on engagement
CREATE TABLE user_interest (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    category_id INT NULL,                         -- Optional category preference
    tag_id INT NULL,                              -- Optional tag preference
    location VARCHAR(255) NULL,                   -- Optional preferred location
    interest_weight FLOAT DEFAULT 1.0,            -- Strength of interest
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (category_id) REFERENCES category(id),
    FOREIGN KEY (tag_id) REFERENCES tag(id)
);
