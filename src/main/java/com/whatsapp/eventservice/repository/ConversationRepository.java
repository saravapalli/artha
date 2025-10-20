package com.whatsapp.eventservice.repository;

import com.whatsapp.eventservice.model.Conversation;
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
 * Conversation repository using JDBC Template
 */
@Repository
@Transactional
public class ConversationRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(ConversationRepository.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private final RowMapper<Conversation> conversationRowMapper = (rs, rowNum) -> {
        Conversation conversation = new Conversation();
        conversation.setId(rs.getLong("id"));
        conversation.setUserId(rs.getLong("user_id"));
        
        if (rs.getTimestamp("started_at") != null) {
            conversation.setStartedAt(rs.getTimestamp("started_at").toLocalDateTime());
        }
        
        if (rs.getTimestamp("ended_at") != null) {
            conversation.setEndedAt(rs.getTimestamp("ended_at").toLocalDateTime());
        }
        
        conversation.setContextSummary(rs.getString("context_summary"));
        
        return conversation;
    };
    
    /**
     * Save a new conversation
     */
    public Conversation save(Conversation conversation) {
        logger.info("💾 Saving conversation for user: {}", conversation.getUserId());
        
        String sql = """
            INSERT INTO conversations (user_id, started_at, ended_at, context_summary)
            VALUES (?, ?, ?, ?)
            """;
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, conversation.getUserId());
                ps.setObject(2, conversation.getStartedAt());
                ps.setObject(3, conversation.getEndedAt());
                ps.setString(4, conversation.getContextSummary());
                return ps;
            }, keyHolder);
            
            Long generatedId = keyHolder.getKey().longValue();
            conversation.setId(generatedId);
            
            logger.info("✅ Conversation saved successfully with ID: {}", generatedId);
            return conversation;
            
        } catch (DataAccessException e) {
            logger.error("❌ Error saving conversation", e);
            throw new RuntimeException("Failed to save conversation", e);
        }
    }
    
    /**
     * Find conversation by ID
     */
    public Optional<Conversation> findById(Long id) {
        logger.info("🔍 Finding conversation by ID: {}", id);
        
        String sql = "SELECT * FROM conversations WHERE id = ?";
        
        try {
            List<Conversation> conversations = jdbcTemplate.query(sql, conversationRowMapper, id);
            return conversations.isEmpty() ? Optional.empty() : Optional.of(conversations.get(0));
            
        } catch (DataAccessException e) {
            logger.error("❌ Error finding conversation by ID: {}", id, e);
            return Optional.empty();
        }
    }
    
    /**
     * Find all conversations
     */
    public List<Conversation> findAll() {
        logger.info("📋 Finding all conversations");
        
        String sql = "SELECT * FROM conversations ORDER BY started_at DESC";
        
        try {
            return jdbcTemplate.query(sql, conversationRowMapper);
        } catch (DataAccessException e) {
            logger.error("❌ Error finding all conversations", e);
            return List.of();
        }
    }
    
    /**
     * Find conversations by user ID
     */
    public List<Conversation> findByUserId(Long userId) {
        logger.info("👤 Finding conversations for user: {}", userId);
        
        String sql = "SELECT * FROM conversations WHERE user_id = ? ORDER BY started_at DESC";
        
        try {
            return jdbcTemplate.query(sql, conversationRowMapper, userId);
        } catch (DataAccessException e) {
            logger.error("❌ Error finding conversations for user: {}", userId, e);
            return List.of();
        }
    }
    
    /**
     * Find active conversation by user ID (not ended)
     */
    public Optional<Conversation> findByUserIdAndEndedAtIsNull(Long userId) {
        logger.info("🔍 Finding active conversation for user: {}", userId);
        
        String sql = "SELECT * FROM conversations WHERE user_id = ? AND ended_at IS NULL ORDER BY started_at DESC LIMIT 1";
        
        try {
            List<Conversation> conversations = jdbcTemplate.query(sql, conversationRowMapper, userId);
            return conversations.isEmpty() ? Optional.empty() : Optional.of(conversations.get(0));
            
        } catch (DataAccessException e) {
            logger.error("❌ Error finding active conversation for user: {}", userId, e);
            return Optional.empty();
        }
    }
    
    /**
     * Update a conversation
     */
    public Conversation update(Conversation conversation) {
        logger.info("✏️ Updating conversation ID: {}", conversation.getId());
        
        String sql = """
            UPDATE conversations SET user_id = ?, started_at = ?, ended_at = ?, context_summary = ?
            WHERE id = ?
            """;
        
        try {
            int rowsAffected = jdbcTemplate.update(sql,
                conversation.getUserId(),
                conversation.getStartedAt(),
                conversation.getEndedAt(),
                conversation.getContextSummary(),
                conversation.getId()
            );
            
            if (rowsAffected > 0) {
                logger.info("✅ Conversation updated successfully");
                return conversation;
            } else {
                logger.warn("⚠️ No conversation found with ID: {}", conversation.getId());
                return null;
            }
            
        } catch (DataAccessException e) {
            logger.error("❌ Error updating conversation", e);
            throw new RuntimeException("Failed to update conversation", e);
        }
    }
    
    /**
     * Delete conversation by ID
     */
    public boolean deleteById(Long id) {
        logger.info("🗑️ Deleting conversation ID: {}", id);
        
        String sql = "DELETE FROM conversations WHERE id = ?";
        
        try {
            int rowsAffected = jdbcTemplate.update(sql, id);
            boolean deleted = rowsAffected > 0;
            
            if (deleted) {
                logger.info("✅ Conversation deleted successfully");
            } else {
                logger.warn("⚠️ No conversation found with ID: {}", id);
            }
            
            return deleted;
            
        } catch (DataAccessException e) {
            logger.error("❌ Error deleting conversation", e);
            throw new RuntimeException("Failed to delete conversation", e);
        }
    }
    
    /**
     * Count conversations by user ID
     */
    public long countByUserId(Long userId) {
        logger.info("🔢 Counting conversations for user: {}", userId);
        
        String sql = "SELECT COUNT(*) FROM conversations WHERE user_id = ?";
        
        try {
            Long count = jdbcTemplate.queryForObject(sql, Long.class, userId);
            return count != null ? count : 0;
        } catch (DataAccessException e) {
            logger.error("❌ Error counting conversations", e);
            return 0;
        }
    }
}