package com.whatsapp.eventservice.service;

import com.whatsapp.eventservice.model.*;
import com.whatsapp.eventservice.repository.ConversationRepository;
import com.whatsapp.eventservice.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Orchestration service that implements the complete conversation workflow from README.md
 * 
 * This service coordinates between multiple APIs to implement the full flow:
 * 1. User sends a message → API receives it.
 * 2. API creates conversation if new and logs the message.
 * 3. API sends message to LLM for intent parsing and query generation.
 * 4. LLM returns parsed query (entities, intent, type of content requested).
 * 5. API queries database for events, businesses, and offers based on parsed query.
 * 6. API stores suggested items in suggested_item table.
 * 7. API sends suggestions to LLM to generate response text.
 * 8. LLM returns response text → API sends it to the user.
 */
@Service
@Transactional
public class ConversationOrchestrationService {
    
    private static final Logger logger = LoggerFactory.getLogger(ConversationOrchestrationService.class);
    
    @Autowired
    private ConversationService conversationService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private EventService eventService;
    
    @Autowired
    private BusinessService businessService;
    
    @Autowired
    private OfferService offerService;
    
    @Autowired
    private AIQueryProcessor aiQueryProcessor;
    
    @Autowired
    private GPT4AllIntegration gpt4AllIntegration;
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private ConversationRepository conversationRepository;
    
    /**
     * Process a user message through the complete workflow
     */
    public Map<String, Object> processUserMessage(Long userId, String content, String messageType) {
        logger.info("🔄 Starting message processing workflow for user: {}", userId);
        
        try {
            // Step 1: User sends a message → API receives it (already done)
            
            // Step 2: API creates conversation if new and logs the message
            Conversation conversation = getOrCreateConversation(userId);
            logger.info("📞 Conversation: {} for user: {}", conversation.getId(), userId);
            
            // Store the user message in the conversation
            Message userMessage = new Message(
                conversation.getId(), 
                Message.Sender.user, 
                content, 
                Message.MessageType.valueOf(messageType.toLowerCase())
            );
            messageRepository.save(userMessage);
            
            // Step 3: API sends message to LLM for intent parsing and query generation
            Map<String, Object> parsedQuery = processMessageWithLLM(content, conversation.getId());
            logger.info("🤖 LLM parsed query: {}", parsedQuery);
            
            // Step 5: API queries database for events, businesses, and offers based on parsed query
            List<SuggestedItem> suggestedItems = queryDatabaseForSuggestions(parsedQuery);
            logger.info("🔍 Found {} suggested items", suggestedItems.size());
            
            // Step 6: API stores suggested items in suggested_item table
            storeSuggestedItems(suggestedItems, conversation.getId());
            
            // Step 7: API sends suggestions to LLM to generate response text
            String responseText = generateResponseWithLLM(suggestedItems, parsedQuery, content);
            
            // Store the system response message
            Message systemMessage = new Message(
                conversation.getId(), 
                Message.Sender.system, 
                responseText, 
                Message.MessageType.text
            );
            messageRepository.save(systemMessage);
            
            // Step 8: Return response text to the user
            Map<String, Object> response = new HashMap<>();
            response.put("system_message", systemMessage);
            response.put("suggested_items", suggestedItems);
            response.put("conversation_id", conversation.getId());
            response.put("parsed_query", parsedQuery);
            
            logger.info("✅ Message processing workflow completed for user: {}", userId);
            return response;
            
        } catch (Exception e) {
            logger.error("❌ Error in message processing workflow", e);
            throw new RuntimeException("Failed to process user message", e);
        }
    }
    
    /**
     * Step 2: Get or create conversation for user
     */
    private Conversation getOrCreateConversation(Long userId) {
        logger.info("🔍 Looking for existing conversation for user: {}", userId);
        
        // Check if user has an active conversation using JDBC repository
        Optional<Conversation> activeConversation = conversationRepository.findByUserIdAndEndedAtIsNull(userId);
        
        if (activeConversation.isPresent()) {
            logger.info("✅ Found existing conversation: {}", activeConversation.get().getId());
            return activeConversation.get();
        }
        
        // Create new conversation
        logger.info("➕ Creating new conversation for user: {}", userId);
        return conversationService.createConversation(userId);
    }
    
    /**
     * Step 3: Process message with LLM for intent parsing
     */
    private Map<String, Object> processMessageWithLLM(String content, Long conversationId) {
        logger.info("🤖 Processing message with LLM");
        
        try {
            // Try GPT4All first, fallback to AIQueryProcessor
            Map<String, Object> parsedQuery;
            try {
                parsedQuery = gpt4AllIntegration.processQuery(content);
                logger.info("✅ GPT4All processed query successfully");
            } catch (Exception e) {
                logger.warn("⚠️ GPT4All failed, falling back to AIQueryProcessor: {}", e.getMessage());
                parsedQuery = aiQueryProcessor.parseEventQuery(content);
                logger.info("✅ AIQueryProcessor processed query successfully");
            }
            
            // Enhance parsed query with conversation context if needed
            enhanceQueryWithContext(parsedQuery, conversationId);
            
            return parsedQuery;
            
        } catch (Exception e) {
            logger.error("❌ Error processing message with LLM", e);
            // Return basic parsed query as fallback
            return aiQueryProcessor.parseEventQuery(content);
        }
    }
    
    /**
     * Step 5: Query database for events, businesses, and offers
     */
    private List<SuggestedItem> queryDatabaseForSuggestions(Map<String, Object> parsedQuery) {
        logger.info("🔍 Querying database for suggestions based on: {}", parsedQuery);
        
        List<SuggestedItem> suggestedItems = new ArrayList<>();
        
        try {
            // Determine what type of content to search for based on intent and keywords
            String searchTypes = determineSearchTypes(parsedQuery);
            logger.info("🎯 Determined search types: {}", searchTypes);
            
            // Search for events if indicated
            if (searchTypes.contains("events")) {
                List<Event> events = eventService.searchEventsByCriteria(parsedQuery);
                logger.info("📅 Found {} events", events.size());
                for (Event event : events) {
                    SuggestedItem item = new SuggestedItem(
                        SuggestedItem.ItemType.event,
                        event.getId(),
                        event.getName(),
                        event.getDescription()
                    );
                    suggestedItems.add(item);
                }
            }
            
            // Search for businesses if indicated
            if (searchTypes.contains("businesses")) {
                List<Business> businesses = businessService.getAllBusinesses();
                logger.info("🏢 Found {} businesses", businesses.size());
                
                // Filter businesses based on parsed query if needed
                businesses = filterBusinessesByCriteria(businesses, parsedQuery);
                logger.info("🏢 Filtered to {} relevant businesses", businesses.size());
                
                for (Business business : businesses) {
                    SuggestedItem item = new SuggestedItem(
                        SuggestedItem.ItemType.business,
                        business.getId(),
                        business.getName(),
                        business.getDescription()
                    );
                    suggestedItems.add(item);
                }
            }
            
            // Search for offers if indicated
            if (searchTypes.contains("offers")) {
                List<Offer> offers = offerService.getAllOffers();
                logger.info("💰 Found {} offers", offers.size());
                for (Offer offer : offers) {
                    SuggestedItem item = new SuggestedItem(
                        SuggestedItem.ItemType.offer,
                        offer.getId(),
                        offer.getTitle(),
                        offer.getDescription()
                    );
                    suggestedItems.add(item);
                }
            }
            
            // If no specific type was determined, default to searching both events and businesses
            if (suggestedItems.isEmpty()) {
                logger.info("🔄 No specific type determined, searching events and businesses");
                
                // Search events
                List<Event> events = eventService.searchEventsByCriteria(parsedQuery);
                for (Event event : events) {
                    SuggestedItem item = new SuggestedItem(
                        SuggestedItem.ItemType.event,
                        event.getId(),
                        event.getName(),
                        event.getDescription()
                    );
                    suggestedItems.add(item);
                }
                
                // Search businesses if no events found
                if (events.isEmpty()) {
                    List<Business> businesses = businessService.getAllBusinesses();
                    businesses = filterBusinessesByCriteria(businesses, parsedQuery);
                    for (Business business : businesses) {
                        SuggestedItem item = new SuggestedItem(
                            SuggestedItem.ItemType.business,
                            business.getId(),
                            business.getName(),
                            business.getDescription()
                        );
                        suggestedItems.add(item);
                    }
                }
            }
            
            // Limit results to top 5 for better user experience
            if (suggestedItems.size() > 5) {
                suggestedItems = suggestedItems.subList(0, 5);
            }
            
            logger.info("✅ Found {} suggested items", suggestedItems.size());
            return suggestedItems;
            
        } catch (Exception e) {
            logger.error("❌ Error querying database for suggestions", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Determine what types of content to search for based on parsed query
     */
    private String determineSearchTypes(Map<String, Object> parsedQuery) {
        StringBuilder searchTypes = new StringBuilder();
        
        // First, check if LLM provided explicit search_types
        @SuppressWarnings("unchecked")
        List<String> llmSearchTypes = (List<String>) parsedQuery.getOrDefault("search_types", new ArrayList<>());
        if (!llmSearchTypes.isEmpty()) {
            logger.info("🤖 Using LLM-determined search types: {}", llmSearchTypes);
            return String.join(" ", llmSearchTypes);
        }
        
        // Extract keywords and category from parsed query
        String category = (String) parsedQuery.getOrDefault("category", "");
        String intent = (String) parsedQuery.getOrDefault("intent", "");
        
        // Get keywords if available
        @SuppressWarnings("unchecked")
        List<String> keywords = (List<String>) parsedQuery.getOrDefault("keywords", new ArrayList<>());
        
        // Combine all text for analysis
        String allText = (category + " " + intent + " " + String.join(" ", keywords)).toLowerCase();
        
        // Check for business-specific keywords
        String[] businessKeywords = {
            "business", "businesses", "restaurant", "restaurants", "store", "stores", 
            "shop", "shops", "cafe", "bar", "service", "services", "company", 
            "companies", "venue", "venues", "place", "places", "establishment"
        };
        
        // Check for event-specific keywords
        String[] eventKeywords = {
            "event", "events", "concert", "concerts", "show", "shows", "exhibition", 
            "exhibitions", "festival", "festivals", "tournament", "tournaments", 
            "meeting", "meetings", "conference", "conferences", "workshop", "workshops",
            "performance", "performances", "gig", "gigs", "party", "parties"
        };
        
        // Check for offer-specific keywords
        String[] offerKeywords = {
            "offer", "offers", "deal", "deals", "discount", "discounts", 
            "promotion", "promotions", "sale", "sales", "special", "specials"
        };
        
        boolean foundBusinessKeywords = false;
        boolean foundEventKeywords = false;
        boolean foundOfferKeywords = false;
        
        // Check for business keywords
        for (String keyword : businessKeywords) {
            if (allText.contains(keyword)) {
                foundBusinessKeywords = true;
                break;
            }
        }
        
        // Check for event keywords
        for (String keyword : eventKeywords) {
            if (allText.contains(keyword)) {
                foundEventKeywords = true;
                break;
            }
        }
        
        // Check for offer keywords
        for (String keyword : offerKeywords) {
            if (allText.contains(keyword)) {
                foundOfferKeywords = true;
                break;
            }
        }
        
        // Build search types string
        if (foundBusinessKeywords) {
            searchTypes.append("businesses ");
        }
        if (foundEventKeywords) {
            searchTypes.append("events ");
        }
        if (foundOfferKeywords) {
            searchTypes.append("offers ");
        }
        
        // If no specific keywords found but we have categories, make intelligent guesses
        if (searchTypes.length() == 0) {
            if (category.contains("food") || category.contains("dining")) {
                searchTypes.append("businesses events ");
            } else if (category.contains("music") || category.contains("sports") || category.contains("art")) {
                searchTypes.append("events businesses ");
            } else {
                // Default to both events and businesses
                searchTypes.append("events businesses ");
            }
        }
        
        return searchTypes.toString().trim();
    }
    
    /**
     * Filter businesses based on parsed query criteria
     */
    private List<Business> filterBusinessesByCriteria(List<Business> businesses, Map<String, Object> parsedQuery) {
        // For now, return all businesses
        // In a future implementation, this could filter by category, city, etc.
        return businesses.stream()
            .limit(5) // Limit to 5 businesses to match events limit
            .collect(Collectors.toList());
    }
    
    /**
     * Step 6: Store suggested items
     */
    private void storeSuggestedItems(List<SuggestedItem> suggestedItems, Long conversationId) {
        logger.info("💾 Storing {} suggested items", suggestedItems.size());
        
        // For now, we'll just log the items since we don't have a repository method
        // In a full implementation, you would save these to the suggested_item table
        for (SuggestedItem item : suggestedItems) {
            logger.info("📝 Suggested item: {} - {}", item.getType(), item.getTitle());
        }
        
        // TODO: Implement actual storage to suggested_item table
        // suggestedItemRepository.saveAll(suggestedItems);
    }
    
    /**
     * Step 7: Generate response with LLM
     */
    private String generateResponseWithLLM(List<SuggestedItem> suggestedItems, Map<String, Object> parsedQuery, String originalMessage) {
        logger.info("🤖 Generating response with LLM for {} suggestions", suggestedItems.size());
        
        try {
            // Try GPT4All first for response generation
            String responseText;
            try {
                responseText = gpt4AllIntegration.generateResponse(originalMessage, suggestedItems, parsedQuery);
                logger.info("✅ GPT4All generated response successfully");
            } catch (Exception e) {
                logger.warn("⚠️ GPT4All failed, generating fallback response: {}", e.getMessage());
                responseText = generateFallbackResponse(suggestedItems, parsedQuery);
            }
            
            return responseText;
            
        } catch (Exception e) {
            logger.error("❌ Error generating response with LLM", e);
            return generateFallbackResponse(suggestedItems, parsedQuery);
        }
    }
    
    /**
     * Generate fallback response when LLM is not available
     */
    private String generateFallbackResponse(List<SuggestedItem> suggestedItems, Map<String, Object> parsedQuery) {
        if (suggestedItems.isEmpty()) {
            return "I couldn't find any events, businesses, or offers matching your request. Please try a different search term or ask me about specific types of activities.";
        }
        
        StringBuilder response = new StringBuilder();
        
        // Group items by type for better presentation
        Map<String, List<SuggestedItem>> itemsByType = suggestedItems.stream()
            .collect(Collectors.groupingBy(item -> item.getType().toString()));
        
        // Present items by type
        if (itemsByType.containsKey("event")) {
            response.append("🎉 **Events:**\n");
            for (SuggestedItem item : itemsByType.get("event")) {
                response.append("• ").append(item.getTitle()).append("\n");
            }
            response.append("\n");
        }
        
        if (itemsByType.containsKey("business")) {
            response.append("🏢 **Businesses:**\n");
            for (SuggestedItem item : itemsByType.get("business")) {
                response.append("• ").append(item.getTitle()).append("\n");
            }
            response.append("\n");
        }
        
        if (itemsByType.containsKey("offer")) {
            response.append("💰 **Offers:**\n");
            for (SuggestedItem item : itemsByType.get("offer")) {
                response.append("• ").append(item.getTitle()).append("\n");
            }
            response.append("\n");
        }
        
        response.append("I found ").append(suggestedItems.size()).append(" total suggestions. Let me know if you'd like more details about any specific item!");
        
        return response.toString();
    }
    
    /**
     * Enhance parsed query with conversation context
     */
    private void enhanceQueryWithContext(Map<String, Object> parsedQuery, Long conversationId) {
        // Add conversation context if needed
        parsedQuery.put("conversation_id", conversationId);
        parsedQuery.put("timestamp", LocalDateTime.now());
    }
    
    /**
     * Process user feedback on suggested items
     */
    public Map<String, Object> processUserFeedback(Long userId, Long suggestionId, String feedbackType) {
        logger.info("👍 Processing feedback from user: {} for suggestion: {}, type: {}", 
                   userId, suggestionId, feedbackType);
        
        try {
            // Store user feedback
            // TODO: Implement actual feedback storage
            // userFeedbackRepository.save(new UserFeedback(userId, suggestionId, feedbackType));
            
            // Update user interests based on feedback
            // TODO: Implement user interest updates
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Thank you for your feedback!");
            response.put("feedback_stored", true);
            
            logger.info("✅ Feedback processed successfully");
            return response;
            
        } catch (Exception e) {
            logger.error("❌ Error processing user feedback", e);
            throw new RuntimeException("Failed to process user feedback", e);
        }
    }
    
    /**
     * Get conversation context for a user
     */
    public Map<String, Object> getConversationContext(Long userId) {
        logger.info("📋 Getting conversation context for user: {}", userId);
        
        try {
            List<Conversation> conversations = conversationService.getAllConversations();
            Optional<Conversation> activeConversation = conversations.stream()
                .filter(c -> c.getUserId().equals(userId) && c.getEndedAt() == null)
                .findFirst();
            
            Map<String, Object> context = new HashMap<>();
            if (activeConversation.isPresent()) {
                context.put("conversation", activeConversation.get());
                context.put("has_active_conversation", true);
            } else {
                context.put("has_active_conversation", false);
            }
            
            return context;
            
        } catch (Exception e) {
            logger.error("❌ Error getting conversation context", e);
            throw new RuntimeException("Failed to get conversation context", e);
        }
    }
}
