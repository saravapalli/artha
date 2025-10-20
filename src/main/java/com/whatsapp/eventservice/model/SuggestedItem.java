package com.whatsapp.eventservice.model;

/**
 * SuggestedItem entity matching OpenAPI specification - JDBC Template version
 */
public class SuggestedItem {
    
    private Long id;
    private ItemType type;
    private Long itemId;
    private String title;
    private String description;
    private String link;
    
    public enum ItemType {
        event, business, offer
    }
    
    // Constructors
    public SuggestedItem() {}
    
    public SuggestedItem(ItemType type, Long itemId, String title, String description) {
        this.type = type;
        this.itemId = itemId;
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
    
    public ItemType getType() {
        return type;
    }
    
    public void setType(ItemType type) {
        this.type = type;
    }
    
    public Long getItemId() {
        return itemId;
    }
    
    public void setItemId(Long itemId) {
        this.itemId = itemId;
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
    
    public String getLink() {
        return link;
    }
    
    public void setLink(String link) {
        this.link = link;
    }
    
    @Override
    public String toString() {
        return "SuggestedItem{" +
                "id=" + id +
                ", type=" + type +
                ", itemId=" + itemId +
                ", title='" + title + '\'' +
                '}';
    }
}
