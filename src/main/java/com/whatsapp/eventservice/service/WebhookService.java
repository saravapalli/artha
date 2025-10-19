package com.whatsapp.eventservice.service;

import com.whatsapp.eventservice.config.WhatsAppConfig;
import com.whatsapp.eventservice.model.WhatsAppWebhookPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Service for handling WhatsApp webhook operations
 * 
 * This service processes incoming webhook requests from WhatsApp Cloud API
 * and manages webhook verification and message processing.
 */
@Service
public class WebhookService {
    
    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);
    
    @Autowired
    private WhatsAppConfig whatsAppConfig;
    
    @Autowired
    private MessageProcessingService messageProcessingService;
    
    /**
     * Verify webhook URL
     * 
     * @param mode Hub mode (should be "subscribe")
     * @param token Verify token
     * @param challenge Challenge string
     * @return Challenge string if verification succeeds, error message otherwise
     */
    public String verifyWebhook(String mode, String token, String challenge) {
        logger.info("🔍 Verifying webhook - Mode: {}, Token: {}", mode, token);
        
        if ("subscribe".equals(mode) && whatsAppConfig.getVerifyToken().equals(token)) {
            logger.info("✅ Webhook verification successful");
            return challenge;
        } else {
            logger.warn("❌ Webhook verification failed - Invalid mode or token");
            return "Verification failed";
        }
    }
    
    /**
     * Process webhook payload asynchronously
     * 
     * @param payload WhatsApp webhook payload
     * @return CompletableFuture for async processing
     */
    @Async
    public CompletableFuture<Void> processWebhookPayload(WhatsAppWebhookPayload payload) {
        logger.info("📩 Processing webhook payload asynchronously");
        
        try {
            if (payload.getEntry() == null || payload.getEntry().isEmpty()) {
                logger.warn("⚠️ No valid entry in webhook payload");
                return CompletableFuture.completedFuture(null);
            }
            
            for (WhatsAppWebhookPayload.WebhookEntry entry : payload.getEntry()) {
                if (entry.getChanges() == null || entry.getChanges().isEmpty()) {
                    continue;
                }
                
                for (WhatsAppWebhookPayload.WebhookChange change : entry.getChanges()) {
                    if (change.getValue() == null) {
                        continue;
                    }
                    
                    // Process messages
                    if (change.getValue().getMessages() != null && !change.getValue().getMessages().isEmpty()) {
                        for (WhatsAppWebhookPayload.WhatsAppMessage message : change.getValue().getMessages()) {
                            messageProcessingService.processIncomingMessage(message, change.getValue().getContacts());
                        }
                    }
                    
                    // Process status updates
                    if (change.getValue().getStatuses() != null && !change.getValue().getStatuses().isEmpty()) {
                        for (WhatsAppWebhookPayload.MessageStatus status : change.getValue().getStatuses()) {
                            messageProcessingService.processMessageStatus(status);
                        }
                    }
                }
            }
            
            logger.info("✅ Webhook payload processed successfully");
            
        } catch (Exception e) {
            logger.error("❌ Error processing webhook payload", e);
        }
        
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * Get service status
     * 
     * @return Service status information
     */
    public Map<String, Object> getServiceStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "healthy");
        status.put("service", "WhatsApp Event Service");
        status.put("timestamp", System.currentTimeMillis());
        status.put("version", "1.0.0");
        status.put("whatsapp_configured", whatsAppConfig.getAccessToken() != null);
        status.put("phone_number_id", whatsAppConfig.getPhoneNumberId());
        
        return status;
    }
    
    /**
     * Get webhook configuration
     * 
     * @return Webhook configuration details
     */
    public Map<String, Object> getWebhookConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("verify_token", whatsAppConfig.getVerifyToken());
        config.put("api_url", whatsAppConfig.getApiUrl());
        config.put("phone_number_id", whatsAppConfig.getPhoneNumberId());
        config.put("access_token_configured", whatsAppConfig.getAccessToken() != null);
        
        return config;
    }
}
