package com.formation.app.service;

import com.formation.app.entity.Groupe;
import com.formation.app.exception.BadRequestException;
import com.formation.app.exception.ResourceNotFoundException;
import com.formation.app.repository.GroupeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service pour la gestion des groupes
 */
@Service
@RequiredArgsConstructor
@Transactional
public class GroupeService {
    
    private final GroupeRepository groupeRepository;
    
    /**
     * Crée un nouveau groupe
     */
    public Groupe createGroupe(Groupe groupe) {
        // Vérifier l'unicité du nom
        if (groupe.getNom() != null && groupeRepository.existsByNom(groupe.getNom())) {
            throw new BadRequestException("Un groupe avec ce nom existe déjà");
        }
        
        // Générer un ID si non fourni
        if (groupe.getId() == null || groupe.getId().isEmpty()) {
            groupe.setId(UUID.randomUUID().toString());
        }
        
        return groupeRepository.save(groupe);
    }
    
    /**
     * Met à jour un groupe
     */
    public Groupe updateGroupe(String id, Groupe groupeDetails) {
        Groupe groupe = getGroupeById(id);
        
        // Vérifier l'unicité du nom si modifié
        if (!groupe.getNom().equals(groupeDetails.getNom())) {
            if (groupeRepository.existsByNom(groupeDetails.getNom())) {
                throw new BadRequestException("Un groupe avec ce nom existe déjà");
            }
            groupe.setNom(groupeDetails.getNom());
        }
        
        return groupeRepository.save(groupe);
    }
    
    /**
     * Supprime un groupe
     */
    public void deleteGroupe(String id) {
        Groupe groupe = getGroupeById(id);
        groupeRepository.delete(groupe);
    }
    
    /**
     * Trouve tous les groupes
     */
    @Transactional(readOnly = true)
    public List<Groupe> getAllGroupes() {
        return groupeRepository.findAll();
    }
    
    /**
     * Trouve un groupe par son ID
     */
    @Transactional(readOnly = true)
    public Groupe getGroupeById(String id) {
        return groupeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Groupe", "id", id));
    }
    
    /**
     * Recherche des groupes par nom
     */
    @Transactional(readOnly = true)
    public List<Groupe> searchByNom(String nom) {
        return groupeRepository.findByNomContainingIgnoreCase(nom);
    }
}

