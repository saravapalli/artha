package com.whatsapp.eventservice.repository;

import com.whatsapp.eventservice.model.Offer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Offer repository using JDBC Template
 */
@Repository
@Transactional
public class OfferRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(OfferRepository.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private final RowMapper<Offer> offerRowMapper = (rs, rowNum) -> {
        Offer offer = new Offer();
        offer.setId(rs.getLong("id"));
        offer.setTitle(rs.getString("title"));
        offer.setDescription(rs.getString("description"));
        offer.setDiscountCode(rs.getString("discount_code"));
        offer.setBusinessId(rs.getObject("business_id", Long.class));
        offer.setEventId(rs.getObject("event_id", Long.class));
        
        if (rs.getDate("start_date") != null) {
            offer.setStartDate(rs.getDate("start_date").toLocalDate());
        }
        if (rs.getDate("end_date") != null) {
            offer.setEndDate(rs.getDate("end_date").toLocalDate());
        }
        
        offer.setIsActive(rs.getBoolean("is_active"));
        
        if (rs.getTimestamp("created_at") != null) {
            offer.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        
        return offer;
    };
    
    /**
     * Get all offers
     */
    public List<Offer> getAllOffers() {
        logger.info("📋 Finding all offers");
        
        String sql = "SELECT * FROM offers WHERE is_active = true ORDER BY created_at DESC";
        
        try {
            return jdbcTemplate.query(sql, offerRowMapper);
        } catch (DataAccessException e) {
            logger.error("❌ Error finding all offers", e);
            return List.of();
        }
    }
    
    /**
     * Save a new offer
     */
    public Offer save(Offer offer) {
        logger.info("💾 Saving offer: {}", offer.getTitle());
        
        String sql = """
            INSERT INTO offers (title, description, discount_code, business_id, event_id, 
                               start_date, end_date, is_active, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, offer.getTitle());
                ps.setString(2, offer.getDescription());
                ps.setString(3, offer.getDiscountCode());
                ps.setObject(4, offer.getBusinessId());
                ps.setObject(5, offer.getEventId());
                ps.setObject(6, offer.getStartDate());
                ps.setObject(7, offer.getEndDate());
                ps.setBoolean(8, offer.getIsActive());
                ps.setObject(9, offer.getCreatedAt());
                return ps;
            }, keyHolder);
            
            Long generatedId = keyHolder.getKey().longValue();
            offer.setId(generatedId);
            
            logger.info("✅ Offer saved successfully with ID: {}", generatedId);
            return offer;
            
        } catch (DataAccessException e) {
            logger.error("❌ Error saving offer", e);
            throw new RuntimeException("Failed to save offer", e);
        }
    }
}