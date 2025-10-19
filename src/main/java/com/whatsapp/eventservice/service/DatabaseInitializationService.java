package com.whatsapp.eventservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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
            long eventCount = eventService.getEventStats().get("total_events", Long.class);
            return eventCount > 0;
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
            // Music Events
            createMusicEvents();
            
            // Sports Events
            createSportsEvents();
            
            // Family Events
            createFamilyEvents();
            
            // Art Events
            createArtEvents();
            
            // Food Events
            createFoodEvents();
            
            // Education Events
            createEducationEvents();
            
            logger.info("✅ Sample events seeded successfully");
            
        } catch (Exception e) {
            logger.error("❌ Error seeding sample events", e);
            throw e;
        }
    }
    
    private void createMusicEvents() {
        // Jazz Night
        com.whatsapp.eventservice.model.Event jazzNight = new com.whatsapp.eventservice.model.Event(
            "Jazz Night at Blue Note",
            "Experience an evening of smooth jazz with local artists. Featuring saxophone, piano, and bass performances.",
            "Blue Note Jazz Club, 123 Music Street",
            "Boston",
            LocalDateTime.now().plusDays(2),
            LocalDateTime.now().plusDays(2).plusHours(3),
            "music"
        );
        jazzNight.setSubcategory("jazz");
        jazzNight.setPriceRange("$15-25");
        jazzNight.setAgeRestriction("21+");
        jazzNight.setImageUrl("https://example.com/jazz-night.jpg");
        jazzNight.setTicketUrl("https://example.com/tickets/jazz-night");
        jazzNight.setOrganizerName("Blue Note Jazz Club");
        jazzNight.setOrganizerContact("info@bluenote.com");
        eventService.createEvent(jazzNight);
        
        // Rock Concert
        com.whatsapp.eventservice.model.Event rockConcert = new com.whatsapp.eventservice.model.Event(
            "Summer Rock Festival",
            "Annual rock festival featuring local and regional bands. Food trucks and merchandise available.",
            "Central Park Amphitheater, 456 Park Avenue",
            "Boston",
            LocalDateTime.now().plusDays(5),
            LocalDateTime.now().plusDays(5).plusHours(6),
            "music"
        );
        rockConcert.setSubcategory("rock");
        rockConcert.setPriceRange("$30-50");
        rockConcert.setAgeRestriction("all_ages");
        rockConcert.setImageUrl("https://example.com/rock-festival.jpg");
        rockConcert.setTicketUrl("https://example.com/tickets/rock-festival");
        rockConcert.setOrganizerName("Boston Music Events");
        rockConcert.setOrganizerContact("events@bostonmusic.com");
        eventService.createEvent(rockConcert);
        
        // Classical Concert
        com.whatsapp.eventservice.model.Event classicalConcert = new com.whatsapp.eventservice.model.Event(
            "Symphony Orchestra Performance",
            "Boston Symphony Orchestra presents Beethoven's 9th Symphony. Formal attire recommended.",
            "Symphony Hall, 301 Massachusetts Avenue",
            "Boston",
            LocalDateTime.now().plusDays(7),
            LocalDateTime.now().plusDays(7).plusHours(2),
            "music"
        );
        classicalConcert.setSubcategory("classical");
        classicalConcert.setPriceRange("$45-120");
        classicalConcert.setAgeRestriction("all_ages");
        classicalConcert.setImageUrl("https://example.com/symphony.jpg");
        classicalConcert.setTicketUrl("https://example.com/tickets/symphony");
        classicalConcert.setOrganizerName("Boston Symphony Orchestra");
        classicalConcert.setOrganizerContact("tickets@bso.org");
        eventService.createEvent(classicalConcert);
    }
    
    private void createSportsEvents() {
        // Basketball Game
        com.whatsapp.eventservice.model.Event basketballGame = new com.whatsapp.eventservice.model.Event(
            "Celtics vs Lakers",
            "NBA regular season game. Celtics home game at TD Garden.",
            "TD Garden, 100 Legends Way",
            "Boston",
            LocalDateTime.now().plusDays(3),
            LocalDateTime.now().plusDays(3).plusHours(2),
            "sports"
        );
        basketballGame.setSubcategory("basketball");
        basketballGame.setPriceRange("$75-300");
        basketballGame.setAgeRestriction("all_ages");
        basketballGame.setImageUrl("https://example.com/celtics-game.jpg");
        basketballGame.setTicketUrl("https://example.com/tickets/celtics");
        basketballGame.setOrganizerName("Boston Celtics");
        basketballGame.setOrganizerContact("tickets@celtics.com");
        eventService.createEvent(basketballGame);
        
        // Marathon
        com.whatsapp.eventservice.model.Event marathon = new com.whatsapp.eventservice.model.Event(
            "Boston Spring Marathon",
            "Annual 5K and 10K run through historic Boston. Registration includes t-shirt and medal.",
            "Boston Common, 139 Tremont Street",
            "Boston",
            LocalDateTime.now().plusDays(10),
            LocalDateTime.now().plusDays(10).plusHours(4),
            "sports"
        );
        marathon.setSubcategory("running");
        marathon.setPriceRange("$25-45");
        marathon.setAgeRestriction("all_ages");
        marathon.setImageUrl("https://example.com/marathon.jpg");
        marathon.setTicketUrl("https://example.com/tickets/marathon");
        marathon.setOrganizerName("Boston Running Club");
        marathon.setOrganizerContact("info@bostonrunning.com");
        eventService.createEvent(marathon);
    }
    
    private void createFamilyEvents() {
        // Children's Museum
        com.whatsapp.eventservice.model.Event childrensMuseum = new com.whatsapp.eventservice.model.Event(
            "Interactive Science Day",
            "Hands-on science experiments and demonstrations for kids of all ages. Special dinosaur exhibit.",
            "Boston Children's Museum, 308 Congress Street",
            "Boston",
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(1).plusHours(8),
            "family"
        );
        childrensMuseum.setSubcategory("education");
        childrensMuseum.setPriceRange("$12-18");
        childrensMuseum.setAgeRestriction("all_ages");
        childrensMuseum.setImageUrl("https://example.com/childrens-museum.jpg");
        childrensMuseum.setTicketUrl("https://example.com/tickets/childrens-museum");
        childrensMuseum.setOrganizerName("Boston Children's Museum");
        childrensMuseum.setOrganizerContact("info@bostonchildrensmuseum.org");
        eventService.createEvent(childrensMuseum);
        
        // Zoo Event
        com.whatsapp.eventservice.model.Event zooEvent = new com.whatsapp.eventservice.model.Event(
            "Zoo Animal Feeding Experience",
            "Special behind-the-scenes tour with animal feeding demonstrations. Perfect for families.",
            "Franklin Park Zoo, 1 Franklin Park Road",
            "Boston",
            LocalDateTime.now().plusDays(4),
            LocalDateTime.now().plusDays(4).plusHours(3),
            "family"
        );
        zooEvent.setSubcategory("animals");
        zooEvent.setPriceRange("$15-25");
        zooEvent.setAgeRestriction("all_ages");
        zooEvent.setImageUrl("https://example.com/zoo-feeding.jpg");
        zooEvent.setTicketUrl("https://example.com/tickets/zoo-feeding");
        zooEvent.setOrganizerName("Franklin Park Zoo");
        zooEvent.setOrganizerContact("info@zoonewengland.com");
        eventService.createEvent(zooEvent);
    }
    
    private void createArtEvents() {
        // Art Gallery Opening
        com.whatsapp.eventservice.model.Event artGallery = new com.whatsapp.eventservice.model.Event(
            "Contemporary Art Exhibition Opening",
            "Opening reception for new contemporary art exhibition. Wine and cheese reception included.",
            "Museum of Fine Arts, 465 Huntington Avenue",
            "Boston",
            LocalDateTime.now().plusDays(6),
            LocalDateTime.now().plusDays(6).plusHours(3),
            "art"
        );
        artGallery.setSubcategory("exhibition");
        artGallery.setPriceRange("$20-35");
        artGallery.setAgeRestriction("all_ages");
        artGallery.setImageUrl("https://example.com/art-exhibition.jpg");
        artGallery.setTicketUrl("https://example.com/tickets/art-exhibition");
        artGallery.setOrganizerName("Museum of Fine Arts");
        artGallery.setOrganizerContact("info@mfa.org");
        eventService.createEvent(artGallery);
        
        // Street Art Tour
        com.whatsapp.eventservice.model.Event streetArtTour = new com.whatsapp.eventservice.model.Event(
            "Boston Street Art Walking Tour",
            "Guided walking tour of Boston's best street art and murals. Learn about local artists.",
            "Meeting at South End, 500 Harrison Avenue",
            "Boston",
            LocalDateTime.now().plusDays(8),
            LocalDateTime.now().plusDays(8).plusHours(2),
            "art"
        );
        streetArtTour.setSubcategory("tour");
        streetArtTour.setPriceRange("$15-25");
        streetArtTour.setAgeRestriction("all_ages");
        streetArtTour.setImageUrl("https://example.com/street-art-tour.jpg");
        streetArtTour.setTicketUrl("https://example.com/tickets/street-art-tour");
        streetArtTour.setOrganizerName("Boston Art Tours");
        streetArtTour.setOrganizerContact("tours@bostonart.com");
        eventService.createEvent(streetArtTour);
    }
    
    private void createFoodEvents() {
        // Food Festival
        com.whatsapp.eventservice.model.Event foodFestival = new com.whatsapp.eventservice.model.Event(
            "Boston Food Truck Festival",
            "Annual food truck festival featuring 50+ local food trucks. Live music and family activities.",
            "Rose Kennedy Greenway, Atlantic Avenue",
            "Boston",
            LocalDateTime.now().plusDays(9),
            LocalDateTime.now().plusDays(9).plusHours(8),
            "food"
        );
        foodFestival.setSubcategory("festival");
        foodFestival.setPriceRange("$5-15 per item");
        foodFestival.setAgeRestriction("all_ages");
        foodFestival.setImageUrl("https://example.com/food-festival.jpg");
        foodFestival.setTicketUrl("https://example.com/tickets/food-festival");
        foodFestival.setOrganizerName("Boston Food Events");
        foodFestival.setOrganizerContact("info@bostonfood.com");
        eventService.createEvent(foodFestival);
        
        // Wine Tasting
        com.whatsapp.eventservice.model.Event wineTasting = new com.whatsapp.eventservice.model.Event(
            "Wine Tasting Evening",
            "Premium wine tasting featuring local and international wines. Cheese pairing included.",
            "Boston Wine Company, 789 Newbury Street",
            "Boston",
            LocalDateTime.now().plusDays(11),
            LocalDateTime.now().plusDays(11).plusHours(3),
            "food"
        );
        wineTasting.setSubcategory("wine");
        wineTasting.setPriceRange("$45-65");
        wineTasting.setAgeRestriction("21+");
        wineTasting.setImageUrl("https://example.com/wine-tasting.jpg");
        wineTasting.setTicketUrl("https://example.com/tickets/wine-tasting");
        wineTasting.setOrganizerName("Boston Wine Company");
        wineTasting.setOrganizerContact("events@bostonwine.com");
        eventService.createEvent(wineTasting);
    }
    
    private void createEducationEvents() {
        // Tech Workshop
        com.whatsapp.eventservice.model.Event techWorkshop = new com.whatsapp.eventservice.model.Event(
            "Introduction to AI Workshop",
            "Hands-on workshop introducing artificial intelligence concepts. No prior experience required.",
            "MIT Media Lab, 75 Amherst Street",
            "Boston",
            LocalDateTime.now().plusDays(12),
            LocalDateTime.now().plusDays(12).plusHours(4),
            "education"
        );
        techWorkshop.setSubcategory("technology");
        techWorkshop.setPriceRange("$50-75");
        techWorkshop.setAgeRestriction("16+");
        techWorkshop.setImageUrl("https://example.com/ai-workshop.jpg");
        techWorkshop.setTicketUrl("https://example.com/tickets/ai-workshop");
        techWorkshop.setOrganizerName("MIT Media Lab");
        techWorkshop.setOrganizerContact("workshops@media.mit.edu");
        eventService.createEvent(techWorkshop);
        
        // Language Exchange
        com.whatsapp.eventservice.model.Event languageExchange = new com.whatsapp.eventservice.model.Event(
            "Spanish-English Language Exchange",
            "Casual language exchange meetup. Practice Spanish and English with native speakers.",
            "Boston Public Library, 700 Boylston Street",
            "Boston",
            LocalDateTime.now().plusDays(13),
            LocalDateTime.now().plusDays(13).plusHours(2),
            "education"
        );
        languageExchange.setSubcategory("language");
        languageExchange.setPriceRange("free");
        languageExchange.setAgeRestriction("all_ages");
        languageExchange.setImageUrl("https://example.com/language-exchange.jpg");
        languageExchange.setTicketUrl("https://example.com/tickets/language-exchange");
        languageExchange.setOrganizerName("Boston Language Exchange");
        languageExchange.setOrganizerContact("info@bostonlanguage.com");
        eventService.createEvent(languageExchange);
    }
}
