package com.formation.app.repository;

import com.formation.app.entity.Cours;
import com.formation.app.entity.Formateur;
import com.formation.app.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Cours
 */
@Repository
public interface CoursRepository extends JpaRepository<Cours, String> {
    
    /**
     * Trouve un cours par son code
     * @param code le code du cours
     * @return Optional contenant le cours si trouvé
     */
    Optional<Cours> findByCode(String code);
    
    /**
     * Trouve tous les cours d'un formateur
     * @param formateur le formateur
     * @return liste des cours enseignés par ce formateur
     */
    List<Cours> findByFormateur(Formateur formateur);
    
    /**
     * Trouve tous les cours d'une session
     * @param session la session
     * @return liste des cours de cette session
     */
    List<Cours> findBySession(Session session);
    
    /**
     * Trouve tous les cours par titre (recherche partielle)
     * @param titre le titre à rechercher
     * @return liste des cours correspondants
     */
    List<Cours> findByTitreContainingIgnoreCase(String titre);
    
    /**
     * Trouve tous les cours d'un formateur par son ID
     * @param formateurId l'ID du formateur
     * @return liste des cours
     */
    @Query("SELECT c FROM Cours c WHERE c.formateur.id = :formateurId")
    List<Cours> findByFormateurId(@Param("formateurId") String formateurId);
    
    /**
     * Trouve tous les cours d'une session par son ID
     * @param sessionId l'ID de la session
     * @return liste des cours
     */
    @Query("SELECT c FROM Cours c WHERE c.session.id = :sessionId")
    List<Cours> findBySessionId(@Param("sessionId") String sessionId);
    
    /**
     * Vérifie si un cours existe avec ce code
     * @param code le code à vérifier
     * @return true si le cours existe
     */
    boolean existsByCode(String code);
}

