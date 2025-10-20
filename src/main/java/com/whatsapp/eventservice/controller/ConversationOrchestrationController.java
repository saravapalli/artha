package com.whatsapp.eventservice.controller;

import com.whatsapp.eventservice.service.ConversationOrchestrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * High-level orchestration controller that handles the complete conversation flow
 * as described in the README.md workflow.
 * 
 * This controller provides the main entry point for user messages and orchestrates
 * the complete flow: message → conversation → LLM processing → database queries → response
 */
@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class ConversationOrchestrationController {
    
    private static final Logger logger = LoggerFactory.getLogger(ConversationOrchestrationController.class);
    
    @Autowired
    private ConversationOrchestrationService orchestrationService;
    
    /**
     * Main entry point for user messages - implements the complete workflow from README.md
     * 
     * 1. User sends a message → API receives it.
     * 2. API creates conversation if new and logs the message.
     * 3. API sends message to LLM for intent parsing and query generation.
     * 4. LLM returns parsed query (entities, intent, type of content requested).
     * 5. API queries database for events, businesses, and offers based on parsed query.
     * 6. API stores suggested items in suggested_item table.
     * 7. API sends suggestions to LLM to generate response text.
     * 8. LLM returns response text → API sends it to the user.
     */
    @PostMapping("/message")
    public ResponseEntity<Map<String, Object>> processUserMessage(@RequestBody Map<String, Object> requestBody) {
        logger.info("📩 Processing user message");
        
        try {
            // Extract required fields
            Long userId = Long.valueOf(requestBody.get("user_id").toString());
            String content = (String) requestBody.get("content");
            String messageType = (String) requestBody.getOrDefault("message_type", "text");
            
            logger.info("💬 Processing message for user: {}, content length: {}", userId, content.length());
            
            // Orchestrate the complete workflow
            Map<String, Object> response = orchestrationService.processUserMessage(userId, content, messageType);
            
            logger.info("✅ Message processed successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ Error processing user message", e);
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to process message: " + e.getMessage()));
        }
    }
    
    /**
     * Handle user feedback on suggested items - Step 9 from README workflow
     * User provides feedback → API stores feedback and updates user interests.
     */
    @PostMapping("/feedback")
    public ResponseEntity<Map<String, Object>> processUserFeedback(@RequestBody Map<String, Object> requestBody) {
        logger.info("👍 Processing user feedback");
        
        try {
            Long userId = Long.valueOf(requestBody.get("user_id").toString());
            Long suggestionId = Long.valueOf(requestBody.get("suggestion_id").toString());
            String feedbackType = (String) requestBody.get("feedback_type");
            
            logger.info("💬 Processing feedback for user: {}, suggestion: {}, type: {}", 
                       userId, suggestionId, feedbackType);
            
            Map<String, Object> response = orchestrationService.processUserFeedback(userId, suggestionId, feedbackType);
            
            logger.info("✅ Feedback processed successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ Error processing user feedback", e);
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to process feedback: " + e.getMessage()));
        }
    }
    
    /**
     * Get conversation context for a user
     */
    @GetMapping("/conversations/user/{userId}")
    public ResponseEntity<Map<String, Object>> getConversationContext(@PathVariable Long userId) {
        logger.info("📋 Getting conversation context for user: {}", userId);
        
        try {
            Map<String, Object> response = orchestrationService.getConversationContext(userId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ Error getting conversation context", e);
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to get conversation context: " + e.getMessage()));
        }
    }
}
