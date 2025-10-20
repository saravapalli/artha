package com.whatsapp.eventservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Database initialization service
 * 
 * This service handles database setup and seeding when the application starts.
 * It implements CommandLineRunner to run after Spring Boot context is loaded.
 */
@Service
public class DatabaseInitializationService implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializationService.class);
    
    @Autowired
    private EventService eventService;
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("🚀 Starting database initialization...");
        
        try {
            // Check if database is already seeded
            if (isDatabaseSeeded()) {
                logger.info("✅ Database already seeded, skipping initialization");
                return;
            }
            
            // Seed sample events
            seedSampleEvents();
            
            logger.info("✅ Database initialization completed successfully");
            
        } catch (Exception e) {
            logger.error("❌ Database initialization failed", e);
            throw e;
        }
    }
    
    /**
     * Check if database is already seeded
     */
    private boolean isDatabaseSeeded() {
        try {
            Map<String, Object> stats = eventService.getEventStats();
            Long eventCount = (Long) stats.get("total_events");
            return eventCount != null && eventCount > 0;
        } catch (Exception e) {
            logger.warn("⚠️ Could not check if database is seeded: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Seed sample events
     */
    private void seedSampleEvents() {
        logger.info("🌱 Seeding database with sample events...");
        
        try {
            // Create a few sample events
            createSampleEvents();
            
            logger.info("✅ Sample events seeded successfully");
            
        } catch (Exception e) {
            logger.error("❌ Error seeding sample events", e);
            throw e;
        }
    }
    
    private void createSampleEvents() {
        // Jazz Night
        com.whatsapp.eventservice.model.Event jazzNight = new com.whatsapp.eventservice.model.Event(
            "Jazz Night at Blue Note",
            "Experience an evening of smooth jazz with local artists.",
            "Blue Note Jazz Club, 123 Music Street",
            "Boston",
            LocalDateTime.now().plusDays(2),
            LocalDateTime.now().plusDays(2).plusHours(3)
        );
        jazzNight.setImageUrl("https://example.com/jazz-night.jpg");
        eventService.createEvent(jazzNight);
        
        // Rock Concert
        com.whatsapp.eventservice.model.Event rockConcert = new com.whatsapp.eventservice.model.Event(
            "Summer Rock Festival",
            "Annual rock festival featuring local and regional bands.",
            "Central Park Amphitheater, 456 Park Avenue",
            "Boston",
            LocalDateTime.now().plusDays(5),
            LocalDateTime.now().plusDays(5).plusHours(6)
        );
        rockConcert.setImageUrl("https://example.com/rock-festival.jpg");
        eventService.createEvent(rockConcert);
        
        // Basketball Game
        com.whatsapp.eventservice.model.Event basketballGame = new com.whatsapp.eventservice.model.Event(
            "Celtics vs Lakers",
            "NBA regular season game. Celtics home game at TD Garden.",
            "TD Garden, 100 Legends Way",
            "Boston",
            LocalDateTime.now().plusDays(3),
            LocalDateTime.now().plusDays(3).plusHours(2)
        );
        basketballGame.setImageUrl("https://example.com/celtics-game.jpg");
        eventService.createEvent(basketballGame);
    }
}