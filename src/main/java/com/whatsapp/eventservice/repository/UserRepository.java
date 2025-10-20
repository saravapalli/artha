package com.whatsapp.eventservice.repository;

import com.whatsapp.eventservice.model.User;
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
import java.util.Optional;

/**
 * User repository using JDBC Template
 */
@Repository
@Transactional
public class UserRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setEmail(rs.getString("email"));
        user.setProfilePicUrl(rs.getString("profile_pic_url"));
        user.setBio(rs.getString("bio"));
        
        if (rs.getTimestamp("created_at") != null) {
            user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        
        return user;
    };
    
    /**
     * Save a new user
     */
    public User save(User user) {
        logger.info("💾 Saving user: {}", user.getPhoneNumber());
        
        String sql = """
            INSERT INTO users (name, phone_number, email, profile_pic_url, bio, created_at)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getName());
                ps.setString(2, user.getPhoneNumber());
                ps.setString(3, user.getEmail());
                ps.setString(4, user.getProfilePicUrl());
                ps.setString(5, user.getBio());
                ps.setObject(6, user.getCreatedAt());
                return ps;
            }, keyHolder);
            
            Long generatedId = keyHolder.getKey().longValue();
            user.setId(generatedId);
            
            logger.info("✅ User saved successfully with ID: {}", generatedId);
            return user;
            
        } catch (DataAccessException e) {
            logger.error("❌ Error saving user", e);
            throw new RuntimeException("Failed to save user", e);
        }
    }
    
    /**
     * Find user by ID
     */
    public Optional<User> findById(Long id) {
        logger.info("🔍 Finding user by ID: {}", id);
        
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try {
            List<User> users = jdbcTemplate.query(sql, userRowMapper, id);
            return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
            
        } catch (DataAccessException e) {
            logger.error("❌ Error finding user by ID: {}", id, e);
            return Optional.empty();
        }
    }
    
    /**
     * Find user by phone number
     */
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        logger.info("📞 Finding user by phone number: {}", phoneNumber);
        
        String sql = "SELECT * FROM users WHERE phone_number = ?";
        
        try {
            List<User> users = jdbcTemplate.query(sql, userRowMapper, phoneNumber);
            return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
            
        } catch (DataAccessException e) {
            logger.error("❌ Error finding user by phone number: {}", phoneNumber, e);
            return Optional.empty();
        }
    }
    
    /**
     * Find all users
     */
    public List<User> findAll() {
        logger.info("📋 Finding all users");
        
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        
        try {
            return jdbcTemplate.query(sql, userRowMapper);
        } catch (DataAccessException e) {
            logger.error("❌ Error finding all users", e);
            return List.of();
        }
    }
    
    /**
     * Update a user
     */
    public User update(User user) {
        logger.info("✏️ Updating user ID: {}", user.getId());
        
        String sql = """
            UPDATE users SET name = ?, phone_number = ?, email = ?, 
                           profile_pic_url = ?, bio = ?
            WHERE id = ?
            """;
        
        try {
            int rowsAffected = jdbcTemplate.update(sql,
                user.getName(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getProfilePicUrl(),
                user.getBio(),
                user.getId()
            );
            
            if (rowsAffected > 0) {
                logger.info("✅ User updated successfully");
                return user;
            } else {
                logger.warn("⚠️ No user found with ID: {}", user.getId());
                return null;
            }
            
        } catch (DataAccessException e) {
            logger.error("❌ Error updating user", e);
            throw new RuntimeException("Failed to update user", e);
        }
    }
    
    /**
     * Delete user by ID
     */
    public boolean deleteById(Long id) {
        logger.info("🗑️ Deleting user ID: {}", id);
        
        String sql = "DELETE FROM users WHERE id = ?";
        
        try {
            int rowsAffected = jdbcTemplate.update(sql, id);
            boolean deleted = rowsAffected > 0;
            
            if (deleted) {
                logger.info("✅ User deleted successfully");
            } else {
                logger.warn("⚠️ No user found with ID: {}", id);
            }
            
            return deleted;
            
        } catch (DataAccessException e) {
            logger.error("❌ Error deleting user", e);
            throw new RuntimeException("Failed to delete user", e);
        }
    }
    
    /**
     * Count total users
     */
    public long count() {
        logger.info("🔢 Counting total users");
        
        String sql = "SELECT COUNT(*) FROM users";
        
        try {
            Long count = jdbcTemplate.queryForObject(sql, Long.class);
            return count != null ? count : 0;
        } catch (DataAccessException e) {
            logger.error("❌ Error counting users", e);
            return 0;
        }
    }
    
    /**
     * Find users created after a specific date
     */
    public List<User> findUsersCreatedAfter(LocalDateTime date) {
        logger.info("📅 Finding users created after: {}", date);
        
        String sql = "SELECT * FROM users WHERE created_at > ? ORDER BY created_at DESC";
        
        try {
            return jdbcTemplate.query(sql, userRowMapper, date);
        } catch (DataAccessException e) {
            logger.error("❌ Error finding users created after date", e);
            return List.of();
        }
    }
    
    /**
     * Check if user exists by phone number
     */
    public boolean existsByPhoneNumber(String phoneNumber) {
        logger.info("🔍 Checking if user exists with phone number: {}", phoneNumber);
        
        String sql = "SELECT COUNT(*) FROM users WHERE phone_number = ?";
        
        try {
            Long count = jdbcTemplate.queryForObject(sql, Long.class, phoneNumber);
            return count != null && count > 0;
        } catch (DataAccessException e) {
            logger.error("❌ Error checking user existence", e);
            return false;
        }
    }
}