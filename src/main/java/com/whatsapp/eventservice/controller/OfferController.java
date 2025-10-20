package com.whatsapp.eventservice.controller;

import com.whatsapp.eventservice.model.Offer;
import com.whatsapp.eventservice.service.OfferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for offer management matching OpenAPI specification
 */
@RestController
@RequestMapping("/offers")
@CrossOrigin(origins = "*")
public class OfferController {
    
    private static final Logger logger = LoggerFactory.getLogger(OfferController.class);
    
    @Autowired
    private OfferService offerService;
    
    /**
     * List all offers - matches OpenAPI spec
     */
    @GetMapping
    public ResponseEntity<List<Offer>> getAllOffers() {
        logger.info("🎁 Getting all offers");
        
        try {
            List<Offer> offers = offerService.getAllOffers();
            logger.info("✅ Retrieved {} offers", offers.size());
            return ResponseEntity.ok(offers);
            
        } catch (Exception e) {
            logger.error("❌ Error getting offers", e);
            return ResponseEntity.status(500).body(null);
        }
    }
    
    /**
     * Create a new offer - matches OpenAPI spec
     */
    @PostMapping
    public ResponseEntity<Offer> createOffer(@RequestBody Offer offer) {
        logger.info("🎁 Creating new offer: {}", offer.getTitle());
        
        try {
            Offer createdOffer = offerService.createOffer(offer);
            logger.info("✅ Offer created successfully with ID: {}", createdOffer.getId());
            return ResponseEntity.ok(createdOffer);
            
        } catch (Exception e) {
            logger.error("❌ Error creating offer", e);
            return ResponseEntity.status(500).body(null);
        }
    }
}
