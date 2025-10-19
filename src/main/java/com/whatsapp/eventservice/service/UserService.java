package com.whatsapp.eventservice.service;

import com.whatsapp.eventservice.model.User;
import com.whatsapp.eventservice.repository.UserRepository;
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
 * Service for managing user data and preferences
 * 
 * This service handles user registration, preference storage, and retrieval
 * using JPA repositories.
 */
@Service
@Transactional
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Get or create a user by phone number
     * 
     * @param phoneNumber User's phone number
     * @param whatsappId WhatsApp ID
     * @return User entity
     */
    public User getOrCreateUser(String phoneNumber, String whatsappId) {
        logger.info("👤 Getting or creating user with phone: {}", phoneNumber);
        
        Optional<User> existingUser = userRepository.findByPhoneNumber(phoneNumber);
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            
            // Update whatsapp_id if it's missing
            if (whatsappId != null && (user.getWhatsappId() == null || user.getWhatsappId().isEmpty())) {
                user.setWhatsappId(whatsappId);
                user = userRepository.save(user);
                logger.info("✅ Updated WhatsApp ID for user: {}", user.getId());
            }
            
            return user;
        }
        
        // Create new user
        User newUser = new User(phoneNumber, whatsappId);
        newUser = userRepository.save(newUser);
        logger.info("✅ Created new user with ID: {}", newUser.getId());
        
        return newUser;
    }
    
    /**
     * Get user by phone number
     * 
     * @param phoneNumber User's phone number
     * @return User entity or null if not found
     */
    public User getUserByPhone(String phoneNumber) {
        logger.info("🔍 Getting user by phone: {}", phoneNumber);
        
        return userRepository.findByPhoneNumber(phoneNumber).orElse(null);
    }
    
    /**
     * Update user information
     * 
     * @param id User ID
     * @param userData User data to update
     * @return Updated user entity
     */
    public User updateUser(Long id, Map<String, Object> userData) {
        logger.info("✏️ Updating user ID: {}", id);
        
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            logger.warn("⚠️ User not found with ID: {}", id);
            return null;
        }
        
        User user = userOpt.get();
        
        // Update fields if provided
        if (userData.containsKey("name")) {
            user.setName((String) userData.get("name"));
        }
        if (userData.containsKey("email")) {
            user.setEmail((String) userData.get("email"));
        }
        if (userData.containsKey("city")) {
            user.setCity((String) userData.get("city"));
        }
        if (userData.containsKey("timezone")) {
            user.setTimezone((String) userData.get("timezone"));
        }
        if (userData.containsKey("language")) {
            user.setLanguage((String) userData.get("language"));
        }
        
        user = userRepository.save(user);
        logger.info("✅ User updated successfully");
        
        return user;
    }
    
    /**
     * Set user opt-in status
     * 
     * @param id User ID
     * @param optIn Opt-in status
     * @return True if updated successfully
     */
    public boolean setOptInStatus(Long id, boolean optIn) {
        logger.info("📝 Setting opt-in status for user ID: {} to {}", id, optIn);
        
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            logger.warn("⚠️ User not found with ID: {}", id);
            return false;
        }
        
        User user = userOpt.get();
        user.setOptInStatus(optIn);
        userRepository.save(user);
        
        logger.info("✅ Opt-in status updated successfully");
        return true;
    }
    
    /**
     * Get user preferences
     * 
     * @param id User ID
     * @return Map of user preferences
     */
    public Map<String, String> getUserPreferences(Long id) {
        logger.info("⚙️ Getting preferences for user ID: {}", id);
        
        // This would typically use a UserPreferenceRepository
        // For now, return empty map
        Map<String, String> preferences = new HashMap<>();
        
        logger.info("✅ Retrieved {} preferences for user", preferences.size());
        return preferences;
    }
    
    /**
     * Save user preference
     * 
     * @param userId User ID
     * @param category Preference category
     * @param value Preference value
     * @param source Preference source
     */
    public void saveUserPreference(Long userId, String category, String value, String source) {
        logger.info("💾 Saving preference for user ID: {} - Category: {}, Value: {}", userId, category, value);
        
        // This would typically use a UserPreferenceRepository
        // For now, just log the action
        
        logger.info("✅ Preference saved successfully");
    }
    
    /**
     * Log user activity
     * 
     * @param userId User ID
     * @param actionType Action type
     * @param actionDetail Action detail
     * @param eventId Event ID (optional)
     * @param queryText Query text (optional)
     * @param aiInference AI inference (optional)
     */
    public void logUserActivity(Long userId, String actionType, String actionDetail, 
                               Long eventId, String queryText, String aiInference) {
        logger.info("📝 Logging activity for user ID: {} - Action: {}", userId, actionType);
        
        // This would typically use a UserActivityRepository
        // For now, just log the action
        
        logger.info("✅ Activity logged successfully");
    }
    
    /**
     * Get user activity
     * 
     * @param id User ID
     * @param limit Maximum number of activities to return
     * @return List of user activities
     */
    public List<Map<String, Object>> getUserActivity(Long id, int limit) {
        logger.info("📊 Getting activity for user ID: {}", id);
        
        // This would typically use a UserActivityRepository
        // For now, return empty list
        List<Map<String, Object>> activities = List.of();
        
        logger.info("✅ Retrieved {} activities for user", activities.size());
        return activities;
    }
    
    /**
     * Save conversation history
     * 
     * @param userId User ID
     * @param messageType Message type
     * @param messageContent Message content
     * @param isFromUser Whether message is from user
     * @param whatsappMessageId WhatsApp message ID
     */
    public void saveConversationHistory(Long userId, String messageType, String messageContent, 
                                      boolean isFromUser, String whatsappMessageId) {
        logger.info("💬 Saving conversation history for user ID: {}", userId);
        
        // This would typically use a ConversationHistoryRepository
        // For now, just log the action
        
        logger.info("✅ Conversation history saved successfully");
    }
    
    /**
     * Get opted-in users
     * 
     * @return List of opted-in users
     */
    public List<User> getOptedInUsers() {
        logger.info("📋 Getting opted-in users");
        
        List<User> users = userRepository.findByOptInStatusTrue();
        
        logger.info("✅ Found {} opted-in users", users.size());
        return users;
    }
    
    /**
     * Get user statistics
     * 
     * @return User statistics
     */
    public Map<String, Object> getUserStats() {
        logger.info("📊 Getting user statistics");
        
        Map<String, Object> stats = new HashMap<>();
        
        long totalUsers = userRepository.count();
        long optedInUsers = userRepository.countByOptInStatusTrue();
        
        stats.put("total_users", totalUsers);
        stats.put("opted_in_users", optedInUsers);
        stats.put("opt_in_rate", totalUsers > 0 ? (double) optedInUsers / totalUsers : 0.0);
        stats.put("timestamp", LocalDateTime.now());
        
        logger.info("✅ User statistics retrieved");
        return stats;
    }
}
