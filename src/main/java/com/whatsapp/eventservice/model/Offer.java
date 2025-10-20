package com.whatsapp.eventservice.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Offer entity matching OpenAPI specification - JDBC Template version
 */
public class Offer {
    
    private Long id;
    private String title;
    private String description;
    private String discountCode;
    private Long businessId;
    private Long eventId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive = true;
    private LocalDateTime createdAt;
    
    // Constructors
    public Offer() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Offer(String title, String description) {
        this();
        this.title = title;
        this.description = description;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getDiscountCode() {
        return discountCode;
    }
    
    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }
    
    public Long getBusinessId() {
        return businessId;
    }
    
    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }
    
    public Long getEventId() {
        return eventId;
    }
    
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "Offer{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", businessId=" + businessId +
                ", eventId=" + eventId +
                ", isActive=" + isActive +
                '}';
    }
}
