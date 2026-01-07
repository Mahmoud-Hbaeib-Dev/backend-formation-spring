package com.formation.app.repository;

import com.formation.app.entity.Cours;
import com.formation.app.entity.Etudiant;
import com.formation.app.entity.Inscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Inscription
 */
@Repository
public interface InscriptionRepository extends JpaRepository<Inscription, String> {
    
    /**
     * Trouve toutes les inscriptions d'un étudiant
     * @param etudiant l'étudiant
     * @return liste des inscriptions de cet étudiant
     */
    List<Inscription> findByEtudiant(Etudiant etudiant);
    
    /**
     * Trouve toutes les inscriptions d'un cours
     * @param cours le cours
     * @return liste des inscriptions pour ce cours
     */
    List<Inscription> findByCours(Cours cours);
    
    /**
     * Trouve une inscription spécifique étudiant-cours
     * @param etudiant l'étudiant
     * @param cours le cours
     * @return Optional contenant l'inscription si trouvée
     */
    Optional<Inscription> findByEtudiantAndCours(Etudiant etudiant, Cours cours);
    
    /**
     * Trouve toutes les inscriptions par statut
     * @param status le statut (ex: "ACTIVE", "CANCELLED")
     * @return liste des inscriptions avec ce statut
     */
    List<Inscription> findByStatus(String status);
    
    /**
     * Trouve toutes les inscriptions d'un étudiant par son ID
     * @param etudiantId l'ID de l'étudiant
     * @return liste des inscriptions
     */
    @Query("SELECT i FROM Inscription i WHERE i.etudiant.id = :etudiantId")
    List<Inscription> findByEtudiantId(@Param("etudiantId") String etudiantId);
    
    /**
     * Trouve toutes les inscriptions d'un cours par son code
     * @param coursCode le code du cours
     * @return liste des inscriptions
     */
    @Query("SELECT i FROM Inscription i WHERE i.cours.code = :coursCode")
    List<Inscription> findByCoursCode(@Param("coursCode") String coursCode);
    
    /**
     * Compte le nombre d'inscriptions actives pour un cours
     * @param coursCode le code du cours
     * @return nombre d'inscriptions actives
     */
    @Query("SELECT COUNT(i) FROM Inscription i WHERE i.cours.code = :coursCode AND i.status = 'ACTIVE'")
    long countActiveInscriptionsByCoursCode(@Param("coursCode") String coursCode);
}

