package com.formation.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller de test simple pour vérifier que le backend fonctionne
 */
@RestController
@RequestMapping("/test")
@CrossOrigin(origins = "*")
public class TestController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Backend is running!");
        response.put("timestamp", System.currentTimeMillis());
        response.put("endpoint", "/test");
        return ResponseEntity.ok(response);
    }
}

