package com.whatsapp.eventservice.controller;

import com.whatsapp.eventservice.model.Business;
import com.whatsapp.eventservice.service.BusinessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for business management matching OpenAPI specification
 */
@RestController
@RequestMapping("/businesses")
@CrossOrigin(origins = "*")
public class BusinessController {
    
    private static final Logger logger = LoggerFactory.getLogger(BusinessController.class);
    
    @Autowired
    private BusinessService businessService;
    
    /**
     * List all businesses - matches OpenAPI spec
     */
    @GetMapping
    public ResponseEntity<List<Business>> getAllBusinesses() {
        logger.info("🏢 Getting all businesses");
        
        try {
            List<Business> businesses = businessService.getAllBusinesses();
            logger.info("✅ Retrieved {} businesses", businesses.size());
            return ResponseEntity.ok(businesses);
            
        } catch (Exception e) {
            logger.error("❌ Error getting businesses", e);
            return ResponseEntity.status(500).body(null);
        }
    }
    
    /**
     * Create a new business - matches OpenAPI spec
     */
    @PostMapping
    public ResponseEntity<Business> createBusiness(@RequestBody Business business) {
        logger.info("🏢 Creating new business: {}", business.getName());
        
        try {
            Business createdBusiness = businessService.createBusiness(business);
            logger.info("✅ Business created successfully with ID: {}", createdBusiness.getId());
            return ResponseEntity.ok(createdBusiness);
            
        } catch (Exception e) {
            logger.error("❌ Error creating business", e);
            return ResponseEntity.status(500).body(null);
        }
    }
}
