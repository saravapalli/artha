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
 * REST Controller for event management matching OpenAPI specification
 */
@RestController
@RequestMapping("/events")
@CrossOrigin(origins = "*")
public class EventController {
    
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);
    
    @Autowired
    private EventService eventService;
    
    /**
     * List all events - matches OpenAPI spec
     */
    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        logger.info("📅 Getting all events");
        
        try {
            List<Event> events = eventService.getAllEvents();
            logger.info("✅ Retrieved {} events", events.size());
            return ResponseEntity.ok(events);
            
        } catch (Exception e) {
            logger.error("❌ Error getting all events", e);
            return ResponseEntity.status(500).body(null);
        }
    }
    
    
    /**
     * Create a new event - matches OpenAPI spec
     */
    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        logger.info("➕ Creating new event: {}", event.getName());
        
        try {
            Event createdEvent = eventService.createEvent(event);
            
            logger.info("✅ Event created successfully with ID: {}", createdEvent.getId());
            return ResponseEntity.ok(createdEvent);
            
        } catch (Exception e) {
            logger.error("❌ Error creating event", e);
            return ResponseEntity.status(500).body(null);
        }
    }
}
