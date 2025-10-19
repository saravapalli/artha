package com.whatsapp.eventservice.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * User entity for storing WhatsApp user information
 */
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "phone_number", nullable = false, unique = true, length = 20)
    private String phoneNumber;
    
    @Column(name = "whatsapp_id", unique = true)
    private String whatsappId;
    
    @Column(name = "name", length = 100)
    private String name;
    
    @Column(name = "email", length = 150)
    private String email;
    
    @Column(name = "city", length = 100)
    private String city;
    
    @Column(name = "timezone", length = 50)
    private String timezone;
    
    @Column(name = "language", length = 20)
    private String language = "en";
    
    @Column(name = "opt_in_status")
    private Boolean optInStatus = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public User() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public User(String phoneNumber, String whatsappId) {
        this();
        this.phoneNumber = phoneNumber;
        this.whatsappId = whatsappId;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getWhatsappId() {
        return whatsappId;
    }
    
    public void setWhatsappId(String whatsappId) {
        this.whatsappId = whatsappId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getTimezone() {
        return timezone;
    }
    
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    public Boolean getOptInStatus() {
        return optInStatus;
    }
    
    public void setOptInStatus(Boolean optInStatus) {
        this.optInStatus = optInStatus;
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
    
    // Convenience methods
    public boolean isOptInStatus() {
        return optInStatus != null && optInStatus;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", whatsappId='" + whatsappId + '\'' +
                ", name='" + name + '\'' +
                ", optInStatus=" + optInStatus +
                '}';
    }
}
