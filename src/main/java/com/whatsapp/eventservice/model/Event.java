package com.whatsapp.eventservice.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Event entity for storing local event information
 */
@Entity
@Table(name = "events")
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "title", nullable = false, length = 255)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "location", length = 255)
    private String location;
    
    @Column(name = "city", length = 100)
    private String city;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "category", length = 100)
    private String category;
    
    @Column(name = "subcategory", length = 100)
    private String subcategory;
    
    @Column(name = "price_range", length = 50)
    private String priceRange;
    
    @Column(name = "age_restriction", length = 50)
    private String ageRestriction;
    
    @Column(name = "image_url", length = 255)
    private String imageUrl;
    
    @Column(name = "ticket_url", length = 255)
    private String ticketUrl;
    
    @Column(name = "organizer_name", length = 255)
    private String organizerName;
    
    @Column(name = "organizer_contact", length = 255)
    private String organizerContact;
    
    @Column(name = "is_active")
    private Boolean active = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EventTag> tags;
    
    // Constructors
    public Event() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Event(String title, String description, String location, String city, 
                LocalDateTime startTime, LocalDateTime endTime, String category) {
        this();
        this.title = title;
        this.description = description;
        this.location = location;
        this.city = city;
        this.startTime = startTime;
        this.endTime = endTime;
        this.category = category;
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
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getSubcategory() {
        return subcategory;
    }
    
    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }
    
    public String getPriceRange() {
        return priceRange;
    }
    
    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }
    
    public String getAgeRestriction() {
        return ageRestriction;
    }
    
    public void setAgeRestriction(String ageRestriction) {
        this.ageRestriction = ageRestriction;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getTicketUrl() {
        return ticketUrl;
    }
    
    public void setTicketUrl(String ticketUrl) {
        this.ticketUrl = ticketUrl;
    }
    
    public String getOrganizerName() {
        return organizerName;
    }
    
    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }
    
    public String getOrganizerContact() {
        return organizerContact;
    }
    
    public void setOrganizerContact(String organizerContact) {
        this.organizerContact = organizerContact;
    }
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public List<EventTag> getTags() {
        return tags;
    }
    
    public void setTags(List<EventTag> tags) {
        this.tags = tags;
    }
    
    // Convenience methods
    public boolean isActive() {
        return active != null && active;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", city='" + city + '\'' +
                ", startTime=" + startTime +
                ", active=" + active +
                '}';
    }
}
