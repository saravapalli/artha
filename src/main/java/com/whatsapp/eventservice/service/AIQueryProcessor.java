package com.whatsapp.eventservice.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * AI Query Processor for understanding natural language event queries
 * Uses pattern matching and keyword extraction to parse user intent
 * Can be enhanced with actual LLM integration (GPT4All, OpenAI, etc.)
 */
@Service
public class AIQueryProcessor {
    
    // Common event categories and their synonyms
    private static final Map<String, List<String>> CATEGORY_SYNONYMS = new HashMap<>();
    private static final Map<String, List<String>> DATE_PATTERNS = new HashMap<>();
    private static final Map<String, List<String>> LOCATION_INDICATORS = new HashMap<>();
    
    static {
        // Initialize category synonyms
        CATEGORY_SYNONYMS.put("music", Arrays.asList("music", "concert", "band", "live music", "jazz", "rock", "pop", "classical", "gig", "performance"));
        CATEGORY_SYNONYMS.put("sports", Arrays.asList("sports", "game", "match", "tournament", "football", "basketball", "soccer", "baseball", "tennis", "golf"));
        CATEGORY_SYNONYMS.put("family", Arrays.asList("family", "family-friendly", "kids", "children", "family fun", "kid-friendly", "all ages"));
        CATEGORY_SYNONYMS.put("art", Arrays.asList("art", "art gallery", "exhibition", "museum", "painting", "sculpture", "gallery", "art show"));
        CATEGORY_SYNONYMS.put("food", Arrays.asList("food", "restaurant", "dining", "food festival", "taste", "culinary", "cooking", "wine"));
        CATEGORY_SYNONYMS.put("education", Arrays.asList("education", "workshop", "seminar", "class", "learning", "training", "course", "lecture"));
        CATEGORY_SYNONYMS.put("entertainment", Arrays.asList("entertainment", "show", "comedy", "theater", "drama", "movie", "film", "cinema"));
        CATEGORY_SYNONYMS.put("outdoor", Arrays.asList("outdoor", "hiking", "nature", "park", "beach", "camping", "outdoor activity"));
        
        // Initialize date patterns
        DATE_PATTERNS.put("today", Arrays.asList("today", "tonight", "this evening"));
        DATE_PATTERNS.put("tomorrow", Arrays.asList("tomorrow", "tomorrow night"));
        DATE_PATTERNS.put("weekend", Arrays.asList("weekend", "this weekend", "saturday", "sunday", "sat", "sun"));
        DATE_PATTERNS.put("this_week", Arrays.asList("this week", "this week", "week"));
        DATE_PATTERNS.put("next_week", Arrays.asList("next week", "next week"));
        DATE_PATTERNS.put("this_month", Arrays.asList("this month", "month"));
        
        // Initialize location indicators
        LOCATION_INDICATORS.put("near_me", Arrays.asList("near me", "close to me", "local", "nearby", "around here"));
        LOCATION_INDICATORS.put("downtown", Arrays.asList("downtown", "city center", "center city"));
        LOCATION_INDICATORS.put("specific_venue", Arrays.asList("at", "in", "venue", "location"));
    }
    
    /**
     * Parse natural language query to extract event search criteria
     */
    public Map<String, Object> parseEventQuery(String query) {
        Map<String, Object> criteria = new HashMap<>();
        
        String lowerQuery = query.toLowerCase().trim();
        
        // Extract category
        String category = extractCategory(lowerQuery);
        if (category != null) {
            criteria.put("category", category);
        }
        
        // Extract subcategory
        String subcategory = extractSubcategory(lowerQuery, category);
        if (subcategory != null) {
            criteria.put("subcategory", subcategory);
        }
        
        // Extract date range
        String dateRange = extractDateRange(lowerQuery);
        if (dateRange != null) {
            criteria.put("date_range", dateRange);
        }
        
        // Extract location
        String location = extractLocation(lowerQuery);
        if (location != null) {
            criteria.put("city", location);
        }
        
        // Extract price range
        String priceRange = extractPriceRange(lowerQuery);
        if (priceRange != null) {
            criteria.put("price_range", priceRange);
        }
        
        // Extract age restriction
        String ageRestriction = extractAgeRestriction(lowerQuery);
        if (ageRestriction != null) {
            criteria.put("age_restriction", ageRestriction);
        }
        
        // Set default values if no specific criteria found
        if (criteria.isEmpty()) {
            criteria.put("category", "general");
            criteria.put("date_range", "upcoming");
        }
        
        return criteria;
    }
    
    /**
     * Extract event category from query
     */
    private String extractCategory(String query) {
        for (Map.Entry<String, List<String>> entry : CATEGORY_SYNONYMS.entrySet()) {
            String category = entry.getKey();
            List<String> synonyms = entry.getValue();
            
            for (String synonym : synonyms) {
                if (query.contains(synonym)) {
                    return category;
                }
            }
        }
        return null;
    }
    
    /**
     * Extract subcategory from query
     */
    private String extractSubcategory(String query, String category) {
        if (category == null) return null;
        
        // Music subcategories
        if ("music".equals(category)) {
            if (query.contains("jazz")) return "jazz";
            if (query.contains("rock")) return "rock";
            if (query.contains("pop")) return "pop";
            if (query.contains("classical")) return "classical";
            if (query.contains("country")) return "country";
            if (query.contains("hip hop") || query.contains("rap")) return "hip_hop";
        }
        
        // Sports subcategories
        if ("sports".equals(category)) {
            if (query.contains("football")) return "football";
            if (query.contains("basketball")) return "basketball";
            if (query.contains("soccer")) return "soccer";
            if (query.contains("baseball")) return "baseball";
            if (query.contains("tennis")) return "tennis";
            if (query.contains("golf")) return "golf";
        }
        
        // Art subcategories
        if ("art".equals(category)) {
            if (query.contains("painting")) return "painting";
            if (query.contains("sculpture")) return "sculpture";
            if (query.contains("photography")) return "photography";
            if (query.contains("digital art")) return "digital_art";
        }
        
        return null;
    }
    
    /**
     * Extract date range from query
     */
    private String extractDateRange(String query) {
        for (Map.Entry<String, List<String>> entry : DATE_PATTERNS.entrySet()) {
            String dateRange = entry.getKey();
            List<String> patterns = entry.getValue();
            
            for (String pattern : patterns) {
                if (query.contains(pattern)) {
                    return dateRange;
                }
            }
        }
        
        // Check for specific dates (simplified)
        Pattern datePattern = Pattern.compile("\\b(\\d{1,2})[/-](\\d{1,2})[/-]?(\\d{2,4})?\\b");
        Matcher matcher = datePattern.matcher(query);
        if (matcher.find()) {
            return "specific_date";
        }
        
        return null;
    }
    
    /**
     * Extract location from query
     */
    private String extractLocation(String query) {
        // Check for "near me" indicators
        for (String indicator : LOCATION_INDICATORS.get("near_me")) {
            if (query.contains(indicator)) {
                return "near_me";
            }
        }
        
        // Check for downtown
        for (String indicator : LOCATION_INDICATORS.get("downtown")) {
            if (query.contains(indicator)) {
                return "downtown";
            }
        }
        
        // Extract city names (simplified - in production, use a city database)
        String[] commonCities = {"boston", "new york", "los angeles", "chicago", "houston", "phoenix", 
                               "philadelphia", "san antonio", "san diego", "dallas", "san jose", 
                               "austin", "jacksonville", "fort worth", "columbus", "charlotte", 
                               "san francisco", "indianapolis", "seattle", "denver", "washington", 
                               "boston", "el paso", "nashville", "detroit", "oklahoma city", "portland", 
                               "las vegas", "memphis", "louisville", "baltimore", "milwaukee", "albuquerque"};
        
        for (String city : commonCities) {
            if (query.contains(city)) {
                return city;
            }
        }
        
        // Extract venue names (simplified)
        Pattern venuePattern = Pattern.compile("\\b(at|in)\\s+([A-Za-z\\s]+?)(?:\\s|$)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = venuePattern.matcher(query);
        if (matcher.find()) {
            return matcher.group(2).trim();
        }
        
        return null;
    }
    
    /**
     * Extract price range from query
     */
    private String extractPriceRange(String query) {
        if (query.contains("free") || query.contains("no cost")) {
            return "free";
        }
        
        if (query.contains("cheap") || query.contains("affordable") || query.contains("budget")) {
            return "low";
        }
        
        if (query.contains("expensive") || query.contains("premium") || query.contains("luxury")) {
            return "high";
        }
        
        // Extract specific price ranges
        Pattern pricePattern = Pattern.compile("\\$?(\\d+)(?:-|\\s+to\\s+)\\$?(\\d+)");
        Matcher matcher = pricePattern.matcher(query);
        if (matcher.find()) {
            int low = Integer.parseInt(matcher.group(1));
            int high = Integer.parseInt(matcher.group(2));
            if (high <= 25) return "low";
            if (high <= 75) return "medium";
            return "high";
        }
        
        // Extract single price
        Pattern singlePricePattern = Pattern.compile("\\$?(\\d+)\\s*(?:or\\s+)?(?:under|below|less\\s+than)");
        Matcher singleMatcher = singlePricePattern.matcher(query);
        if (singleMatcher.find()) {
            int price = Integer.parseInt(singleMatcher.group(1));
            if (price <= 25) return "low";
            if (price <= 75) return "medium";
            return "high";
        }
        
        return null;
    }
    
    /**
     * Extract age restriction from query
     */
    private String extractAgeRestriction(String query) {
        if (query.contains("family") || query.contains("kids") || query.contains("children") || 
            query.contains("all ages") || query.contains("family-friendly")) {
            return "all_ages";
        }
        
        if (query.contains("adult") || query.contains("18+") || query.contains("21+")) {
            return "adults_only";
        }
        
        if (query.contains("teen") || query.contains("teenager") || query.contains("13+")) {
            return "teens_and_up";
        }
        
        return null;
    }
    
    /**
     * Generate a natural language response based on search criteria
     */
    public String generateSearchSummary(Map<String, Object> criteria) {
        StringBuilder summary = new StringBuilder("Searching for ");
        
        List<String> criteriaList = new ArrayList<>();
        
        if (criteria.containsKey("category")) {
            criteriaList.add(criteria.get("category") + " events");
        }
        
        if (criteria.containsKey("date_range")) {
            criteriaList.add("happening " + criteria.get("date_range"));
        }
        
        if (criteria.containsKey("city")) {
            criteriaList.add("in " + criteria.get("city"));
        }
        
        if (criteria.containsKey("price_range")) {
            criteriaList.add("with " + criteria.get("price_range") + " pricing");
        }
        
        if (criteriaList.isEmpty()) {
            return "Searching for upcoming events";
        }
        
        for (int i = 0; i < criteriaList.size(); i++) {
            if (i > 0) {
                if (i == criteriaList.size() - 1) {
                    summary.append(" and ");
                } else {
                    summary.append(", ");
                }
            }
            summary.append(criteriaList.get(i));
        }
        
        return summary.toString();
    }
    
    /**
     * Check if query is a greeting or help request
     */
    public boolean isGreetingOrHelp(String query) {
        String lowerQuery = query.toLowerCase().trim();
        
        String[] greetings = {"hi", "hello", "hey", "good morning", "good afternoon", "good evening"};
        String[] helpWords = {"help", "what can you do", "how does this work", "commands", "options"};
        
        for (String greeting : greetings) {
            if (lowerQuery.contains(greeting)) {
                return true;
            }
        }
        
        for (String help : helpWords) {
            if (lowerQuery.contains(help)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Generate help message
     */
    public String generateHelpMessage() {
        return """
            🎉 Welcome to the Local Events Assistant!
            
            I can help you find events in your area. Here's how to use me:
            
            📅 **Ask about events by time:**
            • "What's happening this weekend?"
            • "Any events tonight?"
            • "Show me events this week"
            
            🎵 **Ask about specific categories:**
            • "Music events this weekend"
            • "Family-friendly activities"
            • "Art exhibitions"
            • "Sports games"
            • "Food festivals"
            
            📍 **Ask about locations:**
            • "Events in Boston"
            • "What's happening downtown?"
            • "Local events near me"
            
            💰 **Ask about pricing:**
            • "Free events this weekend"
            • "Cheap activities for families"
            
            Just ask me naturally - I understand conversational language!
            """;
    }
}