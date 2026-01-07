package com.formation.app.repository;

import com.formation.app.entity.Etudiant;
import com.formation.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Etudiant
 */
@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, String> {
    
    /**
     * Trouve un étudiant par son matricule
     * @param matricule le matricule de l'étudiant
     * @return Optional contenant l'étudiant si trouvé
     */
    Optional<Etudiant> findByMatricule(String matricule);
    
    /**
     * Trouve un étudiant par son email
     * @param email l'email de l'étudiant
     * @return Optional contenant l'étudiant si trouvé
     */
    Optional<Etudiant> findByEmail(String email);
    
    /**
     * Trouve un étudiant par son utilisateur associé
     * @param user l'utilisateur associé
     * @return Optional contenant l'étudiant si trouvé
     */
    Optional<Etudiant> findByUser(User user);
    
    /**
     * Trouve tous les étudiants par nom (recherche partielle)
     * @param nom le nom à rechercher
     * @return liste des étudiants correspondants
     */
    List<Etudiant> findByNomContainingIgnoreCase(String nom);
    
    /**
     * Trouve tous les étudiants par prénom (recherche partielle)
     * @param prenom le prénom à rechercher
     * @return liste des étudiants correspondants
     */
    List<Etudiant> findByPrenomContainingIgnoreCase(String prenom);
    
    /**
     * Vérifie si un étudiant existe avec ce matricule
     * @param matricule le matricule à vérifier
     * @return true si l'étudiant existe
     */
    boolean existsByMatricule(String matricule);
    
    /**
     * Vérifie si un étudiant existe avec cet email
     * @param email l'email à vérifier
     * @return true si l'étudiant existe
     */
    boolean existsByEmail(String email);
}

