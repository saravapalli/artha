package com.whatsapp.eventservice.repository;

import com.whatsapp.eventservice.model.Business;
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
import java.time.LocalDateTime;
import java.util.List;

/**
 * Business repository using JDBC Template
 */
@Repository
@Transactional
public class BusinessRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(BusinessRepository.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private final RowMapper<Business> businessRowMapper = (rs, rowNum) -> {
        Business business = new Business();
        business.setId(rs.getLong("id"));
        business.setName(rs.getString("name"));
        business.setDescription(rs.getString("description"));
        business.setPhoneNumber(rs.getString("phone_number"));
        business.setEmail(rs.getString("email"));
        business.setWebsiteUrl(rs.getString("website_url"));
        business.setAddress(rs.getString("address"));
        business.setCity(rs.getString("city"));
        business.setLatitude(rs.getObject("latitude", Double.class));
        business.setLongitude(rs.getObject("longitude", Double.class));
        business.setCategoryId(rs.getObject("category_id", Long.class));
        business.setImageUrl(rs.getString("image_url"));
        
        if (rs.getTimestamp("created_at") != null) {
            business.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
            business.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }
        
        return business;
    };
    
    /**
     * Get all businesses
     */
    public List<Business> getAllBusinesses() {
        logger.info("📋 Finding all businesses");
        
        String sql = "SELECT * FROM businesses ORDER BY created_at DESC";
        
        try {
            return jdbcTemplate.query(sql, businessRowMapper);
        } catch (DataAccessException e) {
            logger.error("❌ Error finding all businesses", e);
            return List.of();
        }
    }
    
    /**
     * Save a new business
     */
    public Business save(Business business) {
        logger.info("💾 Saving business: {}", business.getName());
        
        String sql = """
            INSERT INTO businesses (name, description, phone_number, email, website_url, 
                                   address, city, latitude, longitude, category_id, image_url, 
                                   created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, business.getName());
                ps.setString(2, business.getDescription());
                ps.setString(3, business.getPhoneNumber());
                ps.setString(4, business.getEmail());
                ps.setString(5, business.getWebsiteUrl());
                ps.setString(6, business.getAddress());
                ps.setString(7, business.getCity());
                ps.setObject(8, business.getLatitude());
                ps.setObject(9, business.getLongitude());
                ps.setObject(10, business.getCategoryId());
                ps.setString(11, business.getImageUrl());
                ps.setObject(12, business.getCreatedAt());
                ps.setObject(13, business.getUpdatedAt());
                return ps;
            }, keyHolder);
            
            Long generatedId = keyHolder.getKey().longValue();
            business.setId(generatedId);
            
            logger.info("✅ Business saved successfully with ID: {}", generatedId);
            return business;
            
        } catch (DataAccessException e) {
            logger.error("❌ Error saving business", e);
            throw new RuntimeException("Failed to save business", e);
        }
    }
}