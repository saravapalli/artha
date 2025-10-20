package com.whatsapp.eventservice.service;

import com.whatsapp.eventservice.model.Conversation;
import com.whatsapp.eventservice.model.Message;
import com.whatsapp.eventservice.model.SuggestedItem;
import com.whatsapp.eventservice.repository.ConversationRepository;
import com.whatsapp.eventservice.repository.MessageRepository;
import com.whatsapp.eventservice.repository.SuggestedItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for managing conversations and messages matching OpenAPI specification
 */
@Service
@Transactional
public class ConversationService {
    
    private static final Logger logger = LoggerFactory.getLogger(ConversationService.class);
    
    @Autowired
    private ConversationRepository conversationRepository;
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private SuggestedItemRepository suggestedItemRepository;
    
    /**
     * Create a new conversation - matches OpenAPI spec
     */
    public Conversation createConversation(Long userId) {
        logger.info("💬 Creating new conversation for user: {}", userId);
        
        Conversation conversation = new Conversation(userId);
        conversation = conversationRepository.save(conversation);
        
        logger.info("✅ Conversation created successfully with ID: {}", conversation.getId());
        return conversation;
    }
    
    /**
     * Get all conversations - matches OpenAPI spec
     */
    public List<Conversation> getAllConversations() {
        logger.info("💬 Getting all conversations");
        
        List<Conversation> conversations = conversationRepository.findAll();
        logger.info("✅ Retrieved {} conversations", conversations.size());
        
        return conversations;
    }
    
    /**
     * Send a message and get system response - matches OpenAPI spec
     */
    public Map<String, Object> sendMessage(Long conversationId, String sender, String content) {
        logger.info("📩 Sending message to conversation: {}, sender: {}", conversationId, sender);
        
        // Create and save the user message
        Message.MessageType messageType = Message.MessageType.text;
        Message.Sender senderEnum = Message.Sender.valueOf(sender.toLowerCase());
        
        Message userMessage = new Message(conversationId, senderEnum, content, messageType);
        messageRepository.save(userMessage);
        
        // Generate system response (simplified for now)
        String systemResponse = "Thank you for your message: " + content;
        Message systemMessage = new Message(conversationId, Message.Sender.system, systemResponse, messageType);
        messageRepository.save(systemMessage);
        
        // For now, return empty suggested items
        List<SuggestedItem> suggestedItems = List.of();
        
        Map<String, Object> response = new HashMap<>();
        response.put("system_message", systemMessage);
        response.put("suggested_items", suggestedItems);
        
        logger.info("✅ Message processed successfully");
        return response;
    }
}
