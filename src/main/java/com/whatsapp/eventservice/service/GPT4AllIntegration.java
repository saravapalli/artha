package com.whatsapp.eventservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * GPT4All Integration for enhanced natural language understanding
 * This class provides integration with GPT4All for more sophisticated query processing
 */
@Service
public class GPT4AllIntegration {
    
    private static final String GPT4ALL_API_URL = System.getenv().getOrDefault("GPT4ALL_API_URL", "http://localhost:8000");
    private static final int TIMEOUT_SECONDS = 30;
    
    @Autowired
    private AIQueryProcessor fallbackProcessor;
    
    /**
     * Enhanced query processing using GPT4All
     */
    public Map<String, Object> processQueryWithGPT4All(String userQuery) {
        try {
            // Try GPT4All first
            Map<String, Object> result = callGPT4AllAPI(userQuery);
            if (result != null && !result.isEmpty()) {
                System.out.println("✅ GPT4All processing successful");
                return result;
            }
        } catch (Exception e) {
            System.err.println("⚠️ GPT4All processing failed, falling back to rule-based: " + e.getMessage());
        }
        
        // Fallback to rule-based processing
        System.out.println("🔄 Using fallback rule-based processing");
        return fallbackProcessor.parseEventQuery(userQuery);
    }
    
    /**
     * Call GPT4All API for query processing
     */
    private Map<String, Object> callGPT4AllAPI(String userQuery) throws Exception {
        String prompt = createPromptForEventQuery(userQuery);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("prompt", prompt);
        requestBody.put("max_tokens", 200);
        requestBody.put("temperature", 0.3);
        requestBody.put("top_p", 0.9);
        
        String response = makeHttpRequest(GPT4ALL_API_URL + "/generate", requestBody);
        
        if (response != null && !response.isEmpty()) {
            return parseGPT4AllResponse(response);
        }
        
        return null;
    }
    
    /**
     * Create a structured prompt for event query processing
     */
    private String createPromptForEventQuery(String userQuery) {
        return String.format("""
            You are an AI assistant that helps users find local events. Parse the following user query and extract relevant information for event search.
            
            User Query: "%s"
            
            Please extract and return ONLY a JSON object with the following structure:
            {
                "intent": "search_events",
                "category": "music|sports|family|art|food|education|entertainment|outdoor|general",
                "subcategory": "specific subcategory if mentioned",
                "date_range": "today|tomorrow|weekend|this_week|next_week|this_month|specific_date",
                "city": "city name if mentioned",
                "location": "specific location or venue if mentioned",
                "price_range": "free|low|medium|high",
                "age_restriction": "all_ages|teens_and_up|adults_only",
                "keywords": ["list", "of", "relevant", "keywords"]
            }
            
            Guidelines:
            - If no specific category is mentioned, use "general"
            - If no date is mentioned, use "upcoming"
            - If no city is mentioned, use "near_me"
            - Extract only information explicitly mentioned or clearly implied
            - Return valid JSON only, no additional text
            
            Response:
            """, userQuery);
    }
    
    /**
     * Parse GPT4All response and extract structured data
     */
    private Map<String, Object> parseGPT4AllResponse(String response) {
        try {
            // Clean the response (remove any extra text before/after JSON)
            String jsonStr = extractJsonFromResponse(response);
            
            // Simple JSON parsing (in production, use a proper JSON library)
            Map<String, Object> result = parseSimpleJson(jsonStr);
            
            // Validate and clean the result
            return validateAndCleanResult(result);
            
        } catch (Exception e) {
            System.err.println("❌ Error parsing GPT4All response: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Extract JSON from GPT4All response
     */
    private String extractJsonFromResponse(String response) {
        // Find JSON object boundaries
        int startIndex = response.indexOf('{');
        int endIndex = response.lastIndexOf('}');
        
        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            return response.substring(startIndex, endIndex + 1);
        }
        
        return response.trim();
    }
    
    /**
     * Simple JSON parser (in production, use Jackson or Gson)
     */
    private Map<String, Object> parseSimpleJson(String jsonStr) {
        Map<String, Object> result = new HashMap<>();
        
        // Remove outer braces
        jsonStr = jsonStr.trim();
        if (jsonStr.startsWith("{") && jsonStr.endsWith("}")) {
            jsonStr = jsonStr.substring(1, jsonStr.length() - 1);
        }
        
        // Split by commas (simplified approach)
        String[] pairs = jsonStr.split(",");
        
        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim().replaceAll("\"", "");
                String value = keyValue[1].trim().replaceAll("\"", "");
                
                // Handle different value types
                if (value.equals("null")) {
                    result.put(key, null);
                } else if (value.startsWith("[") && value.endsWith("]")) {
                    // Handle arrays
                    String arrayContent = value.substring(1, value.length() - 1);
                    if (!arrayContent.trim().isEmpty()) {
                        String[] items = arrayContent.split(",");
                        List<String> list = new ArrayList<>();
                        for (String item : items) {
                            list.add(item.trim().replaceAll("\"", ""));
                        }
                        result.put(key, list);
                    } else {
                        result.put(key, new ArrayList<>());
                    }
                } else {
                    result.put(key, value);
                }
            }
        }
        
        return result;
    }
    
    /**
     * Validate and clean the parsed result
     */
    private Map<String, Object> validateAndCleanResult(Map<String, Object> result) {
        Map<String, Object> cleaned = new HashMap<>();
        
        // Validate category
        String category = (String) result.get("category");
        if (category != null && isValidCategory(category)) {
            cleaned.put("category", category);
        } else {
            cleaned.put("category", "general");
        }
        
        // Validate subcategory
        String subcategory = (String) result.get("subcategory");
        if (subcategory != null && !subcategory.isEmpty()) {
            cleaned.put("subcategory", subcategory);
        }
        
        // Validate date range
        String dateRange = (String) result.get("date_range");
        if (dateRange != null && isValidDateRange(dateRange)) {
            cleaned.put("date_range", dateRange);
        } else {
            cleaned.put("date_range", "upcoming");
        }
        
        // Validate city
        String city = (String) result.get("city");
        if (city != null && !city.isEmpty()) {
            cleaned.put("city", city);
        }
        
        // Validate location
        String location = (String) result.get("location");
        if (location != null && !location.isEmpty()) {
            cleaned.put("location", location);
        }
        
        // Validate price range
        String priceRange = (String) result.get("price_range");
        if (priceRange != null && isValidPriceRange(priceRange)) {
            cleaned.put("price_range", priceRange);
        }
        
        // Validate age restriction
        String ageRestriction = (String) result.get("age_restriction");
        if (ageRestriction != null && isValidAgeRestriction(ageRestriction)) {
            cleaned.put("age_restriction", ageRestriction);
        }
        
        // Add keywords if present
        Object keywords = result.get("keywords");
        if (keywords instanceof List) {
            cleaned.put("keywords", keywords);
        }
        
        return cleaned;
    }
    
    /**
     * Validate category values
     */
    private boolean isValidCategory(String category) {
        String[] validCategories = {
            "music", "sports", "family", "art", "food", "education", 
            "entertainment", "outdoor", "general"
        };
        return Arrays.asList(validCategories).contains(category.toLowerCase());
    }
    
    /**
     * Validate date range values
     */
    private boolean isValidDateRange(String dateRange) {
        String[] validDateRanges = {
            "today", "tomorrow", "weekend", "this_week", "next_week", 
            "this_month", "specific_date", "upcoming"
        };
        return Arrays.asList(validDateRanges).contains(dateRange.toLowerCase());
    }
    
    /**
     * Validate price range values
     */
    private boolean isValidPriceRange(String priceRange) {
        String[] validPriceRanges = {"free", "low", "medium", "high"};
        return Arrays.asList(validPriceRanges).contains(priceRange.toLowerCase());
    }
    
    /**
     * Validate age restriction values
     */
    private boolean isValidAgeRestriction(String ageRestriction) {
        String[] validAgeRestrictions = {"all_ages", "teens_and_up", "adults_only"};
        return Arrays.asList(validAgeRestrictions).contains(ageRestriction.toLowerCase());
    }
    
    /**
     * Make HTTP request to GPT4All API
     */
    private String makeHttpRequest(String url, Map<String, Object> requestBody) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        connection.setConnectTimeout(TIMEOUT_SECONDS * 1000);
        connection.setReadTimeout(TIMEOUT_SECONDS * 1000);
        
        // Convert request body to JSON
        String jsonPayload = convertToJson(requestBody);
        
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        
        int responseCode = connection.getResponseCode();
        
        if (responseCode >= 200 && responseCode < 300) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        } else {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                StringBuilder errorResponse = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    errorResponse.append(line);
                }
                throw new Exception("HTTP " + responseCode + ": " + errorResponse.toString());
            }
        }
    }
    
    /**
     * Convert Map to JSON string (simplified)
     */
    private String convertToJson(Map<String, Object> map) {
        StringBuilder json = new StringBuilder("{");
        
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                json.append(",");
            }
            first = false;
            
            json.append("\"").append(entry.getKey()).append("\":");
            
            Object value = entry.getValue();
            if (value instanceof String) {
                json.append("\"").append(escapeJson((String) value)).append("\"");
            } else if (value instanceof Number) {
                json.append(value);
            } else if (value instanceof Boolean) {
                json.append(value);
            } else {
                json.append("\"").append(escapeJson(value.toString())).append("\"");
            }
        }
        
        json.append("}");
        return json.toString();
    }
    
    /**
     * Escape JSON special characters
     */
    private String escapeJson(String str) {
        return str.replace("\\", "\\\\")
                 .replace("\"", "\\\"")
                 .replace("\n", "\\n")
                 .replace("\r", "\\r")
                 .replace("\t", "\\t");
    }
    
    /**
     * Check if GPT4All service is available
     */
    public boolean isGPT4AllAvailable() {
        try {
            URL url = new URL(GPT4ALL_API_URL + "/health");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            int responseCode = connection.getResponseCode();
            return responseCode >= 200 && responseCode < 300;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get GPT4All service status
     */
    public Map<String, Object> getServiceStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("available", isGPT4AllAvailable());
        status.put("api_url", GPT4ALL_API_URL);
        status.put("fallback_enabled", true);
        
        return status;
    }
}
