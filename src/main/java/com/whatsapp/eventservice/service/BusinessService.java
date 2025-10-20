package com.whatsapp.eventservice.service;

import com.whatsapp.eventservice.model.Business;
import com.whatsapp.eventservice.repository.BusinessRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing business data matching OpenAPI specification
 */
@Service
@Transactional
public class BusinessService {
    
    private static final Logger logger = LoggerFactory.getLogger(BusinessService.class);
    
    @Autowired
    private BusinessRepository businessRepository;
    
    /**
     * Get all businesses - matches OpenAPI spec
     */
    public List<Business> getAllBusinesses() {
        logger.info("🏢 Getting all businesses");
        
        List<Business> businesses = businessRepository.getAllBusinesses();
        logger.info("✅ Retrieved {} businesses", businesses.size());
        
        return businesses;
    }
    
    /**
     * Create a new business - matches OpenAPI spec
     */
    public Business createBusiness(Business business) {
        logger.info("🏢 Creating new business: {}", business.getName());
        
        business.setCreatedAt(LocalDateTime.now());
        business.setUpdatedAt(LocalDateTime.now());
        
        Business createdBusiness = businessRepository.save(business);
        
        logger.info("✅ Business created successfully with ID: {}", createdBusiness.getId());
        return createdBusiness;
    }
}
