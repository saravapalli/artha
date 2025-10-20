package com.whatsapp.eventservice.controller;

import com.whatsapp.eventservice.model.Conversation;
import com.whatsapp.eventservice.model.Message;
import com.whatsapp.eventservice.model.SuggestedItem;
import com.whatsapp.eventservice.service.ConversationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for conversation management matching OpenAPI specification
 */
@RestController
@RequestMapping("/conversations")
@CrossOrigin(origins = "*")
public class ConversationController {
    
    private static final Logger logger = LoggerFactory.getLogger(ConversationController.class);
    
    @Autowired
    private ConversationService conversationService;
    
    /**
     * Start a new conversation - matches OpenAPI spec
     */
    @PostMapping
    public ResponseEntity<Conversation> createConversation(@RequestBody Map<String, Object> requestBody) {
        logger.info("💬 Creating new conversation");
        
        try {
            Long userId = Long.valueOf(requestBody.get("user_id").toString());
            Conversation conversation = conversationService.createConversation(userId);
            
            logger.info("✅ Conversation created successfully with ID: {}", conversation.getId());
            return ResponseEntity.ok(conversation);
            
        } catch (Exception e) {
            logger.error("❌ Error creating conversation", e);
            return ResponseEntity.status(500).body(null);
        }
    }
    
    /**
     * List all conversations - matches OpenAPI spec
     */
    @GetMapping
    public ResponseEntity<List<Conversation>> getAllConversations() {
        logger.info("💬 Getting all conversations");
        
        try {
            List<Conversation> conversations = conversationService.getAllConversations();
            logger.info("✅ Retrieved {} conversations", conversations.size());
            return ResponseEntity.ok(conversations);
            
        } catch (Exception e) {
            logger.error("❌ Error getting conversations", e);
            return ResponseEntity.status(500).body(null);
        }
    }
    
    /**
     * Send a message and get system response - matches OpenAPI spec
     */
    @PostMapping("/{conversationId}/messages")
    public ResponseEntity<Map<String, Object>> sendMessage(
            @PathVariable Long conversationId,
            @RequestBody Map<String, String> requestBody) {
        
        logger.info("📩 Sending message to conversation: {}", conversationId);
        
        try {
            String sender = requestBody.get("sender");
            String content = requestBody.get("content");
            
            Map<String, Object> response = conversationService.sendMessage(conversationId, sender, content);
            
            logger.info("✅ Message processed successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ Error sending message", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
