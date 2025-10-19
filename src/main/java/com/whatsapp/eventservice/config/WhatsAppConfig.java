package com.whatsapp.eventservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for WhatsApp Business API
 */
@Configuration
@ConfigurationProperties(prefix = "whatsapp")
public class WhatsAppConfig {
    
    private String accessToken;
    private String phoneNumberId;
    private String verifyToken = "mywhatsappverify";
    private String apiUrl = "https://graph.facebook.com/v21.0";
    
    // Getters and Setters
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getPhoneNumberId() {
        return phoneNumberId;
    }
    
    public void setPhoneNumberId(String phoneNumberId) {
        this.phoneNumberId = phoneNumberId;
    }
    
    public String getVerifyToken() {
        return verifyToken;
    }
    
    public void setVerifyToken(String verifyToken) {
        this.verifyToken = verifyToken;
    }
    
    public String getApiUrl() {
        return apiUrl;
    }
    
    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
}
