package com.formation.app.service;

import com.formation.app.entity.Formateur;
import com.formation.app.entity.Role;
import com.formation.app.entity.User;
import com.formation.app.exception.BadRequestException;
import com.formation.app.exception.ResourceNotFoundException;
import com.formation.app.repository.FormateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class FormateurService {
    
    private final FormateurRepository formateurRepository;
    private final UserService userService;
    private final NotificationService notificationService;
    
    /**
     * Crée un nouveau formateur
     */
    public Formateur createFormateur(Formateur formateur) {
        // Générer un matricule automatiquement si non fourni
        if (formateur.getMatricule() == null || formateur.getMatricule().isEmpty()) {
            String matricule;
            do {
                matricule = "FORM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            } while (formateurRepository.existsByMatricule(matricule));
            formateur.setMatricule(matricule);
        } else {
            // Vérifier l'unicité du matricule si fourni manuellement
            if (formateurRepository.existsByMatricule(formateur.getMatricule())) {
                throw new BadRequestException("Un formateur avec ce matricule existe déjà");
            }
        }
        
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
            // Utiliser le matricule en minuscules comme login et password
            String login = formateur.getMatricule().toLowerCase();
            String password = formateur.getMatricule().toLowerCase(); // Le password sera hashé par UserService
            
            // Vérifier si le login existe déjà (peu probable mais on vérifie)
            if (userService.getUserByLogin(login).isPresent()) {
                // Si le login existe, générer un nouveau matricule unique
                String matricule;
                do {
                    matricule = "FORM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                } while (formateurRepository.existsByMatricule(matricule) || userService.getUserByLogin(matricule.toLowerCase()).isPresent());
                formateur.setMatricule(matricule);
                login = matricule.toLowerCase();
                password = matricule.toLowerCase();
            }
            
            User user = userService.createUser(login, password, Role.FORMATEUR);
            formateur.setUser(user);
        }
        
        Formateur saved = formateurRepository.save(formateur);
        
        // Envoyer un email avec les identifiants si l'utilisateur vient d'être créé
        if (saved.getUser() != null) {
            try {
                notificationService.sendFormateurCredentials(saved);
            } catch (Exception e) {
                log.error("❌ Erreur lors de l'envoi de l'email de bienvenue au formateur {}: {}", 
                    saved.getEmail(), e.getMessage());
                // Ne pas faire échouer la création si l'email échoue
            }
        }
        
        return saved;
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

