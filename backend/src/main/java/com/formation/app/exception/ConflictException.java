package com.formation.app.exception;

/**
 * Exception levée en cas de conflit (ex: doublon, conflit d'horaire)
 */
public class ConflictException extends RuntimeException {
    
    public ConflictException(String message) {
        super(message);
    }
}

