package com.formation.app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller pour gérer les requêtes favicon.ico
 * Évite les erreurs 500 lorsque le navigateur demande automatiquement le favicon
 */
@RestController
public class FaviconController {
    
    /**
     * Gère les requêtes pour /favicon.ico
     * Retourne une réponse 204 No Content pour éviter les erreurs
     */
    @GetMapping("/favicon.ico")
    public ResponseEntity<Void> favicon() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
