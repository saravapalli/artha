package com.whatsapp.eventservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main Spring Boot Application for WhatsApp Event Service
 * 
 * This application provides a REST API for handling WhatsApp webhooks
 * and managing local event discovery through conversational AI.
 */
@SpringBootApplication
@EnableAsync
public class WhatsAppEventServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WhatsAppEventServiceApplication.class, args);
    }
}
