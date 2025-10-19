package com.whatsapp.eventservice.repository;

import com.whatsapp.eventservice.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for Event entity
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    /**
     * Find events by city and active status
     * 
     * @param city City name
     * @return List of active events in the city
     */
    List<Event> findByCityAndActiveTrue(String city);
    
    /**
     * Find events by category and active status
     * 
     * @param category Event category
     * @return List of active events in the category
     */
    List<Event> findByCategoryAndActiveTrue(String category);
    
    /**
     * Find events by city and category and active status
     * 
     * @param city City name
     * @param category Event category
     * @return List of active events in the city and category
     */
    List<Event> findByCityAndCategoryAndActiveTrue(String city, String category);
    
    /**
     * Find events by active status
     * 
     * @return List of active events
     */
    List<Event> findByActiveTrue();
    
    /**
     * Find upcoming events (start time after now)
     * 
     * @param now Current time
     * @return List of upcoming events
     */
    List<Event> findByStartTimeAfterAndActiveTrue(LocalDateTime now);
    
    /**
     * Find upcoming events in a specific city
     * 
     * @param city City name
     * @param now Current time
     * @return List of upcoming events in the city
     */
    List<Event> findByCityAndStartTimeAfterAndActiveTrue(String city, LocalDateTime now);
    
    /**
     * Find events by date range
     * 
     * @param startDate Start date
     * @param endDate End date
     * @return List of events in the date range
     */
    List<Event> findByStartTimeBetweenAndActiveTrue(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find events by price range
     * 
     * @param priceRange Price range
     * @return List of events with the price range
     */
    List<Event> findByPriceRangeAndActiveTrue(String priceRange);
    
    /**
     * Find events by age restriction
     * 
     * @param ageRestriction Age restriction
     * @return List of events with the age restriction
     */
    List<Event> findByAgeRestrictionAndActiveTrue(String ageRestriction);
    
    /**
     * Count events by active status
     * 
     * @return Count of active events
     */
    long countByActiveTrue();
    
    /**
     * Count upcoming events
     * 
     * @param now Current time
     * @return Count of upcoming events
     */
    long countByStartTimeAfterAndActiveTrue(LocalDateTime now);
    
    /**
     * Count events by city
     * 
     * @param city City name
     * @return Count of events in the city
     */
    long countByCityAndActiveTrue(String city);
    
    /**
     * Count events by category
     * 
     * @param category Event category
     * @return Count of events in the category
     */
    long countByCategoryAndActiveTrue(String category);
    
    /**
     * Find events by organizer
     * 
     * @param organizerName Organizer name
     * @return List of events by the organizer
     */
    List<Event> findByOrganizerNameAndActiveTrue(String organizerName);
    
    /**
     * Find events with free admission
     * 
     * @return List of free events
     */
    @Query("SELECT e FROM Event e WHERE e.active = true AND (e.priceRange IS NULL OR LOWER(e.priceRange) LIKE '%free%')")
    List<Event> findFreeEvents();
    
    /**
     * Find events by multiple categories
     * 
     * @param categories List of categories
     * @return List of events in any of the categories
     */
    @Query("SELECT e FROM Event e WHERE e.active = true AND e.category IN :categories")
    List<Event> findByCategoryInAndActiveTrue(@Param("categories") List<String> categories);
    
    /**
     * Find events by text search in title or description
     * 
     * @param searchText Search text
     * @return List of events matching the search text
     */
    @Query("SELECT e FROM Event e WHERE e.active = true AND (LOWER(e.title) LIKE LOWER(CONCAT('%', :searchText, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :searchText, '%')))")
    List<Event> findByTitleOrDescriptionContainingIgnoreCaseAndActiveTrue(@Param("searchText") String searchText);
    
    /**
     * Find events near a location (within a certain distance)
     * This would require additional location data and spatial queries
     * For now, this is a placeholder
     */
    @Query("SELECT e FROM Event e WHERE e.active = true AND e.city = :city")
    List<Event> findEventsNearLocation(@Param("city") String city);
}
