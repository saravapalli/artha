package com.whatsapp.eventservice.repository;

import com.whatsapp.eventservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by phone number
     * 
     * @param phoneNumber Phone number
     * @return Optional User
     */
    Optional<User> findByPhoneNumber(String phoneNumber);
    
    /**
     * Find user by WhatsApp ID
     * 
     * @param whatsappId WhatsApp ID
     * @return Optional User
     */
    Optional<User> findByWhatsappId(String whatsappId);
    
    /**
     * Find users by opt-in status
     * 
     * @return List of opted-in users
     */
    List<User> findByOptInStatusTrue();
    
    /**
     * Count users by opt-in status
     * 
     * @return Count of opted-in users
     */
    long countByOptInStatusTrue();
    
    /**
     * Find users by city
     * 
     * @param city City name
     * @return List of users in the city
     */
    List<User> findByCity(String city);
    
    /**
     * Find users by language
     * 
     * @param language Language code
     * @return List of users with the language
     */
    List<User> findByLanguage(String language);
    
    /**
     * Find active users (opted-in) by city
     * 
     * @param city City name
     * @return List of active users in the city
     */
    @Query("SELECT u FROM User u WHERE u.city = :city AND u.optInStatus = true")
    List<User> findActiveUsersByCity(@Param("city") String city);
    
    /**
     * Count users by city
     * 
     * @param city City name
     * @return Count of users in the city
     */
    long countByCity(String city);
    
    /**
     * Find users created after a specific date
     * 
     * @param date Date
     * @return List of users created after the date
     */
    @Query("SELECT u FROM User u WHERE u.createdAt > :date")
    List<User> findUsersCreatedAfter(@Param("date") java.time.LocalDateTime date);
}
