package com.whatsapp.eventservice.repository;

import com.whatsapp.eventservice.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Event repository using JDBC Template
 */
@Repository
public class EventRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(EventRepository.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private final RowMapper<Event> eventRowMapper = (rs, rowNum) -> {
        Event event = new Event();
        event.setId(rs.getLong("id"));
        event.setName(rs.getString("name"));
        event.setDescription(rs.getString("description"));
        event.setCategoryId(rs.getObject("category_id", Long.class));
        event.setBusinessId(rs.getObject("business_id", Long.class));
        event.setCreatedBy(rs.getObject("created_by", Long.class));
        
        // Handle LocalDateTime columns
        if (rs.getTimestamp("start_time") != null) {
            event.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
        }
        if (rs.getTimestamp("end_time") != null) {
            event.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
        }
        if (rs.getTimestamp("created_at") != null) {
            event.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
            event.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }
        
        event.setLocation(rs.getString("location"));
        event.setCity(rs.getString("city"));
        event.setImageUrl(rs.getString("image_url"));
        
        return event;
    };
    
    /**
     * Save a new event
     */
    public Event save(Event event) {
        logger.info("💾 Saving event: {}", event.getName());
        
        String sql = """
            INSERT INTO events (name, description, category_id, business_id, created_by, 
                               start_time, end_time, location, city, image_url, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, event.getName());
                ps.setString(2, event.getDescription());
                ps.setObject(3, event.getCategoryId());
                ps.setObject(4, event.getBusinessId());
                ps.setObject(5, event.getCreatedBy());
                ps.setObject(6, event.getStartTime());
                ps.setObject(7, event.getEndTime());
                ps.setString(8, event.getLocation());
                ps.setString(9, event.getCity());
                ps.setString(10, event.getImageUrl());
                ps.setObject(11, event.getCreatedAt());
                ps.setObject(12, event.getUpdatedAt());
                return ps;
            }, keyHolder);
            
            Long generatedId = keyHolder.getKey().longValue();
            event.setId(generatedId);
            
            logger.info("✅ Event saved successfully with ID: {}", generatedId);
            return event;
            
        } catch (DataAccessException e) {
            logger.error("❌ Error saving event", e);
            throw new RuntimeException("Failed to save event", e);
        }
    }
    
    /**
     * Find event by ID
     */
    public Optional<Event> findById(Long id) {
        logger.info("🔍 Finding event by ID: {}", id);
        
        String sql = "SELECT * FROM events WHERE id = ?";
        
        try {
            List<Event> events = jdbcTemplate.query(sql, eventRowMapper, id);
            return events.isEmpty() ? Optional.empty() : Optional.of(events.get(0));
            
        } catch (DataAccessException e) {
            logger.error("❌ Error finding event by ID: {}", id, e);
            return Optional.empty();
        }
    }
    
    /**
     * Find all events
     */
    public List<Event> findAll() {
        logger.info("📋 Finding all events");
        
        String sql = "SELECT * FROM events ORDER BY created_at DESC";
        
        try {
            return jdbcTemplate.query(sql, eventRowMapper);
        } catch (DataAccessException e) {
            logger.error("❌ Error finding all events", e);
            return List.of();
        }
    }
    
    /**
     * Update an event
     */
    public Event update(Event event) {
        logger.info("✏️ Updating event ID: {}", event.getId());
        
        String sql = """
            UPDATE events SET name = ?, description = ?, category_id = ?, business_id = ?, 
                            created_by = ?, start_time = ?, end_time = ?, location = ?, 
                            city = ?, image_url = ?, updated_at = ?
            WHERE id = ?
            """;
        
        try {
            event.preUpdate();
            
            int rowsAffected = jdbcTemplate.update(sql,
                event.getName(),
                event.getDescription(),
                event.getCategoryId(),
                event.getBusinessId(),
                event.getCreatedBy(),
                event.getStartTime(),
                event.getEndTime(),
                event.getLocation(),
                event.getCity(),
                event.getImageUrl(),
                event.getUpdatedAt(),
                event.getId()
            );
            
            if (rowsAffected > 0) {
                logger.info("✅ Event updated successfully");
                return event;
            } else {
                logger.warn("⚠️ No event found with ID: {}", event.getId());
                return null;
            }
            
        } catch (DataAccessException e) {
            logger.error("❌ Error updating event", e);
            throw new RuntimeException("Failed to update event", e);
        }
    }
    
    /**
     * Delete an event by ID
     */
    public boolean deleteById(Long id) {
        logger.info("🗑️ Deleting event ID: {}", id);
        
        String sql = "DELETE FROM events WHERE id = ?";
        
        try {
            int rowsAffected = jdbcTemplate.update(sql, id);
            boolean deleted = rowsAffected > 0;
            
            if (deleted) {
                logger.info("✅ Event deleted successfully");
            } else {
                logger.warn("⚠️ No event found with ID: {}", id);
            }
            
            return deleted;
            
        } catch (DataAccessException e) {
            logger.error("❌ Error deleting event", e);
            throw new RuntimeException("Failed to delete event", e);
        }
    }
    
    /**
     * Count total events
     */
    public long count() {
        logger.info("🔢 Counting total events");
        
        String sql = "SELECT COUNT(*) FROM events";
        
        try {
            Long count = jdbcTemplate.queryForObject(sql, Long.class);
            return count != null ? count : 0;
        } catch (DataAccessException e) {
            logger.error("❌ Error counting events", e);
            return 0;
        }
    }
    
    /**
     * Find events by city
     */
    public List<Event> findByCity(String city) {
        logger.info("🏙️ Finding events in city: {}", city);
        
        String sql = "SELECT * FROM events WHERE city = ? ORDER BY start_time ASC";
        
        try {
            return jdbcTemplate.query(sql, eventRowMapper, city);
        } catch (DataAccessException e) {
            logger.error("❌ Error finding events by city: {}", city, e);
            return List.of();
        }
    }
    
    /**
     * Find upcoming events (start time after now)
     */
    public List<Event> findUpcomingEvents(LocalDateTime now) {
        logger.info("⏰ Finding upcoming events after: {}", now);
        
        String sql = "SELECT * FROM events WHERE start_time > ? ORDER BY start_time ASC";
        
        try {
            return jdbcTemplate.query(sql, eventRowMapper, now);
        } catch (DataAccessException e) {
            logger.error("❌ Error finding upcoming events", e);
            return List.of();
        }
    }
    
    /**
     * Find events by date range
     */
    public List<Event> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("📅 Finding events between {} and {}", startDate, endDate);
        
        String sql = "SELECT * FROM events WHERE start_time BETWEEN ? AND ? ORDER BY start_time ASC";
        
        try {
            return jdbcTemplate.query(sql, eventRowMapper, startDate, endDate);
        } catch (DataAccessException e) {
            logger.error("❌ Error finding events by date range", e);
            return List.of();
        }
    }
    
    /**
     * Search events by criteria (simplified version for EventService)
     */
    public List<Event> searchByCriteria(Map<String, Object> criteria) {
        logger.info("🔍 Searching events by criteria: {}", criteria);
        
        StringBuilder sql = new StringBuilder("SELECT * FROM events WHERE 1=1");
        
        // Build dynamic WHERE clause based on criteria
        if (criteria.containsKey("city")) {
            sql.append(" AND city = ?");
        }
        if (criteria.containsKey("category")) {
            sql.append(" AND category_id = ?");
        }
        if (criteria.containsKey("date_range") && "upcoming".equals(criteria.get("date_range"))) {
            sql.append(" AND start_time > NOW()");
        }
        
        sql.append(" ORDER BY start_time ASC LIMIT 20");
        
        try {
            // For now, return a simple search - this can be enhanced based on criteria
            return jdbcTemplate.query(sql.toString(), eventRowMapper);
        } catch (DataAccessException e) {
            logger.error("❌ Error searching events by criteria", e);
            return List.of();
        }
    }
    
    /**
     * Find events by text search in name or description
     */
    public List<Event> findByTextSearch(String searchText) {
        logger.info("🔍 Searching events by text: {}", searchText);
        
        String sql = """
            SELECT * FROM events 
            WHERE LOWER(name) LIKE LOWER(?) OR LOWER(description) LIKE LOWER(?)
            ORDER BY start_time ASC
            """;
        
        String searchPattern = "%" + searchText + "%";
        
        try {
            return jdbcTemplate.query(sql, eventRowMapper, searchPattern, searchPattern);
        } catch (DataAccessException e) {
            logger.error("❌ Error searching events by text", e);
            return List.of();
        }
    }
}