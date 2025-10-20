package com.whatsapp.eventservice.repository;

import com.whatsapp.eventservice.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Category repository using JDBC Template - placeholder for now
 */
@Repository
public class CategoryRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    // For now, just return empty lists since categories are not heavily used
    public List<Category> findAll() {
        return List.of();
    }
}