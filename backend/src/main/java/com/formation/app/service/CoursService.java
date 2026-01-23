package com.formation.app.service;

import com.formation.app.entity.Cours;
import com.formation.app.entity.Formateur;
import com.formation.app.entity.Groupe;
import com.formation.app.entity.Session;
import com.formation.app.exception.BadRequestException;
import com.formation.app.exception.ResourceNotFoundException;
import com.formation.app.repository.CoursGroupeRepository;
import com.formation.app.repository.CoursRepository;
import com.formation.app.repository.FormateurRepository;
import com.formation.app.repository.GroupeRepository;
import com.formation.app.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service pour la gestion des cours
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CoursService {
    
    private final CoursRepository coursRepository;
    private final FormateurRepository formateurRepository;
    private final SessionRepository sessionRepository;
    private final GroupeRepository groupeRepository;
    private final CoursGroupeRepository coursGroupeRepository;
    
    /**
     * Crée un nouveau cours
     */
    @CacheEvict(value = "cours", allEntries = true)
    public Cours createCours(Cours cours) {
        // Vérifier l'unicité du code
        if (cours.getCode() != null && coursRepository.existsByCode(cours.getCode())) {
            throw new BadRequestException("Un cours avec ce code existe déjà");
        }
        
        // Générer un code si non fourni
        if (cours.getCode() == null || cours.getCode().isEmpty()) {
            cours.setCode("COURS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        
        // Vérifier que le formateur existe et est défini (obligatoire)
        if (cours.getFormateur() == null) {
            throw new BadRequestException("Le formateur est obligatoire pour créer un cours");
        }
        
        if (cours.getFormateur().getId() != null) {
            Formateur formateur = formateurRepository.findById(cours.getFormateur().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Formateur", "id", cours.getFormateur().getId()));
            cours.setFormateur(formateur);
        } else {
            throw new BadRequestException("L'ID du formateur est obligatoire");
        }
        
        // Vérifier que la session existe (optionnelle)
        if (cours.getSession() != null && cours.getSession().getId() != null) {
            Session session = sessionRepository.findById(cours.getSession().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Session", "id", cours.getSession().getId()));
            cours.setSession(session);
        }
        // Si la session n'est pas fournie, elle reste null (nullable = true)
        
        return coursRepository.save(cours);
    }
    
    /**
     * Met à jour un cours
     */
    @CacheEvict(value = "cours", allEntries = true)
    public Cours updateCours(String code, Cours coursDetails) {
        Cours cours = getCoursByCode(code);
        
        cours.setTitre(coursDetails.getTitre());
        cours.setDescription(coursDetails.getDescription());
        
        // Mettre à jour le formateur si fourni
        if (coursDetails.getFormateur() != null && coursDetails.getFormateur().getId() != null) {
            Formateur formateur = formateurRepository.findById(coursDetails.getFormateur().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Formateur", "id", coursDetails.getFormateur().getId()));
            cours.setFormateur(formateur);
        }
        
        // Mettre à jour la session si fournie
        if (coursDetails.getSession() != null && coursDetails.getSession().getId() != null) {
            Session session = sessionRepository.findById(coursDetails.getSession().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Session", "id", coursDetails.getSession().getId()));
            cours.setSession(session);
        }
        
        return coursRepository.save(cours);
    }
    
    /**
     * Supprime un cours
     */
    @CacheEvict(value = "cours", allEntries = true)
    public void deleteCours(String code) {
        Cours cours = getCoursByCode(code);
        coursRepository.delete(cours);
    }
    
    /**
     * Trouve tous les cours
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "cours", unless = "#result.isEmpty()")
    public List<Cours> getAllCours() {
        return coursRepository.findAll();
    }
    
    /**
     * Trouve un cours par son code
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "cours", key = "#code")
    public Cours getCoursByCode(String code) {
        return coursRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Cours", "code", code));
    }
    
    /**
     * Trouve tous les cours d'un formateur
     */
    @Transactional(readOnly = true)
    public List<Cours> getCoursByFormateur(String formateurId) {
        return coursRepository.findByFormateurId(formateurId);
    }
    
    /**
     * Trouve tous les cours d'une session
     */
    @Transactional(readOnly = true)
    public List<Cours> getCoursBySession(String sessionId) {
        return coursRepository.findBySessionId(sessionId);
    }
    
    /**
     * Recherche des cours par titre
     */
    @Transactional(readOnly = true)
    public List<Cours> searchByTitre(String titre) {
        return coursRepository.findByTitreContainingIgnoreCase(titre);
    }
    
    /**
     * Assigne un formateur à un cours
     */
    public Cours assignFormateur(String coursCode, String formateurId) {
        Cours cours = getCoursByCode(coursCode);
        Formateur formateur = formateurRepository.findById(formateurId)
                .orElseThrow(() -> new ResourceNotFoundException("Formateur", "id", formateurId));
        
        cours.setFormateur(formateur);
        return coursRepository.save(cours);
    }
    
    /**
     * Assigne un cours à des groupes
     */
    public void assignToGroupes(String coursCode, List<String> groupeIds) {
        Cours cours = getCoursByCode(coursCode);
        
        // Supprimer les associations existantes
        coursGroupeRepository.deleteByCoursCode(coursCode);
        
        // Créer les nouvelles associations
        for (String groupeId : groupeIds) {
            Groupe groupe = groupeRepository.findById(groupeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Groupe", "id", groupeId));
            
            // Vérifier si l'association existe déjà
            if (!coursGroupeRepository.existsByCoursCodeAndGroupeId(coursCode, groupeId)) {
                com.formation.app.entity.CoursGroupe coursGroupe = new com.formation.app.entity.CoursGroupe();
                coursGroupe.setId(UUID.randomUUID().toString());
                coursGroupe.setCours(cours);
                coursGroupe.setGroupe(groupe);
                coursGroupeRepository.save(coursGroupe);
            }
        }
    }
    
    /**
     * Trouve tous les groupes d'un cours
     */
    @Transactional(readOnly = true)
    public List<Groupe> getGroupesByCours(String coursCode) {
        return coursGroupeRepository.findGroupesByCoursCode(coursCode);
    }
}

