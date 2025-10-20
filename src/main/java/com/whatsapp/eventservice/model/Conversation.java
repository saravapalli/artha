package com.whatsapp.eventservice.model;

import java.time.LocalDateTime;

/**
 * Conversation entity matching OpenAPI specification - JDBC Template version
 */
public class Conversation {
    
    private Long id;
    private Long userId;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private String contextSummary;
    
    // Constructors
    public Conversation() {
        this.startedAt = LocalDateTime.now();
    }
    
    public Conversation(Long userId) {
        this();
        this.userId = userId;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public LocalDateTime getStartedAt() {
        return startedAt;
    }
    
    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }
    
    public LocalDateTime getEndedAt() {
        return endedAt;
    }
    
    public void setEndedAt(LocalDateTime endedAt) {
        this.endedAt = endedAt;
    }
    
    public String getContextSummary() {
        return contextSummary;
    }
    
    public void setContextSummary(String contextSummary) {
        this.contextSummary = contextSummary;
    }
    
    @Override
    public String toString() {
        return "Conversation{" +
                "id=" + id +
                ", userId=" + userId +
                ", startedAt=" + startedAt +
                '}';
    }
}
