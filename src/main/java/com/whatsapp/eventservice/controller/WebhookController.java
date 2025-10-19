package com.whatsapp.eventservice.controller;

import com.whatsapp.eventservice.model.WhatsAppWebhookPayload;
import com.whatsapp.eventservice.service.WebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for handling WhatsApp webhook requests
 * 
 * This controller handles:
 * - Webhook verification (GET requests)
 * - Incoming messages (POST requests)
 * - Message status updates
 */
@RestController
@RequestMapping("/webhook")
@CrossOrigin(origins = "*")
public class WebhookController {
    
    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);
    
    @Autowired
    private WebhookService webhookService;
    
    /**
     * Handle webhook verification (GET request)
     * WhatsApp Cloud API sends a GET request to verify the webhook URL
     * 
     * @param mode Hub mode (should be "subscribe")
     * @param token Verify token (should match configured token)
     * @param challenge Challenge string to return
     * @return Challenge string if verification succeeds, error otherwise
     */
    @GetMapping
    public ResponseEntity<String> verifyWebhook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.verify_token") String token,
            @RequestParam("hub.challenge") String challenge) {
        
        logger.info("Webhook verification request - Mode: {}, Token: {}", mode, token);
        
        try {
            String response = webhookService.verifyWebhook(mode, token, challenge);
            
            if (response.equals(challenge)) {
                logger.info("✅ Webhook verification successful");
                return ResponseEntity.ok(response);
            } else {
                logger.warn("❌ Webhook verification failed - Invalid token or mode");
                return ResponseEntity.status(403).body("Verification failed");
            }
            
        } catch (Exception e) {
            logger.error("❌ Error during webhook verification", e);
            return ResponseEntity.status(500).body("Internal server error");
        }
    }
    
    /**
     * Handle incoming webhook messages (POST request)
     * WhatsApp Cloud API sends POST requests with message data
     * 
     * @param payload WhatsApp webhook payload containing messages and statuses
     * @return 200 OK response
     */
    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestBody WhatsAppWebhookPayload payload) {
        logger.info("📩 Received webhook payload with {} entries", 
                   payload.getEntry() != null ? payload.getEntry().size() : 0);
        
        try {
            // Process the webhook payload asynchronously
            webhookService.processWebhookPayload(payload);
            
            logger.info("✅ Webhook payload processed successfully");
            return ResponseEntity.ok("OK");
            
        } catch (Exception e) {
            logger.error("❌ Error processing webhook payload", e);
            return ResponseEntity.status(500).body("Internal server error");
        }
    }
    
    /**
     * Health check endpoint
     * 
     * @return Service status information
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        try {
            Map<String, Object> status = webhookService.getServiceStatus();
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            logger.error("❌ Health check failed", e);
            return ResponseEntity.status(500).body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * Get webhook configuration information
     * 
     * @return Webhook configuration details
     */
    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getWebhookConfig() {
        try {
            Map<String, Object> config = webhookService.getWebhookConfig();
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            logger.error("❌ Failed to get webhook config", e);
            return ResponseEntity.status(500).body(Map.of(
                "error", e.getMessage()
            ));
        }
    }
}
