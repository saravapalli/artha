package com.whatsapp.eventservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * WhatsApp Webhook Payload Model
 * Represents the structure of incoming webhook data from WhatsApp Cloud API
 */
public class WhatsAppWebhookPayload {
    
    @JsonProperty("object")
    private String object;
    
    @JsonProperty("entry")
    private List<WebhookEntry> entry;
    
    // Getters and Setters
    public String getObject() {
        return object;
    }
    
    public void setObject(String object) {
        this.object = object;
    }
    
    public List<WebhookEntry> getEntry() {
        return entry;
    }
    
    public void setEntry(List<WebhookEntry> entry) {
        this.entry = entry;
    }
    
    public static class WebhookEntry {
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("changes")
        private List<WebhookChange> changes;
        
        // Getters and Setters
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public List<WebhookChange> getChanges() {
            return changes;
        }
        
        public void setChanges(List<WebhookChange> changes) {
            this.changes = changes;
        }
    }
    
    public static class WebhookChange {
        @JsonProperty("field")
        private String field;
        
        @JsonProperty("value")
        private WebhookValue value;
        
        // Getters and Setters
        public String getField() {
            return field;
        }
        
        public void setField(String field) {
            this.field = field;
        }
        
        public WebhookValue getValue() {
            return value;
        }
        
        public void setValue(WebhookValue value) {
            this.value = value;
        }
    }
    
    public static class WebhookValue {
        @JsonProperty("messaging_product")
        private String messagingProduct;
        
        @JsonProperty("metadata")
        private Metadata metadata;
        
        @JsonProperty("contacts")
        private List<WhatsAppContact> contacts;
        
        @JsonProperty("messages")
        private List<WhatsAppMessage> messages;
        
        @JsonProperty("statuses")
        private List<MessageStatus> statuses;
        
        // Getters and Setters
        public String getMessagingProduct() {
            return messagingProduct;
        }
        
        public void setMessagingProduct(String messagingProduct) {
            this.messagingProduct = messagingProduct;
        }
        
        public Metadata getMetadata() {
            return metadata;
        }
        
        public void setMetadata(Metadata metadata) {
            this.metadata = metadata;
        }
        
        public List<WhatsAppContact> getContacts() {
            return contacts;
        }
        
        public void setContacts(List<WhatsAppContact> contacts) {
            this.contacts = contacts;
        }
        
        public List<WhatsAppMessage> getMessages() {
            return messages;
        }
        
        public void setMessages(List<WhatsAppMessage> messages) {
            this.messages = messages;
        }
        
        public List<MessageStatus> getStatuses() {
            return statuses;
        }
        
        public void setStatuses(List<MessageStatus> statuses) {
            this.statuses = statuses;
        }
    }
    
    public static class Metadata {
        @JsonProperty("display_phone_number")
        private String displayPhoneNumber;
        
        @JsonProperty("phone_number_id")
        private String phoneNumberId;
        
        // Getters and Setters
        public String getDisplayPhoneNumber() {
            return displayPhoneNumber;
        }
        
        public void setDisplayPhoneNumber(String displayPhoneNumber) {
            this.displayPhoneNumber = displayPhoneNumber;
        }
        
        public String getPhoneNumberId() {
            return phoneNumberId;
        }
        
        public void setPhoneNumberId(String phoneNumberId) {
            this.phoneNumberId = phoneNumberId;
        }
    }
    
    public static class WhatsAppContact {
        @JsonProperty("profile")
        private Profile profile;
        
        @JsonProperty("wa_id")
        private String waId;
        
        // Getters and Setters
        public Profile getProfile() {
            return profile;
        }
        
        public void setProfile(Profile profile) {
            this.profile = profile;
        }
        
        public String getWaId() {
            return waId;
        }
        
        public void setWaId(String waId) {
            this.waId = waId;
        }
    }
    
    public static class Profile {
        @JsonProperty("name")
        private String name;
        
        // Getters and Setters
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
    }
    
    public static class WhatsAppMessage {
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("from")
        private String from;
        
        @JsonProperty("timestamp")
        private String timestamp;
        
        @JsonProperty("type")
        private String type;
        
        @JsonProperty("text")
        private MessageText text;
        
        @JsonProperty("interactive")
        private MessageInteractive interactive;
        
        @JsonProperty("image")
        private MessageImage image;
        
        @JsonProperty("document")
        private MessageDocument document;
        
        @JsonProperty("audio")
        private MessageAudio audio;
        
        @JsonProperty("video")
        private MessageVideo video;
        
        @JsonProperty("sticker")
        private MessageSticker sticker;
        
        @JsonProperty("location")
        private MessageLocation location;
        
        @JsonProperty("contacts")
        private List<MessageContact> contacts;
        
        @JsonProperty("context")
        private MessageContext context;
        
        // Getters and Setters
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getFrom() {
            return from;
        }
        
        public void setFrom(String from) {
            this.from = from;
        }
        
        public String getTimestamp() {
            return timestamp;
        }
        
        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public MessageText getText() {
            return text;
        }
        
        public void setText(MessageText text) {
            this.text = text;
        }
        
        public MessageInteractive getInteractive() {
            return interactive;
        }
        
        public void setInteractive(MessageInteractive interactive) {
            this.interactive = interactive;
        }
        
        public MessageImage getImage() {
            return image;
        }
        
        public void setImage(MessageImage image) {
            this.image = image;
        }
        
        public MessageDocument getDocument() {
            return document;
        }
        
        public void setDocument(MessageDocument document) {
            this.document = document;
        }
        
        public MessageAudio getAudio() {
            return audio;
        }
        
        public void setAudio(MessageAudio audio) {
            this.audio = audio;
        }
        
        public MessageVideo getVideo() {
            return video;
        }
        
        public void setVideo(MessageVideo video) {
            this.video = video;
        }
        
        public MessageSticker getSticker() {
            return sticker;
        }
        
        public void setSticker(MessageSticker sticker) {
            this.sticker = sticker;
        }
        
        public MessageLocation getLocation() {
            return location;
        }
        
        public void setLocation(MessageLocation location) {
            this.location = location;
        }
        
        public List<MessageContact> getContacts() {
            return contacts;
        }
        
        public void setContacts(List<MessageContact> contacts) {
            this.contacts = contacts;
        }
        
        public MessageContext getContext() {
            return context;
        }
        
        public void setContext(MessageContext context) {
            this.context = context;
        }
    }
    
    public static class MessageText {
        @JsonProperty("body")
        private String body;
        
        // Getters and Setters
        public String getBody() {
            return body;
        }
        
        public void setBody(String body) {
            this.body = body;
        }
    }
    
    public static class MessageInteractive {
        @JsonProperty("type")
        private String type;
        
        @JsonProperty("button_reply")
        private ButtonReply buttonReply;
        
        @JsonProperty("list_reply")
        private ListReply listReply;
        
        // Getters and Setters
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public ButtonReply getButtonReply() {
            return buttonReply;
        }
        
        public void setButtonReply(ButtonReply buttonReply) {
            this.buttonReply = buttonReply;
        }
        
        public ListReply getListReply() {
            return listReply;
        }
        
        public void setListReply(ListReply listReply) {
            this.listReply = listReply;
        }
    }
    
    public static class ButtonReply {
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("title")
        private String title;
        
        // Getters and Setters
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
    }
    
    public static class ListReply {
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("title")
        private String title;
        
        @JsonProperty("description")
        private String description;
        
        // Getters and Setters
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
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
    }
    
    public static class MessageImage {
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("mime_type")
        private String mimeType;
        
        @JsonProperty("sha256")
        private String sha256;
        
        @JsonProperty("caption")
        private String caption;
        
        // Getters and Setters
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getMimeType() {
            return mimeType;
        }
        
        public void setMimeType(String mimeType) {
            this.mimeType = mimeType;
        }
        
        public String getSha256() {
            return sha256;
        }
        
        public void setSha256(String sha256) {
            this.sha256 = sha256;
        }
        
        public String getCaption() {
            return caption;
        }
        
        public void setCaption(String caption) {
            this.caption = caption;
        }
    }
    
    public static class MessageDocument {
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("mime_type")
        private String mimeType;
        
        @JsonProperty("sha256")
        private String sha256;
        
        @JsonProperty("filename")
        private String filename;
        
        @JsonProperty("caption")
        private String caption;
        
        // Getters and Setters
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getMimeType() {
            return mimeType;
        }
        
        public void setMimeType(String mimeType) {
            this.mimeType = mimeType;
        }
        
        public String getSha256() {
            return sha256;
        }
        
        public void setSha256(String sha256) {
            this.sha256 = sha256;
        }
        
        public String getFilename() {
            return filename;
        }
        
        public void setFilename(String filename) {
            this.filename = filename;
        }
        
        public String getCaption() {
            return caption;
        }
        
        public void setCaption(String caption) {
            this.caption = caption;
        }
    }
    
    public static class MessageAudio {
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("mime_type")
        private String mimeType;
        
        @JsonProperty("sha256")
        private String sha256;
        
        // Getters and Setters
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getMimeType() {
            return mimeType;
        }
        
        public void setMimeType(String mimeType) {
            this.mimeType = mimeType;
        }
        
        public String getSha256() {
            return sha256;
        }
        
        public void setSha256(String sha256) {
            this.sha256 = sha256;
        }
    }
    
    public static class MessageVideo {
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("mime_type")
        private String mimeType;
        
        @JsonProperty("sha256")
        private String sha256;
        
        @JsonProperty("caption")
        private String caption;
        
        // Getters and Setters
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getMimeType() {
            return mimeType;
        }
        
        public void setMimeType(String mimeType) {
            this.mimeType = mimeType;
        }
        
        public String getSha256() {
            return sha256;
        }
        
        public void setSha256(String sha256) {
            this.sha256 = sha256;
        }
        
        public String getCaption() {
            return caption;
        }
        
        public void setCaption(String caption) {
            this.caption = caption;
        }
    }
    
    public static class MessageSticker {
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("mime_type")
        private String mimeType;
        
        @JsonProperty("sha256")
        private String sha256;
        
        @JsonProperty("animated")
        private boolean animated;
        
        // Getters and Setters
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getMimeType() {
            return mimeType;
        }
        
        public void setMimeType(String mimeType) {
            this.mimeType = mimeType;
        }
        
        public String getSha256() {
            return sha256;
        }
        
        public void setSha256(String sha256) {
            this.sha256 = sha256;
        }
        
        public boolean isAnimated() {
            return animated;
        }
        
        public void setAnimated(boolean animated) {
            this.animated = animated;
        }
    }
    
    public static class MessageLocation {
        @JsonProperty("latitude")
        private double latitude;
        
        @JsonProperty("longitude")
        private double longitude;
        
        @JsonProperty("name")
        private String name;
        
        @JsonProperty("address")
        private String address;
        
        // Getters and Setters
        public double getLatitude() {
            return latitude;
        }
        
        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }
        
        public double getLongitude() {
            return longitude;
        }
        
        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getAddress() {
            return address;
        }
        
        public void setAddress(String address) {
            this.address = address;
        }
    }
    
    public static class MessageContact {
        @JsonProperty("name")
        private ContactName name;
        
        @JsonProperty("phones")
        private List<ContactPhone> phones;
        
        // Getters and Setters
        public ContactName getName() {
            return name;
        }
        
        public void setName(ContactName name) {
            this.name = name;
        }
        
        public List<ContactPhone> getPhones() {
            return phones;
        }
        
        public void setPhones(List<ContactPhone> phones) {
            this.phones = phones;
        }
    }
    
    public static class ContactName {
        @JsonProperty("formatted_name")
        private String formattedName;
        
        @JsonProperty("first_name")
        private String firstName;
        
        @JsonProperty("last_name")
        private String lastName;
        
        // Getters and Setters
        public String getFormattedName() {
            return formattedName;
        }
        
        public void setFormattedName(String formattedName) {
            this.formattedName = formattedName;
        }
        
        public String getFirstName() {
            return firstName;
        }
        
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }
        
        public String getLastName() {
            return lastName;
        }
        
        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
    }
    
    public static class ContactPhone {
        @JsonProperty("phone")
        private String phone;
        
        @JsonProperty("type")
        private String type;
        
        // Getters and Setters
        public String getPhone() {
            return phone;
        }
        
        public void setPhone(String phone) {
            this.phone = phone;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
    }
    
    public static class MessageContext {
        @JsonProperty("from")
        private String from;
        
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("referred_product")
        private ReferredProduct referredProduct;
        
        // Getters and Setters
        public String getFrom() {
            return from;
        }
        
        public void setFrom(String from) {
            this.from = from;
        }
        
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public ReferredProduct getReferredProduct() {
            return referredProduct;
        }
        
        public void setReferredProduct(ReferredProduct referredProduct) {
            this.referredProduct = referredProduct;
        }
    }
    
    public static class ReferredProduct {
        @JsonProperty("catalog_id")
        private String catalogId;
        
        @JsonProperty("product_retailer_id")
        private String productRetailerId;
        
        // Getters and Setters
        public String getCatalogId() {
            return catalogId;
        }
        
        public void setCatalogId(String catalogId) {
            this.catalogId = catalogId;
        }
        
        public String getProductRetailerId() {
            return productRetailerId;
        }
        
        public void setProductRetailerId(String productRetailerId) {
            this.productRetailerId = productRetailerId;
        }
    }
    
    public static class MessageStatus {
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("status")
        private String status;
        
        @JsonProperty("timestamp")
        private String timestamp;
        
        @JsonProperty("recipient_id")
        private String recipientId;
        
        @JsonProperty("conversation")
        private Conversation conversation;
        
        @JsonProperty("pricing")
        private Pricing pricing;
        
        // Getters and Setters
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        public String getTimestamp() {
            return timestamp;
        }
        
        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
        
        public String getRecipientId() {
            return recipientId;
        }
        
        public void setRecipientId(String recipientId) {
            this.recipientId = recipientId;
        }
        
        public Conversation getConversation() {
            return conversation;
        }
        
        public void setConversation(Conversation conversation) {
            this.conversation = conversation;
        }
        
        public Pricing getPricing() {
            return pricing;
        }
        
        public void setPricing(Pricing pricing) {
            this.pricing = pricing;
        }
    }
    
    public static class Conversation {
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("category")
        private String category;
        
        @JsonProperty("expiration_timestamp")
        private String expirationTimestamp;
        
        @JsonProperty("origin")
        private Origin origin;
        
        // Getters and Setters
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getCategory() {
            return category;
        }
        
        public void setCategory(String category) {
            this.category = category;
        }
        
        public String getExpirationTimestamp() {
            return expirationTimestamp;
        }
        
        public void setExpirationTimestamp(String expirationTimestamp) {
            this.expirationTimestamp = expirationTimestamp;
        }
        
        public Origin getOrigin() {
            return origin;
        }
        
        public void setOrigin(Origin origin) {
            this.origin = origin;
        }
    }
    
    public static class Origin {
        @JsonProperty("type")
        private String type;
        
        // Getters and Setters
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
    }
    
    public static class Pricing {
        @JsonProperty("billable")
        private boolean billable;
        
        @JsonProperty("pricing_model")
        private String pricingModel;
        
        @JsonProperty("category")
        private String category;
        
        // Getters and Setters
        public boolean isBillable() {
            return billable;
        }
        
        public void setBillable(boolean billable) {
            this.billable = billable;
        }
        
        public String getPricingModel() {
            return pricingModel;
        }
        
        public void setPricingModel(String pricingModel) {
            this.pricingModel = pricingModel;
        }
        
        public String getCategory() {
            return category;
        }
        
        public void setCategory(String category) {
            this.category = category;
        }
    }
}
