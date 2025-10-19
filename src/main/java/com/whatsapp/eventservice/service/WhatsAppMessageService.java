package com.whatsapp.eventservice.service;

import com.whatsapp.eventservice.config.WhatsAppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for sending messages via WhatsApp Business API
 * 
 * This service handles sending text messages, interactive messages, and media
 * through the WhatsApp Cloud API.
 */
@Service
public class WhatsAppMessageService {
    
    private static final Logger logger = LoggerFactory.getLogger(WhatsAppMessageService.class);
    
    @Autowired
    private WhatsAppConfig whatsAppConfig;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    /**
     * Send a text message
     * 
     * @param to Recipient phone number
     * @param message Message text
     * @return True if sent successfully
     */
    public boolean sendTextMessage(String to, String message) {
        logger.info("📤 Sending text message to: {}", to);
        
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("messaging_product", "whatsapp");
            payload.put("to", to);
            payload.put("type", "text");
            
            Map<String, String> text = new HashMap<>();
            text.put("body", message);
            payload.put("text", text);
            
            return sendMessage(payload);
            
        } catch (Exception e) {
            logger.error("❌ Error sending text message", e);
            return false;
        }
    }
    
    /**
     * Send an interactive message with buttons
     * 
     * @param to Recipient phone number
     * @param body Message body
     * @param buttons Button definitions
     * @return True if sent successfully
     */
    public boolean sendInteractiveMessage(String to, String body, Map<String, String> buttons) {
        logger.info("📤 Sending interactive message to: {}", to);
        
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("messaging_product", "whatsapp");
            payload.put("to", to);
            payload.put("type", "interactive");
            
            Map<String, Object> interactive = new HashMap<>();
            interactive.put("type", "button");
            
            Map<String, String> bodyText = new HashMap<>();
            bodyText.put("text", body);
            interactive.put("body", bodyText);
            
            Map<String, Object> action = new HashMap<>();
            action.put("buttons", createButtonList(buttons));
            interactive.put("action", action);
            
            payload.put("interactive", interactive);
            
            return sendMessage(payload);
            
        } catch (Exception e) {
            logger.error("❌ Error sending interactive message", e);
            return false;
        }
    }
    
    /**
     * Send an interactive list message
     * 
     * @param to Recipient phone number
     * @param body Message body
     * @param buttonText Button text
     * @param sections List sections
     * @return True if sent successfully
     */
    public boolean sendListMessage(String to, String body, String buttonText, Map<String, String> sections) {
        logger.info("📤 Sending list message to: {}", to);
        
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("messaging_product", "whatsapp");
            payload.put("to", to);
            payload.put("type", "interactive");
            
            Map<String, Object> interactive = new HashMap<>();
            interactive.put("type", "list");
            
            Map<String, String> bodyText = new HashMap<>();
            bodyText.put("text", body);
            interactive.put("body", bodyText);
            
            Map<String, Object> action = new HashMap<>();
            action.put("button", buttonText);
            action.put("sections", createSectionList(sections));
            interactive.put("action", action);
            
            payload.put("interactive", interactive);
            
            return sendMessage(payload);
            
        } catch (Exception e) {
            logger.error("❌ Error sending list message", e);
            return false;
        }
    }
    
    /**
     * Send a template message (for notifications)
     * 
     * @param to Recipient phone number
     * @param templateName Template name
     * @param language Language code
     * @param parameters Template parameters
     * @return True if sent successfully
     */
    public boolean sendTemplateMessage(String to, String templateName, String language, Map<String, String> parameters) {
        logger.info("📤 Sending template message to: {}", to);
        
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("messaging_product", "whatsapp");
            payload.put("to", to);
            payload.put("type", "template");
            
            Map<String, Object> template = new HashMap<>();
            template.put("name", templateName);
            template.put("language", Map.of("code", language));
            
            if (parameters != null && !parameters.isEmpty()) {
                Map<String, Object> components = new HashMap<>();
                components.put("type", "body");
                components.put("parameters", createParameterList(parameters));
                template.put("components", new Object[]{components});
            }
            
            payload.put("template", template);
            
            return sendMessage(payload);
            
        } catch (Exception e) {
            logger.error("❌ Error sending template message", e);
            return false;
        }
    }
    
    /**
     * Send a media message (image, document, etc.)
     * 
     * @param to Recipient phone number
     * @param mediaType Media type (image, document, audio, video)
     * @param mediaUrl Media URL
     * @param caption Media caption
     * @return True if sent successfully
     */
    public boolean sendMediaMessage(String to, String mediaType, String mediaUrl, String caption) {
        logger.info("📤 Sending {} message to: {}", mediaType, to);
        
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("messaging_product", "whatsapp");
            payload.put("to", to);
            payload.put("type", mediaType);
            
            Map<String, String> media = new HashMap<>();
            media.put("link", mediaUrl);
            if (caption != null && !caption.isEmpty()) {
                media.put("caption", caption);
            }
            payload.put(mediaType, media);
            
            return sendMessage(payload);
            
        } catch (Exception e) {
            logger.error("❌ Error sending media message", e);
            return false;
        }
    }
    
    /**
     * Send message with event details and interactive buttons
     * 
     * @param to Recipient phone number
     * @param eventTitle Event title
     * @param eventDetails Event details
     * @param eventId Event ID
     * @return True if sent successfully
     */
    public boolean sendEventMessage(String to, String eventTitle, String eventDetails, String eventId) {
        String body = "🎉 " + eventTitle + "\n\n" + eventDetails;
        
        Map<String, String> buttons = new HashMap<>();
        buttons.put("interested", "I'm Interested");
        buttons.put("not_interested", "Not Interested");
        buttons.put("more_info", "More Info");
        
        return sendInteractiveMessage(to, body, buttons);
    }
    
    /**
     * Send welcome message with options
     * 
     * @param to Recipient phone number
     * @return True if sent successfully
     */
    public boolean sendWelcomeMessage(String to) {
        String body = """
            🎉 Welcome to Local Events Assistant!
            
            I can help you discover amazing events in your area. Here's what you can do:
            
            • Ask about events by category (music, sports, art, family-friendly)
            • Search by time (today, this weekend, this week)
            • Find events by location (downtown, near me, specific city)
            • Get personalized recommendations
            
            Try asking: "What music events are happening this weekend?"
            """;
        
        Map<String, String> buttons = new HashMap<>();
        buttons.put("browse_events", "Browse Events");
        buttons.put("set_preferences", "Set Preferences");
        buttons.put("help", "Help");
        
        return sendInteractiveMessage(to, body, buttons);
    }
    
    /**
     * Send help message
     * 
     * @param to Recipient phone number
     * @return True if sent successfully
     */
    public boolean sendHelpMessage(String to) {
        String body = """
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
        
        return sendTextMessage(to, body);
    }
    
    /**
     * Send error message
     * 
     * @param to Recipient phone number
     * @param error Error message
     * @return True if sent successfully
     */
    public boolean sendErrorMessage(String to, String error) {
        String body = "❌ Sorry, I encountered an error: " + error + 
                     "\n\nPlease try again or contact support if the issue persists.";
        return sendTextMessage(to, body);
    }
    
    /**
     * Core method to send message via WhatsApp API
     * 
     * @param payload Message payload
     * @return True if sent successfully
     */
    private boolean sendMessage(Map<String, Object> payload) {
        try {
            if (whatsAppConfig.getAccessToken() == null || whatsAppConfig.getPhoneNumberId() == null) {
                logger.error("❌ WhatsApp credentials not configured");
                return false;
            }
            
            String url = whatsAppConfig.getApiUrl() + "/" + whatsAppConfig.getPhoneNumberId() + "/messages";
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + whatsAppConfig.getAccessToken());
            headers.set("Content-Type", "application/json");
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("✅ Message sent successfully");
                return true;
            } else {
                logger.error("❌ Failed to send message. Status: {}", response.getStatusCode());
                return false;
            }
            
        } catch (Exception e) {
            logger.error("❌ Error sending message via WhatsApp API", e);
            return false;
        }
    }
    
    /**
     * Create button list for interactive messages
     * 
     * @param buttons Button definitions
     * @return Button list
     */
    private Object[] createButtonList(Map<String, String> buttons) {
        Object[] buttonList = new Object[buttons.size()];
        int index = 0;
        
        for (Map.Entry<String, String> entry : buttons.entrySet()) {
            Map<String, Object> button = new HashMap<>();
            button.put("type", "reply");
            
            Map<String, String> reply = new HashMap<>();
            reply.put("id", entry.getKey());
            reply.put("title", entry.getValue());
            button.put("reply", reply);
            
            buttonList[index++] = button;
        }
        
        return buttonList;
    }
    
    /**
     * Create section list for list messages
     * 
     * @param sections Section definitions
     * @return Section list
     */
    private Object[] createSectionList(Map<String, String> sections) {
        Object[] sectionList = new Object[sections.size()];
        int index = 0;
        
        for (Map.Entry<String, String> entry : sections.entrySet()) {
            Map<String, Object> section = new HashMap<>();
            section.put("title", entry.getKey());
            section.put("rows", new Object[]{}); // Simplified - would need actual row data
            sectionList[index++] = section;
        }
        
        return sectionList;
    }
    
    /**
     * Create parameter list for template messages
     * 
     * @param parameters Parameter definitions
     * @return Parameter list
     */
    private Object[] createParameterList(Map<String, String> parameters) {
        Object[] paramList = new Object[parameters.size()];
        int index = 0;
        
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            Map<String, String> param = new HashMap<>();
            param.put("type", "text");
            param.put("text", entry.getValue());
            paramList[index++] = param;
        }
        
        return paramList;
    }
}
