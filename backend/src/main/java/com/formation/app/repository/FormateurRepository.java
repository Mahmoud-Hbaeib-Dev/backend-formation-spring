package com.formation.app.repository;

import com.formation.app.entity.Formateur;
import com.formation.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Formateur
 */
@Repository
public interface FormateurRepository extends JpaRepository<Formateur, String> {
    
    /**
     * Trouve un formateur par son email
     * @param email l'email du formateur
     * @return Optional contenant le formateur si trouvé
     */
    Optional<Formateur> findByEmail(String email);
    
    /**
     * Trouve tous les formateurs par spécialité
     * @param specialite la spécialité recherchée
     * @return liste des formateurs avec cette spécialité
     */
    List<Formateur> findBySpecialite(String specialite);
    
    /**
     * Trouve tous les formateurs par nom (recherche partielle)
     * @param nom le nom à rechercher
     * @return liste des formateurs correspondants
     */
    List<Formateur> findByNomContainingIgnoreCase(String nom);
    
    /**
     * Vérifie si un formateur existe avec cet email
     * @param email l'email à vérifier
     * @return true si le formateur existe
     */
    boolean existsByEmail(String email);
    
    /**
     * Trouve un formateur par son matricule
     * @param matricule le matricule du formateur
     * @return Optional contenant le formateur si trouvé
     */
    Optional<Formateur> findByMatricule(String matricule);
    
    /**
     * Vérifie si un formateur existe avec ce matricule
     * @param matricule le matricule à vérifier
     * @return true si le formateur existe
     */
    boolean existsByMatricule(String matricule);
    
    /**
     * Trouve un formateur par son utilisateur associé
     * @param user l'utilisateur associé
     * @return Optional contenant le formateur si trouvé
     */
    Optional<Formateur> findByUser(User user);
}

