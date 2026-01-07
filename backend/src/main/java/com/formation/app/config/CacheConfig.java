package com.formation.app.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Configuration du cache avec Caffeine
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public Caffeine<Object, Object> caffeineConfig() {
        return Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES) // Les entrées expirent 10 minutes après leur écriture
                .maximumSize(1000); // Taille maximale du cache
    }

    @Bean
    public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeine);
        // Définir les noms des caches à utiliser (doivent correspondre aux annotations @Cacheable)
        cacheManager.setCacheNames(Arrays.asList(
                "cours",
                "etudiants",
                "formateurs",
                "sessions",
                "groupes"
        ));
        return cacheManager;
    }
}

