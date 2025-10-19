package com.whatsapp.eventservice.controller;

import com.whatsapp.eventservice.model.Event;
import com.whatsapp.eventservice.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for event management
 * 
 * This controller provides endpoints for:
 * - Searching events
 * - Managing events (CRUD operations)
 * - Event analytics
 */
@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {
    
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);
    
    @Autowired
    private EventService eventService;
    
    /**
     * Search events based on criteria
     * 
     * @param city City name
     * @param category Event category
     * @param subcategory Event subcategory
     * @param dateRange Date range (today, weekend, this_week, etc.)
     * @param priceRange Price range (free, low, medium, high)
     * @param ageRestriction Age restriction (all_ages, teens_and_up, adults_only)
     * @param limit Maximum number of events to return
     * @return List of matching events
     */
    @GetMapping("/search")
    public ResponseEntity<List<Event>> searchEvents(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String subcategory,
            @RequestParam(required = false) String dateRange,
            @RequestParam(required = false) String priceRange,
            @RequestParam(required = false) String ageRestriction,
            @RequestParam(defaultValue = "20") int limit) {
        
        logger.info("🔍 Event search request - City: {}, Category: {}, DateRange: {}", 
                   city, category, dateRange);
        
        try {
            List<Event> events = eventService.searchEvents(
                city, category, subcategory, dateRange, priceRange, ageRestriction, limit);
            
            logger.info("✅ Found {} events matching criteria", events.size());
            return ResponseEntity.ok(events);
            
        } catch (Exception e) {
            logger.error("❌ Error searching events", e);
            return ResponseEntity.status(500).body(null);
        }
    }
    
    /**
     * Get events by category
     * 
     * @param category Event category
     * @param city City name (optional)
     * @param limit Maximum number of events to return
     * @return List of events in the category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Event>> getEventsByCategory(
            @PathVariable String category,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "10") int limit) {
        
        logger.info("📂 Getting events by category: {} in city: {}", category, city);
        
        try {
            List<Event> events = eventService.getEventsByCategory(category, city, limit);
            
            logger.info("✅ Found {} events in category {}", events.size(), category);
            return ResponseEntity.ok(events);
            
        } catch (Exception e) {
            logger.error("❌ Error getting events by category", e);
            return ResponseEntity.status(500).body(null);
        }
    }
    
    /**
     * Get upcoming events
     * 
     * @param city City name (optional)
     * @param limit Maximum number of events to return
     * @return List of upcoming events
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<Event>> getUpcomingEvents(
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "10") int limit) {
        
        logger.info("📅 Getting upcoming events in city: {}", city);
        
        try {
            List<Event> events = eventService.getUpcomingEvents(city, limit);
            
            logger.info("✅ Found {} upcoming events", events.size());
            return ResponseEntity.ok(events);
            
        } catch (Exception e) {
            logger.error("❌ Error getting upcoming events", e);
            return ResponseEntity.status(500).body(null);
        }
    }
    
    /**
     * Get event by ID
     * 
     * @param id Event ID
     * @return Event details
     */
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        logger.info("🔍 Getting event by ID: {}", id);
        
        try {
            Event event = eventService.getEventById(id);
            
            if (event != null) {
                logger.info("✅ Found event: {}", event.getTitle());
                return ResponseEntity.ok(event);
            } else {
                logger.warn("⚠️ Event not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            logger.error("❌ Error getting event by ID", e);
            return ResponseEntity.status(500).body(null);
        }
    }
    
    /**
     * Create a new event
     * 
     * @param event Event data
     * @return Created event
     */
    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        logger.info("➕ Creating new event: {}", event.getTitle());
        
        try {
            Event createdEvent = eventService.createEvent(event);
            
            logger.info("✅ Event created successfully with ID: {}", createdEvent.getId());
            return ResponseEntity.ok(createdEvent);
            
        } catch (Exception e) {
            logger.error("❌ Error creating event", e);
            return ResponseEntity.status(500).body(null);
        }
    }
    
    /**
     * Update an existing event
     * 
     * @param id Event ID
     * @param event Updated event data
     * @return Updated event
     */
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Event event) {
        logger.info("✏️ Updating event ID: {}", id);
        
        try {
            Event updatedEvent = eventService.updateEvent(id, event);
            
            if (updatedEvent != null) {
                logger.info("✅ Event updated successfully");
                return ResponseEntity.ok(updatedEvent);
            } else {
                logger.warn("⚠️ Event not found for update with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            logger.error("❌ Error updating event", e);
            return ResponseEntity.status(500).body(null);
        }
    }
    
    /**
     * Delete an event
     * 
     * @param id Event ID
     * @return Success response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteEvent(@PathVariable Long id) {
        logger.info("🗑️ Deleting event ID: {}", id);
        
        try {
            boolean deleted = eventService.deleteEvent(id);
            
            if (deleted) {
                logger.info("✅ Event deleted successfully");
                return ResponseEntity.ok(Map.of("message", "Event deleted successfully"));
            } else {
                logger.warn("⚠️ Event not found for deletion with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            logger.error("❌ Error deleting event", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get event statistics
     * 
     * @return Event statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getEventStats() {
        logger.info("📊 Getting event statistics");
        
        try {
            Map<String, Object> stats = eventService.getEventStats();
            
            logger.info("✅ Event statistics retrieved");
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            logger.error("❌ Error getting event statistics", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get available event categories
     * 
     * @return List of available categories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getEventCategories() {
        logger.info("📂 Getting available event categories");
        
        try {
            List<String> categories = eventService.getEventCategories();
            
            logger.info("✅ Found {} event categories", categories.size());
            return ResponseEntity.ok(categories);
            
        } catch (Exception e) {
            logger.error("❌ Error getting event categories", e);
            return ResponseEntity.status(500).body(null);
        }
    }
}
