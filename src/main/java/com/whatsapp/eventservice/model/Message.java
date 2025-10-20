package com.whatsapp.eventservice.model;

import java.time.LocalDateTime;

/**
 * Message entity matching OpenAPI specification - JDBC Template version
 */
public class Message {
    
    private Long id;
    private Long conversationId;
    private Sender sender;
    private String content;
    private MessageType messageType;
    private LocalDateTime createdAt;
    
    public enum Sender {
        user, system
    }
    
    public enum MessageType {
        text, image, audio, video, interactive
    }
    
    // Constructors
    public Message() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Message(Long conversationId, Sender sender, String content, MessageType messageType) {
        this();
        this.conversationId = conversationId;
        this.sender = sender;
        this.content = content;
        this.messageType = messageType;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getConversationId() {
        return conversationId;
    }
    
    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }
    
    public Sender getSender() {
        return sender;
    }
    
    public void setSender(Sender sender) {
        this.sender = sender;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public MessageType getMessageType() {
        return messageType;
    }
    
    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", conversationId=" + conversationId +
                ", sender=" + sender +
                ", messageType=" + messageType +
                ", createdAt=" + createdAt +
                '}';
    }
}
