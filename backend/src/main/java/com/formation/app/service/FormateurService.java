package com.formation.app.service;

import com.formation.app.entity.Formateur;
import com.formation.app.entity.Role;
import com.formation.app.entity.User;
import com.formation.app.exception.BadRequestException;
import com.formation.app.exception.ResourceNotFoundException;
import com.formation.app.repository.FormateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service pour la gestion des formateurs
 */
@Service
@RequiredArgsConstructor
@Transactional
public class FormateurService {
    
    private final FormateurRepository formateurRepository;
    private final UserService userService;
    
    /**
     * Crée un nouveau formateur
     */
    public Formateur createFormateur(Formateur formateur) {
        // Vérifier l'unicité de l'email
        if (formateurRepository.existsByEmail(formateur.getEmail())) {
            throw new BadRequestException("Un formateur avec cet email existe déjà");
        }
        
        // Générer un ID si non fourni
        if (formateur.getId() == null || formateur.getId().isEmpty()) {
            formateur.setId(UUID.randomUUID().toString());
        }
        
        // Créer un User pour le formateur si non existant
        if (formateur.getUser() == null) {
            // Générer un login basé sur l'email (partie avant @)
            String login = formateur.getEmail().split("@")[0];
            String password = login; // Mot de passe par défaut = login
            
            // Vérifier si le login existe déjà, ajouter un suffixe si nécessaire
            int suffix = 1;
            String originalLogin = login;
            while (userService.getUserByLogin(login).isPresent()) {
                login = originalLogin + suffix;
                suffix++;
            }
            
            User user = userService.createUser(login, password, Role.FORMATEUR);
            formateur.setUser(user);
        }
        
        return formateurRepository.save(formateur);
    }
    
    /**
     * Met à jour un formateur
     */
    public Formateur updateFormateur(String id, Formateur formateurDetails) {
        Formateur formateur = getFormateurById(id);
        
        // Vérifier l'unicité de l'email si modifié
        if (!formateur.getEmail().equals(formateurDetails.getEmail())) {
            if (formateurRepository.existsByEmail(formateurDetails.getEmail())) {
                throw new BadRequestException("Un formateur avec cet email existe déjà");
            }
            formateur.setEmail(formateurDetails.getEmail());
        }
        
        formateur.setNom(formateurDetails.getNom());
        formateur.setSpecialite(formateurDetails.getSpecialite());
        
        return formateurRepository.save(formateur);
    }
    
    /**
     * Supprime un formateur
     */
    public void deleteFormateur(String id) {
        Formateur formateur = getFormateurById(id);
        formateurRepository.delete(formateur);
    }
    
    /**
     * Trouve tous les formateurs
     */
    @Transactional(readOnly = true)
    public List<Formateur> getAllFormateurs() {
        return formateurRepository.findAll();
    }
    
    /**
     * Trouve un formateur par son ID
     */
    @Transactional(readOnly = true)
    public Formateur getFormateurById(String id) {
        return formateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Formateur", "id", id));
    }
    
    /**
     * Trouve tous les formateurs d'une spécialité
     */
    @Transactional(readOnly = true)
    public List<Formateur> getFormateursBySpecialite(String specialite) {
        return formateurRepository.findBySpecialite(specialite);
    }
    
    /**
     * Recherche des formateurs par nom
     */
    @Transactional(readOnly = true)
    public List<Formateur> searchByNom(String nom) {
        return formateurRepository.findByNomContainingIgnoreCase(nom);
    }
}

