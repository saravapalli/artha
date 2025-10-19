package com.whatsapp.eventservice.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * EventTag entity for storing event tags
 */
@Entity
@Table(name = "event_tags")
public class EventTag {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    
    @Column(name = "tag", nullable = false, length = 100)
    private String tag;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Constructors
    public EventTag() {
        this.createdAt = LocalDateTime.now();
    }
    
    public EventTag(Event event, String tag) {
        this();
        this.event = event;
        this.tag = tag;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Event getEvent() {
        return event;
    }
    
    public void setEvent(Event event) {
        this.event = event;
    }
    
    public String getTag() {
        return tag;
    }
    
    public void setTag(String tag) {
        this.tag = tag;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "EventTag{" +
                "id=" + id +
                ", tag='" + tag + '\'' +
                '}';
    }
}
