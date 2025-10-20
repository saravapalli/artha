package com.whatsapp.eventservice.service;

import com.whatsapp.eventservice.model.Offer;
import com.whatsapp.eventservice.repository.OfferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing offer data matching OpenAPI specification
 */
@Service
@Transactional
public class OfferService {
    
    private static final Logger logger = LoggerFactory.getLogger(OfferService.class);
    
    @Autowired
    private OfferRepository offerRepository;
    
    /**
     * Get all offers - matches OpenAPI spec
     */
    public List<Offer> getAllOffers() {
        logger.info("🎁 Getting all offers");
        
        List<Offer> offers = offerRepository.getAllOffers();
        logger.info("✅ Retrieved {} offers", offers.size());
        
        return offers;
    }
    
    /**
     * Create a new offer - matches OpenAPI spec
     */
    public Offer createOffer(Offer offer) {
        logger.info("🎁 Creating new offer: {}", offer.getTitle());
        
        offer.setCreatedAt(LocalDateTime.now());
        
        Offer createdOffer = offerRepository.save(offer);
        
        logger.info("✅ Offer created successfully with ID: {}", createdOffer.getId());
        return createdOffer;
    }
}
