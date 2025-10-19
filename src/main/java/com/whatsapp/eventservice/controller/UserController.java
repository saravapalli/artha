package com.whatsapp.eventservice.controller;

import com.whatsapp.eventservice.model.User;
import com.whatsapp.eventservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for user management
 * 
 * This controller provides endpoints for:
 * - User registration and management
 * - User preferences
 * - User activity tracking
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;
    
    /**
     * Get user by phone number
     * 
     * @param phoneNumber User's phone number
     * @return User information
     */
    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<User> getUserByPhone(@PathVariable String phoneNumber) {
        logger.info("👤 Getting user by phone: {}", phoneNumber);
        
        try {
            User user = userService.getUserByPhone(phoneNumber);
            
            if (user != null) {
                logger.info("✅ Found user: {}", user.getName());
                return ResponseEntity.ok(user);
            } else {
                logger.warn("⚠️ User not found with phone: {}", phoneNumber);
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            logger.error("❌ Error getting user by phone", e);
            return ResponseEntity.status(500).body(null);
        }
    }
    
    /**
     * Get or create user
     * 
     * @param phoneNumber User's phone number
     * @param whatsappId WhatsApp ID
     * @return User information
     */
    @PostMapping("/get-or-create")
    public ResponseEntity<User> getOrCreateUser(
            @RequestParam String phoneNumber,
            @RequestParam(required = false) String whatsappId) {
        
        logger.info("👤 Getting or creating user with phone: {}", phoneNumber);
        
        try {
            User user = userService.getOrCreateUser(phoneNumber, whatsappId);
            
            logger.info("✅ User processed: {}", user.getName());
            return ResponseEntity.ok(user);
            
        } catch (Exception e) {
            logger.error("❌ Error getting or creating user", e);
            return ResponseEntity.status(500).body(null);
        }
    }
    
    /**
     * Update user information
     * 
     * @param id User ID
     * @param userData Updated user data
     * @return Updated user information
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> userData) {
        logger.info("✏️ Updating user ID: {}", id);
        
        try {
            User updatedUser = userService.updateUser(id, userData);
            
            if (updatedUser != null) {
                logger.info("✅ User updated successfully");
                return ResponseEntity.ok(updatedUser);
            } else {
                logger.warn("⚠️ User not found for update with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            logger.error("❌ Error updating user", e);
            return ResponseEntity.status(500).body(null);
        }
    }
    
    /**
     * Set user opt-in status
     * 
     * @param id User ID
     * @param optIn Opt-in status
     * @return Success response
     */
    @PutMapping("/{id}/opt-in")
    public ResponseEntity<Map<String, String>> setOptInStatus(
            @PathVariable Long id, 
            @RequestParam boolean optIn) {
        
        logger.info("📝 Setting opt-in status for user ID: {} to {}", id, optIn);
        
        try {
            boolean updated = userService.setOptInStatus(id, optIn);
            
            if (updated) {
                logger.info("✅ Opt-in status updated successfully");
                return ResponseEntity.ok(Map.of("message", "Opt-in status updated successfully"));
            } else {
                logger.warn("⚠️ User not found for opt-in update with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            logger.error("❌ Error updating opt-in status", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get user preferences
     * 
     * @param id User ID
     * @return User preferences
     */
    @GetMapping("/{id}/preferences")
    public ResponseEntity<Map<String, String>> getUserPreferences(@PathVariable Long id) {
        logger.info("⚙️ Getting preferences for user ID: {}", id);
        
        try {
            Map<String, String> preferences = userService.getUserPreferences(id);
            
            logger.info("✅ Retrieved {} preferences for user", preferences.size());
            return ResponseEntity.ok(preferences);
            
        } catch (Exception e) {
            logger.error("❌ Error getting user preferences", e);
            return ResponseEntity.status(500).body(null);
        }
    }
    
    /**
     * Save user preference
     * 
     * @param id User ID
     * @param preferenceData Preference data
     * @return Success response
     */
    @PostMapping("/{id}/preferences")
    public ResponseEntity<Map<String, String>> saveUserPreference(
            @PathVariable Long id, 
            @RequestBody Map<String, String> preferenceData) {
        
        logger.info("💾 Saving preference for user ID: {}", id);
        
        try {
            String category = preferenceData.get("category");
            String value = preferenceData.get("value");
            String source = preferenceData.getOrDefault("source", "user");
            
            userService.saveUserPreference(id, category, value, source);
            
            logger.info("✅ Preference saved successfully");
            return ResponseEntity.ok(Map.of("message", "Preference saved successfully"));
            
        } catch (Exception e) {
            logger.error("❌ Error saving user preference", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get user activity
     * 
     * @param id User ID
     * @param limit Maximum number of activities to return
     * @return User activity list
     */
    @GetMapping("/{id}/activity")
    public ResponseEntity<List<Map<String, Object>>> getUserActivity(
            @PathVariable Long id,
            @RequestParam(defaultValue = "50") int limit) {
        
        logger.info("📊 Getting activity for user ID: {}", id);
        
        try {
            List<Map<String, Object>> activities = userService.getUserActivity(id, limit);
            
            logger.info("✅ Retrieved {} activities for user", activities.size());
            return ResponseEntity.ok(activities);
            
        } catch (Exception e) {
            logger.error("❌ Error getting user activity", e);
            return ResponseEntity.status(500).body(null);
        }
    }
    
    /**
     * Log user activity
     * 
     * @param id User ID
     * @param activityData Activity data
     * @return Success response
     */
    @PostMapping("/{id}/activity")
    public ResponseEntity<Map<String, String>> logUserActivity(
            @PathVariable Long id, 
            @RequestBody Map<String, Object> activityData) {
        
        logger.info("📝 Logging activity for user ID: {}", id);
        
        try {
            String actionType = (String) activityData.get("actionType");
            String actionDetail = (String) activityData.get("actionDetail");
            Long eventId = activityData.get("eventId") != null ? 
                          Long.valueOf(activityData.get("eventId").toString()) : null;
            String queryText = (String) activityData.get("queryText");
            String aiInference = (String) activityData.get("aiInference");
            
            userService.logUserActivity(id, actionType, actionDetail, eventId, queryText, aiInference);
            
            logger.info("✅ Activity logged successfully");
            return ResponseEntity.ok(Map.of("message", "Activity logged successfully"));
            
        } catch (Exception e) {
            logger.error("❌ Error logging user activity", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get opted-in users
     * 
     * @return List of opted-in users
     */
    @GetMapping("/opted-in")
    public ResponseEntity<List<User>> getOptedInUsers() {
        logger.info("📋 Getting opted-in users");
        
        try {
            List<User> users = userService.getOptedInUsers();
            
            logger.info("✅ Found {} opted-in users", users.size());
            return ResponseEntity.ok(users);
            
        } catch (Exception e) {
            logger.error("❌ Error getting opted-in users", e);
            return ResponseEntity.status(500).body(null);
        }
    }
    
    /**
     * Get user statistics
     * 
     * @return User statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        logger.info("📊 Getting user statistics");
        
        try {
            Map<String, Object> stats = userService.getUserStats();
            
            logger.info("✅ User statistics retrieved");
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            logger.error("❌ Error getting user statistics", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
