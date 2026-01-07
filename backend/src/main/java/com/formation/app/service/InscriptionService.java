package com.formation.app.service;

import com.formation.app.entity.Cours;
import com.formation.app.entity.Etudiant;
import com.formation.app.entity.Inscription;
import com.formation.app.exception.ConflictException;
import com.formation.app.exception.ResourceNotFoundException;
import com.formation.app.repository.CoursRepository;
import com.formation.app.repository.EtudiantRepository;
import com.formation.app.repository.InscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service pour la gestion des inscriptions
 */
@Service
@RequiredArgsConstructor
@Transactional
public class InscriptionService {
    
    private final InscriptionRepository inscriptionRepository;
    private final EtudiantRepository etudiantRepository;
    private final CoursRepository coursRepository;
    private final NotificationService notificationService;
    
    /**
     * Inscrit un étudiant à un cours
     */
    public Inscription inscrireEtudiant(String etudiantId, String coursCode) {
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new ResourceNotFoundException("Etudiant", "id", etudiantId));
        
        Cours cours = coursRepository.findByCode(coursCode)
                .orElseThrow(() -> new ResourceNotFoundException("Cours", "code", coursCode));
        
        // Vérifier si l'étudiant est déjà inscrit
        Optional<Inscription> existingInscription = inscriptionRepository.findByEtudiantAndCours(etudiant, cours);
        if (existingInscription.isPresent()) {
            Inscription inscription = existingInscription.get();
            if ("ACTIVE".equals(inscription.getStatus())) {
                throw new ConflictException("L'étudiant est déjà inscrit à ce cours");
            } else {
                // Réactiver l'inscription
                inscription.setStatus("ACTIVE");
                inscription.setDateInscription(LocalDate.now());
                Inscription saved = inscriptionRepository.save(inscription);
                notificationService.sendInscriptionEmail(etudiant, cours);
                return saved;
            }
        }
        
        // Vérifier la disponibilité
        if (!verifierDisponibilite(etudiantId, coursCode)) {
            throw new ConflictException("L'étudiant a un conflit d'horaire avec ce cours");
        }
        
        // Créer la nouvelle inscription
        Inscription inscription = new Inscription();
        inscription.setId(UUID.randomUUID().toString());
        inscription.setEtudiant(etudiant);
        inscription.setCours(cours);
        inscription.setDateInscription(LocalDate.now());
        inscription.setStatus("ACTIVE");
        
        Inscription saved = inscriptionRepository.save(inscription);
        
        // Envoyer l'email de confirmation
        notificationService.sendInscriptionEmail(etudiant, cours);
        
        return saved;
    }
    
    /**
     * Désinscrit un étudiant d'un cours
     */
    public void desinscrireEtudiant(String inscriptionId) {
        Inscription inscription = inscriptionRepository.findById(inscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Inscription", "id", inscriptionId));
        
        inscription.setStatus("CANCELLED");
        inscriptionRepository.save(inscription);
        
        // Envoyer l'email de désinscription
        notificationService.sendDesinscriptionEmail(inscription.getEtudiant(), inscription.getCours());
    }
    
    /**
     * Trouve toutes les inscriptions d'un étudiant
     */
    @Transactional(readOnly = true)
    public List<Inscription> getInscriptionsByEtudiant(String etudiantId) {
        return inscriptionRepository.findByEtudiantId(etudiantId);
    }
    
    /**
     * Trouve toutes les inscriptions d'un cours
     */
    @Transactional(readOnly = true)
    public List<Inscription> getInscriptionsByCours(String coursCode) {
        return inscriptionRepository.findByCoursCode(coursCode);
    }
    
    /**
     * Trouve toutes les inscriptions actives d'un cours
     */
    @Transactional(readOnly = true)
    public List<Inscription> getActiveInscriptionsByCours(String coursCode) {
        return inscriptionRepository.findByCoursCode(coursCode).stream()
                .filter(i -> "ACTIVE".equals(i.getStatus()))
                .toList();
    }
    
    /**
     * Vérifie la disponibilité d'un étudiant pour un cours
     */
    @Transactional(readOnly = true)
    public boolean verifierDisponibilite(String etudiantId, String coursCode) {
        // Cette méthode peut être étendue pour vérifier les conflits d'horaires
        // Pour l'instant, on vérifie juste si l'étudiant n'est pas déjà inscrit
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new ResourceNotFoundException("Etudiant", "id", etudiantId));
        
        Cours cours = coursRepository.findByCode(coursCode)
                .orElseThrow(() -> new ResourceNotFoundException("Cours", "code", coursCode));
        
        Optional<Inscription> existing = inscriptionRepository.findByEtudiantAndCours(etudiant, cours);
        return existing.isEmpty() || !"ACTIVE".equals(existing.get().getStatus());
    }
    
    /**
     * Compte le nombre d'inscriptions actives pour un cours
     */
    @Transactional(readOnly = true)
    public long countActiveInscriptions(String coursCode) {
        return inscriptionRepository.countActiveInscriptionsByCoursCode(coursCode);
    }
}

