package com.whatsapp.eventservice.controller;

import com.whatsapp.eventservice.model.User;
import com.whatsapp.eventservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for user management matching OpenAPI specification
 */
@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;
    
    /**
     * Create a new user - matches OpenAPI spec
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        logger.info("👤 Creating new user: {}", user.getName());
        
        try {
            User createdUser = userService.createUser(user);
            logger.info("✅ User created successfully with ID: {}", createdUser.getId());
            return ResponseEntity.ok(createdUser);
            
        } catch (Exception e) {
            logger.error("❌ Error creating user", e);
            return ResponseEntity.status(500).body(null);
        }
    }
    
    /**
     * List all users - matches OpenAPI spec
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        logger.info("👥 Getting all users");
        
        try {
            List<User> users = userService.getAllUsers();
            logger.info("✅ Retrieved {} users", users.size());
            return ResponseEntity.ok(users);
            
        } catch (Exception e) {
            logger.error("❌ Error getting all users", e);
            return ResponseEntity.status(500).body(null);
        }
    }
}