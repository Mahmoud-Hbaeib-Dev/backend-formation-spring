package com.formation.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Configuration CORS pour l'API REST
 */
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Autoriser les origines (configurable via properties)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",  // React
            "http://localhost:4200",  // Angular
            "http://localhost:5173"   // Vite
        ));
        
        // Autoriser les méthodes HTTP
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // Autoriser les headers
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Autoriser les credentials (cookies, auth headers)
        configuration.setAllowCredentials(true);
        
        // Headers exposés au client
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
        // Durée de cache de la pré-requête CORS
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        
        return source;
    }
}

