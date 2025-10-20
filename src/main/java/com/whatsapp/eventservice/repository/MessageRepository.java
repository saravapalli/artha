package com.whatsapp.eventservice.repository;

import com.whatsapp.eventservice.model.Message;
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
 * Message repository using JDBC Template
 */
@Repository
@Transactional
public class MessageRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageRepository.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private final RowMapper<Message> messageRowMapper = (rs, rowNum) -> {
        Message message = new Message();
        message.setId(rs.getLong("id"));
        message.setConversationId(rs.getLong("conversation_id"));
        
        // Handle enum conversion
        String senderStr = rs.getString("sender");
        if (senderStr != null) {
            message.setSender(Message.Sender.valueOf(senderStr.toLowerCase()));
        }
        
        String messageTypeStr = rs.getString("message_type");
        if (messageTypeStr != null) {
            message.setMessageType(Message.MessageType.valueOf(messageTypeStr.toLowerCase()));
        }
        
        message.setContent(rs.getString("content"));
        
        if (rs.getTimestamp("created_at") != null) {
            message.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        
        return message;
    };
    
    /**
     * Save a new message
     */
    public Message save(Message message) {
        logger.info("💾 Saving message for conversation: {}", message.getConversationId());
        
        String sql = """
            INSERT INTO messages (conversation_id, sender, content, message_type, created_at)
            VALUES (?, ?, ?, ?, ?)
            """;
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, message.getConversationId());
                ps.setString(2, message.getSender() != null ? message.getSender().toString() : null);
                ps.setString(3, message.getContent());
                ps.setString(4, message.getMessageType() != null ? message.getMessageType().toString() : null);
                ps.setObject(5, message.getCreatedAt());
                return ps;
            }, keyHolder);
            
            Long generatedId = keyHolder.getKey().longValue();
            message.setId(generatedId);
            
            logger.info("✅ Message saved successfully with ID: {}", generatedId);
            return message;
            
        } catch (DataAccessException e) {
            logger.error("❌ Error saving message", e);
            throw new RuntimeException("Failed to save message", e);
        }
    }
    
    /**
     * Find message by ID
     */
    public Message findById(Long id) {
        logger.info("🔍 Finding message by ID: {}", id);
        
        String sql = "SELECT * FROM messages WHERE id = ?";
        
        try {
            List<Message> messages = jdbcTemplate.query(sql, messageRowMapper, id);
            return messages.isEmpty() ? null : messages.get(0);
            
        } catch (DataAccessException e) {
            logger.error("❌ Error finding message by ID: {}", id, e);
            return null;
        }
    }
    
    /**
     * Find all messages
     */
    public List<Message> findAll() {
        logger.info("📋 Finding all messages");
        
        String sql = "SELECT * FROM messages ORDER BY created_at DESC";
        
        try {
            return jdbcTemplate.query(sql, messageRowMapper);
        } catch (DataAccessException e) {
            logger.error("❌ Error finding all messages", e);
            return List.of();
        }
    }
    
    /**
     * Find messages by conversation ID ordered by creation time ascending
     */
    public List<Message> findByConversationIdOrderByCreatedAtAsc(Long conversationId) {
        logger.info("💬 Finding messages for conversation: {}", conversationId);
        
        String sql = "SELECT * FROM messages WHERE conversation_id = ? ORDER BY created_at ASC";
        
        try {
            return jdbcTemplate.query(sql, messageRowMapper, conversationId);
        } catch (DataAccessException e) {
            logger.error("❌ Error finding messages for conversation: {}", conversationId, e);
            return List.of();
        }
    }
    
    /**
     * Find user messages by conversation ID
     */
    public List<Message> findUserMessagesByConversationId(Long conversationId) {
        logger.info("👤 Finding user messages for conversation: {}", conversationId);
        
        String sql = "SELECT * FROM messages WHERE conversation_id = ? AND sender = 'user' ORDER BY created_at DESC";
        
        try {
            return jdbcTemplate.query(sql, messageRowMapper, conversationId);
        } catch (DataAccessException e) {
            logger.error("❌ Error finding user messages for conversation: {}", conversationId, e);
            return List.of();
        }
    }
    
    /**
     * Find messages by conversation ID since a specific date
     */
    public List<Message> findSinceByConversationId(Long conversationId, LocalDateTime since) {
        logger.info("📅 Finding messages for conversation {} since: {}", conversationId, since);
        
        String sql = "SELECT * FROM messages WHERE conversation_id = ? AND created_at >= ? ORDER BY created_at ASC";
        
        try {
            return jdbcTemplate.query(sql, messageRowMapper, conversationId, since);
        } catch (DataAccessException e) {
            logger.error("❌ Error finding messages since date", e);
            return List.of();
        }
    }
    
    /**
     * Count messages by conversation ID
     */
    public long countByConversationId(Long conversationId) {
        logger.info("🔢 Counting messages for conversation: {}", conversationId);
        
        String sql = "SELECT COUNT(*) FROM messages WHERE conversation_id = ?";
        
        try {
            Long count = jdbcTemplate.queryForObject(sql, Long.class, conversationId);
            return count != null ? count : 0;
        } catch (DataAccessException e) {
            logger.error("❌ Error counting messages", e);
            return 0;
        }
    }
    
    /**
     * Delete message by ID
     */
    public boolean deleteById(Long id) {
        logger.info("🗑️ Deleting message ID: {}", id);
        
        String sql = "DELETE FROM messages WHERE id = ?";
        
        try {
            int rowsAffected = jdbcTemplate.update(sql, id);
            boolean deleted = rowsAffected > 0;
            
            if (deleted) {
                logger.info("✅ Message deleted successfully");
            } else {
                logger.warn("⚠️ No message found with ID: {}", id);
            }
            
            return deleted;
            
        } catch (DataAccessException e) {
            logger.error("❌ Error deleting message", e);
            throw new RuntimeException("Failed to delete message", e);
        }
    }
}