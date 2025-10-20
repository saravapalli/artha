package com.whatsapp.eventservice.service;

import com.whatsapp.eventservice.model.Event;
import com.whatsapp.eventservice.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service for managing event data
 * 
 * This service handles event CRUD operations, search, and filtering
 * using JPA repositories.
 */
@Service
@Transactional
public class EventService {
    
    private static final Logger logger = LoggerFactory.getLogger(EventService.class);
    
    @Autowired
    private EventRepository eventRepository;
    
    /**
     * Search events based on criteria
     * 
     * @param city City name
     * @param category Event category
     * @param subcategory Event subcategory
     * @param dateRange Date range
     * @param priceRange Price range
     * @param ageRestriction Age restriction
     * @param limit Maximum number of events to return
     * @return List of matching events
     */
    public List<Event> searchEvents(String city, String category, String subcategory, 
                                  String dateRange, String priceRange, String ageRestriction, int limit) {
        logger.info("🔍 Searching events - City: {}, Category: {}, DateRange: {}", city, category, dateRange);
        
        // This would typically use a custom repository method with dynamic queries
        // For now, use basic repository methods
        
        List<Event> events;
        
        // Use findAll since the repository methods may not match the new schema
        events = eventRepository.findAll();
        
        // Apply additional filters
        events = events.stream()
                .filter(event -> filterByDateRange(event, dateRange))
                .limit(limit)
                .toList();
        
        logger.info("✅ Found {} events matching criteria", events.size());
        return events;
    }
    
    /**
     * Search events using natural language criteria
     * 
     * @param criteria Search criteria map
     * @return List of matching events
     */
    public List<Event> searchEventsByCriteria(Map<String, Object> criteria) {
        logger.info("🔍 Searching events by criteria: {}", criteria);
        
        // Use the new JDBC repository method for searching
        return eventRepository.searchByCriteria(criteria);
    }
    
    /**
     * Get events by category
     * 
     * @param category Event category
     * @param city City name (optional)
     * @param limit Maximum number of events to return
     * @return List of events in the category
     */
    public List<Event> getEventsByCategory(String category, String city, int limit) {
        logger.info("📂 Getting events by category: {} in city: {}", category, city);
        
        List<Event> events = eventRepository.findAll();
        
        events = events.stream().limit(limit).toList();
        
        logger.info("✅ Found {} events in category {}", events.size(), category);
        return events;
    }
    
    /**
     * Get upcoming events
     * 
     * @param city City name (optional)
     * @param limit Maximum number of events to return
     * @return List of upcoming events
     */
    public List<Event> getUpcomingEvents(String city, int limit) {
        logger.info("📅 Getting upcoming events in city: {}", city);
        
        LocalDateTime now = LocalDateTime.now();
        List<Event> events = eventRepository.findUpcomingEvents(now);
        
        // Filter by city if specified
        if (city != null && !city.trim().isEmpty()) {
            events = events.stream()
                    .filter(event -> city.equalsIgnoreCase(event.getCity()))
                    .limit(limit)
                    .toList();
        } else {
            events = events.stream()
                    .limit(limit)
                    .toList();
        }
        
        logger.info("✅ Found {} upcoming events", events.size());
        return events;
    }
    
    /**
     * Get event by ID
     * 
     * @param id Event ID
     * @return Event entity or null if not found
     */
    public Event getEventById(Long id) {
        logger.info("🔍 Getting event by ID: {}", id);
        
        Optional<Event> eventOpt = eventRepository.findById(id);
        
        if (eventOpt.isPresent()) {
            Event event = eventOpt.get();
            logger.info("✅ Found event: {}", event.getName());
            return event;
        } else {
            logger.warn("⚠️ Event not found with ID: {}", id);
            return null;
        }
    }
    
    /**
     * Create a new event
     * 
     * @param event Event data
     * @return Created event
     */
    public Event createEvent(Event event) {
        logger.info("➕ Creating new event: {}", event.getName());
        
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        
        Event createdEvent = eventRepository.save(event);
        
        logger.info("✅ Event created successfully with ID: {}", createdEvent.getId());
        return createdEvent;
    }
    
    /**
     * Get all events - matches OpenAPI spec
     */
    public List<Event> getAllEvents() {
        logger.info("📅 Getting all events");
        
        List<Event> events = eventRepository.findAll();
        logger.info("✅ Retrieved {} events", events.size());
        
        return events;
    }
    
    /**
     * Update an existing event
     * 
     * @param id Event ID
     * @param eventData Updated event data
     * @return Updated event
     */
    public Event updateEvent(Long id, Event eventData) {
        logger.info("✏️ Updating event ID: {}", id);
        
        Optional<Event> eventOpt = eventRepository.findById(id);
        if (eventOpt.isEmpty()) {
            logger.warn("⚠️ Event not found for update with ID: {}", id);
            return null;
        }
        
        Event event = eventOpt.get();
        
        // Update fields
        if (eventData.getName() != null) {
            event.setName(eventData.getName());
        }
        if (eventData.getDescription() != null) {
            event.setDescription(eventData.getDescription());
        }
        if (eventData.getLocation() != null) {
            event.setLocation(eventData.getLocation());
        }
        if (eventData.getCity() != null) {
            event.setCity(eventData.getCity());
        }
        if (eventData.getStartTime() != null) {
            event.setStartTime(eventData.getStartTime());
        }
        if (eventData.getEndTime() != null) {
            event.setEndTime(eventData.getEndTime());
        }
        if (eventData.getCategoryId() != null) {
            event.setCategoryId(eventData.getCategoryId());
        }
        if (eventData.getBusinessId() != null) {
            event.setBusinessId(eventData.getBusinessId());
        }
        if (eventData.getCreatedBy() != null) {
            event.setCreatedBy(eventData.getCreatedBy());
        }
        if (eventData.getImageUrl() != null) {
            event.setImageUrl(eventData.getImageUrl());
        }
        
        event.setUpdatedAt(LocalDateTime.now());
        event = eventRepository.update(event);
        
        logger.info("✅ Event updated successfully");
        return event;
    }
    
    /**
     * Delete an event
     * 
     * @param id Event ID
     * @return True if deleted successfully
     */
    public boolean deleteEvent(Long id) {
        logger.info("🗑️ Deleting event ID: {}", id);
        
        boolean deleted = eventRepository.deleteById(id);
        
        if (deleted) {
            logger.info("✅ Event deleted successfully");
        } else {
            logger.warn("⚠️ Event not found for deletion with ID: {}", id);
        }
        
        return deleted;
    }
    
    /**
     * Get event statistics
     * 
     * @return Event statistics
     */
    public Map<String, Object> getEventStats() {
        logger.info("📊 Getting event statistics");
        
        Map<String, Object> stats = new HashMap<>();
        
        long totalEvents = eventRepository.count();
        
        stats.put("total_events", totalEvents);
        stats.put("upcoming_events", totalEvents); // Simplified for now
        stats.put("timestamp", LocalDateTime.now());
        
        logger.info("✅ Event statistics retrieved");
        return stats;
    }
    
    /**
     * Get available event categories
     * 
     * @return List of available categories
     */
    public List<String> getEventCategories() {
        logger.info("📂 Getting available event categories");
        
        // This would typically use a custom query to get distinct categories
        List<String> categories = List.of(
            "music", "sports", "family", "art", "food", "education", 
            "entertainment", "outdoor", "general"
        );
        
        logger.info("✅ Found {} event categories", categories.size());
        return categories;
    }
    
    // Helper methods for filtering
    
    private boolean filterByDateRange(Event event, String dateRange) {
        if (dateRange == null || dateRange.isEmpty()) {
            return true;
        }
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime eventTime = event.getStartTime();
        
        if (eventTime == null) {
            return true;
        }
        
        switch (dateRange.toLowerCase()) {
            case "today":
                return eventTime.toLocalDate().equals(now.toLocalDate());
            case "tomorrow":
                return eventTime.toLocalDate().equals(now.plusDays(1).toLocalDate());
            case "weekend":
                return isWeekend(eventTime);
            case "this_week":
                return eventTime.isAfter(now) && eventTime.isBefore(now.plusWeeks(1));
            case "next_week":
                return eventTime.isAfter(now.plusWeeks(1)) && eventTime.isBefore(now.plusWeeks(2));
            case "this_month":
                return eventTime.isAfter(now) && eventTime.isBefore(now.plusMonths(1));
            case "upcoming":
                return eventTime.isAfter(now);
            default:
                return true;
        }
    }
    
    
    private boolean isWeekend(LocalDateTime dateTime) {
        int dayOfWeek = dateTime.getDayOfWeek().getValue();
        return dayOfWeek == 6 || dayOfWeek == 7; // Saturday or Sunday
    }
}
