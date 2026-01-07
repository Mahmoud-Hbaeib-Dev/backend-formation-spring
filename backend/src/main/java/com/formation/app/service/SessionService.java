package com.formation.app.service;

import com.formation.app.entity.Session;
import com.formation.app.exception.ResourceNotFoundException;
import com.formation.app.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service pour la gestion des sessions pédagogiques
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SessionService {
    
    private final SessionRepository sessionRepository;
    
    /**
     * Crée une nouvelle session
     */
    public Session createSession(Session session) {
        // Générer un ID si non fourni
        if (session.getId() == null || session.getId().isEmpty()) {
            session.setId(UUID.randomUUID().toString());
        }
        
        return sessionRepository.save(session);
    }
    
    /**
     * Met à jour une session
     */
    public Session updateSession(String id, Session sessionDetails) {
        Session session = getSessionById(id);
        
        session.setSemestre(sessionDetails.getSemestre());
        session.setAnneeScolaire(sessionDetails.getAnneeScolaire());
        
        return sessionRepository.save(session);
    }
    
    /**
     * Supprime une session
     */
    public void deleteSession(String id) {
        Session session = getSessionById(id);
        sessionRepository.delete(session);
    }
    
    /**
     * Trouve toutes les sessions
     */
    @Transactional(readOnly = true)
    public List<Session> getAllSessions() {
        return sessionRepository.findAll();
    }
    
    /**
     * Trouve une session par son ID
     */
    @Transactional(readOnly = true)
    public Session getSessionById(String id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session", "id", id));
    }
    
    /**
     * Trouve toutes les sessions d'une année scolaire
     */
    @Transactional(readOnly = true)
    public List<Session> getSessionsByAnneeScolaire(String anneeScolaire) {
        return sessionRepository.findByAnneeScolaire(anneeScolaire);
    }
    
    /**
     * Trouve toutes les sessions d'un semestre
     */
    @Transactional(readOnly = true)
    public List<Session> getSessionsBySemestre(String semestre) {
        return sessionRepository.findBySemestre(semestre);
    }
    
    /**
     * Trouve une session par semestre et année
     */
    @Transactional(readOnly = true)
    public Session getSessionBySemestreAndAnnee(String semestre, String anneeScolaire) {
        return sessionRepository.findBySemestreAndAnneeScolaire(semestre, anneeScolaire)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Session", "semestre et anneeScolaire", semestre + " - " + anneeScolaire));
    }
}

