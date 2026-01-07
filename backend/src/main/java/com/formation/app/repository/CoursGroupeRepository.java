package com.formation.app.repository;

import com.formation.app.entity.Cours;
import com.formation.app.entity.CoursGroupe;
import com.formation.app.entity.Groupe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité CoursGroupe (table de liaison)
 */
@Repository
public interface CoursGroupeRepository extends JpaRepository<CoursGroupe, String> {
    
    /**
     * Trouve toutes les associations d'un cours
     * @param cours le cours
     * @return liste des associations
     */
    List<CoursGroupe> findByCours(Cours cours);
    
    /**
     * Trouve toutes les associations d'un groupe
     * @param groupe le groupe
     * @return liste des associations
     */
    List<CoursGroupe> findByGroupe(Groupe groupe);
    
    /**
     * Trouve une association spécifique cours-groupe
     * @param cours le cours
     * @param groupe le groupe
     * @return Optional contenant l'association si trouvée
     */
    Optional<CoursGroupe> findByCoursAndGroupe(Cours cours, Groupe groupe);
    
    /**
     * Trouve tous les groupes associés à un cours
     * @param coursCode le code du cours
     * @return liste des groupes
     */
    @Query("SELECT cg.groupe FROM CoursGroupe cg WHERE cg.cours.code = :coursCode")
    List<Groupe> findGroupesByCoursCode(@Param("coursCode") String coursCode);
    
    /**
     * Trouve tous les cours associés à un groupe
     * @param groupeId l'ID du groupe
     * @return liste des cours
     */
    @Query("SELECT cg.cours FROM CoursGroupe cg WHERE cg.groupe.id = :groupeId")
    List<Cours> findCoursByGroupeId(@Param("groupeId") String groupeId);
    
    /**
     * Vérifie si une association existe
     * @param coursCode le code du cours
     * @param groupeId l'ID du groupe
     * @return true si l'association existe
     */
    @Query("SELECT COUNT(cg) > 0 FROM CoursGroupe cg " +
           "WHERE cg.cours.code = :coursCode AND cg.groupe.id = :groupeId")
    boolean existsByCoursCodeAndGroupeId(@Param("coursCode") String coursCode, 
                                          @Param("groupeId") String groupeId);
    
    /**
     * Supprime toutes les associations d'un cours
     * @param coursCode le code du cours
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM CoursGroupe cg WHERE cg.cours.code = :coursCode")
    void deleteByCoursCode(@Param("coursCode") String coursCode);
    
    /**
     * Supprime toutes les associations d'un groupe
     * @param groupeId l'ID du groupe
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM CoursGroupe cg WHERE cg.groupe.id = :groupeId")
    void deleteByGroupeId(@Param("groupeId") String groupeId);
}

