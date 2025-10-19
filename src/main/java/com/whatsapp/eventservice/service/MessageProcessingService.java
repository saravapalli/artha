package com.whatsapp.eventservice.service;

import com.whatsapp.eventservice.model.WhatsAppWebhookPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service for processing WhatsApp messages
 * 
 * This service handles the business logic for processing incoming messages
 * and generating appropriate responses.
 */
@Service
public class MessageProcessingService {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageProcessingService.class);
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private EventService eventService;
    
    @Autowired
    private AIQueryProcessor aiQueryProcessor;
    
    @Autowired
    private WhatsAppMessageService whatsAppMessageService;
    
    /**
     * Process incoming WhatsApp message
     * 
     * @param message WhatsApp message
     * @param contacts Contact information
     */
    public void processIncomingMessage(WhatsAppWebhookPayload.WhatsAppMessage message, 
                                     List<WhatsAppWebhookPayload.WhatsAppContact> contacts) {
        try {
            String fromNumber = message.getFrom();
            String messageType = message.getType();
            String messageId = message.getId();
            
            logger.info("📩 Processing message from {} - Type: {}, ID: {}", fromNumber, messageType, messageId);
            
            // Get or create user
            com.whatsapp.eventservice.model.User user = userService.getOrCreateUser(fromNumber, fromNumber);
            
            // Save conversation history
            saveConversationHistory(user.getId(), messageType, getMessageContent(message), true, messageId);
            
            // Log user activity
            userService.logUserActivity(user.getId(), "message_received", messageType, null, 
                                      getMessageContent(message), null);
            
            // Process different message types
            String response = processMessageByType(user, message, contacts);
            
            // Send response if available
            if (response != null && !response.isEmpty()) {
                whatsAppMessageService.sendTextMessage(fromNumber, response);
                
                // Save response to conversation history
                saveConversationHistory(user.getId(), "text", response, false, null);
            }
            
        } catch (Exception e) {
            logger.error("❌ Error processing incoming message", e);
            
            // Send error message to user
            try {
                whatsAppMessageService.sendTextMessage(message.getFrom(), 
                    "Sorry, I encountered an error processing your message. Please try again later.");
            } catch (Exception ex) {
                logger.error("❌ Failed to send error message", ex);
            }
        }
    }
    
    /**
     * Process message status update
     * 
     * @param status Message status
     */
    public void processMessageStatus(WhatsAppWebhookPayload.MessageStatus status) {
        try {
            logger.info("📊 Processing message status - ID: {}, Status: {}", 
                       status.getId(), status.getStatus());
            
            // Update message status in database
            // This could be used for delivery confirmation, read receipts, etc.
            
        } catch (Exception e) {
            logger.error("❌ Error processing message status", e);
        }
    }
    
    /**
     * Process message by type
     * 
     * @param user User information
     * @param message WhatsApp message
     * @param contacts Contact information
     * @return Response message
     */
    private String processMessageByType(com.whatsapp.eventservice.model.User user, 
                                      WhatsAppWebhookPayload.WhatsAppMessage message,
                                      List<WhatsAppWebhookPayload.WhatsAppContact> contacts) {
        
        String messageType = message.getType();
        String messageContent = getMessageContent(message);
        
        switch (messageType) {
            case "text":
                return processTextMessage(user, messageContent);
                
            case "interactive":
                return processInteractiveMessage(user, message);
                
            case "image":
                return processImageMessage(user, message);
                
            case "document":
                return processDocumentMessage(user, message);
                
            case "audio":
                return processAudioMessage(user, message);
                
            case "video":
                return processVideoMessage(user, message);
                
            case "location":
                return processLocationMessage(user, message);
                
            default:
                return "I can only process text messages and interactive buttons right now. Please send me a text message about events you're interested in!";
        }
    }
    
    /**
     * Process text message
     * 
     * @param user User information
     * @param messageText Message text
     * @return Response message
     */
    private String processTextMessage(com.whatsapp.eventservice.model.User user, String messageText) {
        try {
            String lowerText = messageText.toLowerCase().trim();
            
            // Handle opt-in/opt-out
            if (lowerText.contains("stop") || lowerText.contains("unsubscribe")) {
                userService.setOptInStatus(user.getId(), false);
                return "You have been unsubscribed from event notifications. Reply 'START' to subscribe again.";
            }
            
            if (lowerText.contains("start") || lowerText.contains("subscribe")) {
                userService.setOptInStatus(user.getId(), true);
                return "Welcome! You're now subscribed to local event notifications. Ask me about events like 'What music events are happening this weekend?' or 'Show me family-friendly events in Boston'.";
            }
            
            // Check if user is opted in
            if (!user.isOptInStatus()) {
                return "Please reply 'START' to subscribe to event notifications first.";
            }
            
            // Process event query using AI
            Map<String, Object> criteria = aiQueryProcessor.parseEventQuery(messageText);
            
            // Log AI inference
            userService.logUserActivity(user.getId(), "ai_query", "event_search", null, 
                                      messageText, criteria.toString());
            
            // Search events based on AI criteria
            List<com.whatsapp.eventservice.model.Event> events = eventService.searchEventsByCriteria(criteria);
            
            // Generate response
            return generateEventResponse(events, criteria, user);
            
        } catch (Exception e) {
            logger.error("❌ Error processing text message", e);
            return "Sorry, I encountered an error processing your message. Please try again.";
        }
    }
    
    /**
     * Process interactive message (button clicks)
     * 
     * @param user User information
     * @param message WhatsApp message
     * @return Response message
     */
    private String processInteractiveMessage(com.whatsapp.eventservice.model.User user, 
                                           WhatsAppWebhookPayload.WhatsAppMessage message) {
        try {
            WhatsAppWebhookPayload.MessageInteractive interactive = message.getInteractive();
            
            if (interactive == null) {
                return "I didn't understand that interaction. Please send me a text message.";
            }
            
            String buttonId = null;
            String buttonTitle = null;
            
            if (interactive.getButtonReply() != null) {
                buttonId = interactive.getButtonReply().getId();
                buttonTitle = interactive.getButtonReply().getTitle();
            } else if (interactive.getListReply() != null) {
                buttonId = interactive.getListReply().getId();
                buttonTitle = interactive.getListReply().getTitle();
            }
            
            logger.info("🔘 Button clicked: {} (ID: {})", buttonTitle, buttonId);
            
            // Log button interaction
            userService.logUserActivity(user.getId(), "button_click", buttonId, null, buttonTitle, null);
            
            // Handle different button actions
            return handleButtonAction(user, buttonId, buttonTitle);
            
        } catch (Exception e) {
            logger.error("❌ Error processing interactive message", e);
            return "Sorry, I encountered an error processing your interaction. Please try again.";
        }
    }
    
    /**
     * Handle button action
     * 
     * @param user User information
     * @param buttonId Button ID
     * @param buttonTitle Button title
     * @return Response message
     */
    private String handleButtonAction(com.whatsapp.eventservice.model.User user, 
                                    String buttonId, String buttonTitle) {
        
        if (buttonId == null) {
            return "Thanks for your feedback! Is there anything specific about events you'd like to know?";
        }
        
        switch (buttonId) {
            case "interested":
                userService.saveUserPreference(user.getId(), "interest_level", "high", "user");
                return "Great! I'll prioritize similar events for you. What type of events are you most interested in?";
                
            case "not_interested":
                userService.saveUserPreference(user.getId(), "interest_level", "low", "user");
                return "No problem! I'll adjust my recommendations. What events would you prefer to see?";
                
            case "more_info":
                return "For more information about events, you can ask me specific questions like 'Tell me more about concerts this weekend' or 'What's happening at the downtown venue?'";
                
            case "change_preferences":
                return "You can tell me your preferences like 'I like music events' or 'Show me family-friendly activities'. What would you like to see?";
                
            case "browse_events":
                return "You can browse events by asking me questions like:\n• 'What music events are this weekend?'\n• 'Show me family-friendly activities'\n• 'Any free events today?'";
                
            case "set_preferences":
                return "You can set your preferences by telling me what you like:\n• 'I like music events'\n• 'Show me family-friendly activities'\n• 'I prefer free events'";
                
            case "help":
                return getHelpMessage();
                
            default:
                return "Thanks for your feedback! Is there anything specific about events you'd like to know?";
        }
    }
    
    /**
     * Process image message
     * 
     * @param user User information
     * @param message WhatsApp message
     * @return Response message
     */
    private String processImageMessage(com.whatsapp.eventservice.model.User user, 
                                     WhatsAppWebhookPayload.WhatsAppMessage message) {
        logger.info("🖼️ Processing image message from user {}", user.getId());
        
        // For now, just acknowledge the image
        return "I received your image! I can currently only process text messages about events. Please send me a text message asking about events you're interested in.";
    }
    
    /**
     * Process document message
     * 
     * @param user User information
     * @param message WhatsApp message
     * @return Response message
     */
    private String processDocumentMessage(com.whatsapp.eventservice.model.User user, 
                                        WhatsAppWebhookPayload.WhatsAppMessage message) {
        logger.info("📄 Processing document message from user {}", user.getId());
        
        return "I received your document! I can currently only process text messages about events. Please send me a text message asking about events you're interested in.";
    }
    
    /**
     * Process audio message
     * 
     * @param user User information
     * @param message WhatsApp message
     * @return Response message
     */
    private String processAudioMessage(com.whatsapp.eventservice.model.User user, 
                                     WhatsAppWebhookPayload.WhatsAppMessage message) {
        logger.info("🎵 Processing audio message from user {}", user.getId());
        
        return "I received your audio message! I can currently only process text messages about events. Please send me a text message asking about events you're interested in.";
    }
    
    /**
     * Process video message
     * 
     * @param user User information
     * @param message WhatsApp message
     * @return Response message
     */
    private String processVideoMessage(com.whatsapp.eventservice.model.User user, 
                                     WhatsAppWebhookPayload.WhatsAppMessage message) {
        logger.info("🎥 Processing video message from user {}", user.getId());
        
        return "I received your video! I can currently only process text messages about events. Please send me a text message asking about events you're interested in.";
    }
    
    /**
     * Process location message
     * 
     * @param user User information
     * @param message WhatsApp message
     * @return Response message
     */
    private String processLocationMessage(com.whatsapp.eventservice.model.User user, 
                                        WhatsAppWebhookPayload.WhatsAppMessage message) {
        logger.info("📍 Processing location message from user {}", user.getId());
        
        WhatsAppWebhookPayload.MessageLocation location = message.getLocation();
        if (location != null) {
            // Update user location preference
            userService.saveUserPreference(user.getId(), "location", 
                location.getLatitude() + "," + location.getLongitude(), "user");
            
            return "Thanks for sharing your location! I'll use this to find events near you. What type of events are you interested in?";
        }
        
        return "I received your location! What type of events are you interested in?";
    }
    
    /**
     * Generate event response
     * 
     * @param events List of events
     * @param criteria Search criteria
     * @param user User information
     * @return Formatted response
     */
    private String generateEventResponse(List<com.whatsapp.eventservice.model.Event> events, 
                                       Map<String, Object> criteria, 
                                       com.whatsapp.eventservice.model.User user) {
        
        if (events.isEmpty()) {
            return "I couldn't find any events matching your criteria. Try asking about different categories like 'music', 'sports', 'family-friendly', or 'art' events.";
        }
        
        StringBuilder response = new StringBuilder();
        
        if (events.size() == 1) {
            response.append("🎉 Here's an event that matches your request:\n\n");
        } else {
            response.append("🎉 Here are ").append(events.size()).append(" events that match your request:\n\n");
        }
        
        for (int i = 0; i < Math.min(events.size(), 3); i++) {
            com.whatsapp.eventservice.model.Event event = events.get(i);
            response.append("📍 ").append(event.getTitle()).append("\n");
            
            if (event.getStartTime() != null) {
                response.append("📅 ").append(event.getStartTime().toString().substring(0, 16)).append("\n");
            }
            
            if (event.getLocation() != null) {
                response.append("📍 ").append(event.getLocation());
                if (event.getCity() != null) {
                    response.append(", ").append(event.getCity());
                }
                response.append("\n");
            }
            
            if (event.getDescription() != null && !event.getDescription().isEmpty()) {
                String description = event.getDescription();
                if (description.length() > 100) {
                    description = description.substring(0, 100) + "...";
                }
                response.append("📝 ").append(description).append("\n");
            }
            
            if (event.getPriceRange() != null) {
                response.append("💰 ").append(event.getPriceRange()).append("\n");
            }
            
            response.append("\n");
        }
        
        if (events.size() > 3) {
            response.append("... and ").append(events.size() - 3).append(" more events. Ask me for more details about any specific event!");
        }
        
        response.append("\n💡 Tip: Click the buttons below to let me know your preferences!");
        
        return response.toString();
    }
    
    /**
     * Get help message
     * 
     * @return Help message
     */
    private String getHelpMessage() {
        return """
            📚 How to use Local Events Assistant:
            
            🎵 **Music Events:**
            "What concerts are this weekend?"
            "Any jazz events tonight?"
            
            🏃 **Sports:**
            "Show me sports games this week"
            "Any basketball games?"
            
            👨‍👩‍👧‍👦 **Family Events:**
            "Family-friendly activities"
            "Kids events this weekend"
            
            🎨 **Art & Culture:**
            "Art exhibitions this month"
            "Museum events"
            
            📍 **Location:**
            "Events in Boston"
            "What's happening downtown?"
            
            💰 **Price:**
            "Free events this weekend"
            "Cheap activities"
            
            Just ask me naturally - I understand conversational language!
            """;
    }
    
    /**
     * Get message content based on message type
     * 
     * @param message WhatsApp message
     * @return Message content
     */
    private String getMessageContent(WhatsAppWebhookPayload.WhatsAppMessage message) {
        switch (message.getType()) {
            case "text":
                return message.getText() != null ? message.getText().getBody() : "";
            case "interactive":
                if (message.getInteractive() != null && message.getInteractive().getButtonReply() != null) {
                    return message.getInteractive().getButtonReply().getTitle();
                }
                return "interactive";
            case "image":
                return "image";
            case "document":
                return "document";
            case "audio":
                return "audio";
            case "video":
                return "video";
            case "location":
                return "location";
            default:
                return message.getType();
        }
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
    private void saveConversationHistory(Long userId, String messageType, String messageContent, 
                                       boolean isFromUser, String whatsappMessageId) {
        try {
            userService.saveConversationHistory(userId, messageType, messageContent, isFromUser, whatsappMessageId);
        } catch (Exception e) {
            logger.error("❌ Error saving conversation history", e);
        }
    }
}
