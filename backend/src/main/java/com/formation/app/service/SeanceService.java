package com.formation.app.service;

import com.formation.app.entity.Cours;
import com.formation.app.entity.Formateur;
import com.formation.app.entity.Seance;
import com.formation.app.exception.ConflictException;
import com.formation.app.exception.ResourceNotFoundException;
import com.formation.app.repository.CoursRepository;
import com.formation.app.repository.FormateurRepository;
import com.formation.app.repository.SeanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Service pour la gestion des séances de cours
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SeanceService {
    
    private final SeanceRepository seanceRepository;
    private final CoursRepository coursRepository;
    private final FormateurRepository formateurRepository;
    
    /**
     * Crée une nouvelle séance
     */
    public Seance createSeance(Seance seance) {
        // Vérifier que le cours existe
        Cours cours = coursRepository.findByCode(seance.getCours().getCode())
                .orElseThrow(() -> new ResourceNotFoundException("Cours", "code", seance.getCours().getCode()));
        seance.setCours(cours);
        
        // Vérifier que le formateur existe
        Formateur formateur = formateurRepository.findById(seance.getFormateur().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Formateur", "id", seance.getFormateur().getId()));
        seance.setFormateur(formateur);
        
        // Vérifier les conflits d'horaires
        if (verifierConflitFormateur(seance.getFormateur().getId(), seance.getDate(), seance.getHeure())) {
            throw new ConflictException("Le formateur a déjà une séance à cette date et heure");
        }
        
        // Générer un ID si non fourni
        if (seance.getId() == null || seance.getId().isEmpty()) {
            seance.setId(UUID.randomUUID().toString());
        }
        
        return seanceRepository.save(seance);
    }
    
    /**
     * Met à jour une séance
     */
    public Seance updateSeance(String id, Seance seanceDetails) {
        Seance seance = getSeanceById(id);
        
        // Vérifier les conflits si la date/heure change
        if (!seance.getDate().equals(seanceDetails.getDate()) || 
            !seance.getHeure().equals(seanceDetails.getHeure())) {
            if (verifierConflitFormateur(seanceDetails.getFormateur().getId(), 
                                        seanceDetails.getDate(), 
                                        seanceDetails.getHeure())) {
                throw new ConflictException("Le formateur a déjà une séance à cette date et heure");
            }
        }
        
        seance.setDate(seanceDetails.getDate());
        seance.setHeure(seanceDetails.getHeure());
        seance.setSalle(seanceDetails.getSalle());
        
        // Mettre à jour le cours si fourni
        if (seanceDetails.getCours() != null && seanceDetails.getCours().getCode() != null) {
            Cours cours = coursRepository.findByCode(seanceDetails.getCours().getCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Cours", "code", seanceDetails.getCours().getCode()));
            seance.setCours(cours);
        }
        
        // Mettre à jour le formateur si fourni
        if (seanceDetails.getFormateur() != null && seanceDetails.getFormateur().getId() != null) {
            Formateur formateur = formateurRepository.findById(seanceDetails.getFormateur().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Formateur", "id", seanceDetails.getFormateur().getId()));
            seance.setFormateur(formateur);
        }
        
        return seanceRepository.save(seance);
    }
    
    /**
     * Supprime une séance
     */
    public void deleteSeance(String id) {
        Seance seance = getSeanceById(id);
        seanceRepository.delete(seance);
    }
    
    /**
     * Trouve toutes les séances d'un cours
     */
    @Transactional(readOnly = true)
    public List<Seance> getSeancesByCours(String coursCode) {
        return seanceRepository.findByCoursCode(coursCode);
    }
    
    /**
     * Trouve toutes les séances d'un formateur
     */
    @Transactional(readOnly = true)
    public List<Seance> getSeancesByFormateur(String formateurId) {
        return seanceRepository.findByFormateurId(formateurId);
    }
    
    /**
     * Trouve l'emploi du temps d'un étudiant
     */
    @Transactional(readOnly = true)
    public List<Seance> getEmploiDuTempsEtudiant(String etudiantId) {
        return seanceRepository.findSeancesByEtudiantId(etudiantId);
    }
    
    /**
     * Vérifie s'il y a un conflit d'horaire pour un étudiant
     */
    @Transactional(readOnly = true)
    public boolean verifierConflitHoraires(String etudiantId, LocalDate date, LocalTime heure) {
        List<Seance> seances = getEmploiDuTempsEtudiant(etudiantId);
        return seances.stream()
                .anyMatch(s -> s.getDate().equals(date) && s.getHeure().equals(heure));
    }
    
    /**
     * Vérifie s'il y a un conflit d'horaire pour un formateur
     */
    @Transactional(readOnly = true)
    public boolean verifierConflitFormateur(String formateurId, LocalDate date, LocalTime heure) {
        return seanceRepository.existsConflitFormateur(formateurId, date, heure);
    }
    
    /**
     * Trouve une séance par son ID
     */
    @Transactional(readOnly = true)
    public Seance getSeanceById(String id) {
        return seanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seance", "id", id));
    }
    
    /**
     * Trouve toutes les séances d'une date
     */
    @Transactional(readOnly = true)
    public List<Seance> getSeancesByDate(LocalDate date) {
        return seanceRepository.findByDate(date);
    }
    
    /**
     * Trouve toutes les séances entre deux dates
     */
    @Transactional(readOnly = true)
    public List<Seance> getSeancesBetweenDates(LocalDate dateDebut, LocalDate dateFin) {
        return seanceRepository.findByDateBetween(dateDebut, dateFin);
    }
}

