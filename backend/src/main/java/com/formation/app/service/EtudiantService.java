package com.formation.app.service;

import com.formation.app.entity.Etudiant;
import com.formation.app.entity.Role;
import com.formation.app.entity.User;
import com.formation.app.exception.BadRequestException;
import com.formation.app.exception.ResourceNotFoundException;
import com.formation.app.repository.EtudiantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service pour la gestion des étudiants
 */
@Service
@RequiredArgsConstructor
@Transactional
public class EtudiantService {
    
    private final EtudiantRepository etudiantRepository;
    private final UserService userService;
    
    /**
     * Crée un nouvel étudiant
     */
    public Etudiant createEtudiant(Etudiant etudiant) {
        // Générer un matricule automatiquement si non fourni
        if (etudiant.getMatricule() == null || etudiant.getMatricule().isEmpty()) {
            String matricule;
            do {
                matricule = "ETUD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            } while (etudiantRepository.existsByMatricule(matricule));
            etudiant.setMatricule(matricule);
        } else {
            // Vérifier l'unicité du matricule si fourni manuellement
            if (etudiantRepository.existsByMatricule(etudiant.getMatricule())) {
                throw new BadRequestException("Un étudiant avec ce matricule existe déjà");
            }
        }
        
        // Vérifier l'unicité de l'email
        if (etudiantRepository.existsByEmail(etudiant.getEmail())) {
            throw new BadRequestException("Un étudiant avec cet email existe déjà");
        }
        
        // Générer un ID si non fourni
        if (etudiant.getId() == null || etudiant.getId().isEmpty()) {
            etudiant.setId(UUID.randomUUID().toString());
        }
        
        // Définir la date d'inscription si non fournie
        if (etudiant.getDateInscription() == null) {
            etudiant.setDateInscription(LocalDate.now());
        }
        
        // Créer un User pour l'étudiant si non existant
        if (etudiant.getUser() == null) {
            // Utiliser le matricule en minuscules comme login et password
            String login = etudiant.getMatricule().toLowerCase();
            String password = etudiant.getMatricule().toLowerCase(); // Le password sera hashé par UserService
            
            // Vérifier si le login existe déjà (peu probable mais on vérifie)
            if (userService.getUserByLogin(login).isPresent()) {
                // Si le login existe, générer un nouveau matricule unique
                String matricule;
                do {
                    matricule = "ETUD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                } while (etudiantRepository.existsByMatricule(matricule) || userService.getUserByLogin(matricule.toLowerCase()).isPresent());
                etudiant.setMatricule(matricule);
                login = matricule.toLowerCase();
                password = matricule.toLowerCase();
            }
            
            User user = userService.createUser(login, password, Role.ETUDIANT);
            etudiant.setUser(user);
        }
        
        return etudiantRepository.save(etudiant);
    }
    
    /**
     * Met à jour un étudiant
     */
    public Etudiant updateEtudiant(String id, Etudiant etudiantDetails) {
        Etudiant etudiant = getEtudiantById(id);
        
        // Vérifier l'unicité du matricule si modifié
        if (!etudiant.getMatricule().equals(etudiantDetails.getMatricule())) {
            if (etudiantRepository.existsByMatricule(etudiantDetails.getMatricule())) {
                throw new BadRequestException("Un étudiant avec ce matricule existe déjà");
            }
            etudiant.setMatricule(etudiantDetails.getMatricule());
        }
        
        // Vérifier l'unicité de l'email si modifié
        if (!etudiant.getEmail().equals(etudiantDetails.getEmail())) {
            if (etudiantRepository.existsByEmail(etudiantDetails.getEmail())) {
                throw new BadRequestException("Un étudiant avec cet email existe déjà");
            }
            etudiant.setEmail(etudiantDetails.getEmail());
        }
        
        etudiant.setNom(etudiantDetails.getNom());
        etudiant.setPrenom(etudiantDetails.getPrenom());
        
        return etudiantRepository.save(etudiant);
    }
    
    /**
     * Supprime un étudiant
     */
    public void deleteEtudiant(String id) {
        Etudiant etudiant = getEtudiantById(id);
        etudiantRepository.delete(etudiant);
    }
    
    /**
     * Trouve tous les étudiants
     */
    @Transactional(readOnly = true)
    public List<Etudiant> getAllEtudiants() {
        return etudiantRepository.findAll();
    }
    
    /**
     * Trouve un étudiant par son ID
     */
    @Transactional(readOnly = true)
    public Etudiant getEtudiantById(String id) {
        return etudiantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Etudiant", "id", id));
    }
    
    /**
     * Trouve un étudiant par son matricule
     */
    @Transactional(readOnly = true)
    public Etudiant getEtudiantByMatricule(String matricule) {
        return etudiantRepository.findByMatricule(matricule)
                .orElseThrow(() -> new ResourceNotFoundException("Etudiant", "matricule", matricule));
    }
    
    /**
     * Recherche des étudiants par nom
     */
    @Transactional(readOnly = true)
    public List<Etudiant> searchByNom(String nom) {
        return etudiantRepository.findByNomContainingIgnoreCase(nom);
    }
    
    /**
     * Recherche des étudiants par prénom
     */
    @Transactional(readOnly = true)
    public List<Etudiant> searchByPrenom(String prenom) {
        return etudiantRepository.findByPrenomContainingIgnoreCase(prenom);
    }
}

