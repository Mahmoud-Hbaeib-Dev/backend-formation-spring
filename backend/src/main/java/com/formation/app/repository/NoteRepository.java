package com.formation.app.repository;

import com.formation.app.entity.Cours;
import com.formation.app.entity.Etudiant;
import com.formation.app.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Note
 */
@Repository
public interface NoteRepository extends JpaRepository<Note, String> {
    
    /**
     * Trouve toutes les notes d'un étudiant
     * @param etudiant l'étudiant
     * @return liste des notes de cet étudiant
     */
    List<Note> findByEtudiant(Etudiant etudiant);
    
    /**
     * Trouve toutes les notes d'un cours
     * @param cours le cours
     * @return liste des notes pour ce cours
     */
    List<Note> findByCours(Cours cours);
    
    /**
     * Trouve une note spécifique étudiant-cours
     * @param etudiant l'étudiant
     * @param cours le cours
     * @return Optional contenant la note si trouvée
     */
    Optional<Note> findByEtudiantAndCours(Etudiant etudiant, Cours cours);
    
    /**
     * Trouve toutes les notes d'un étudiant par son ID
     * @param etudiantId l'ID de l'étudiant
     * @return liste des notes
     */
    @Query("SELECT n FROM Note n WHERE n.etudiant.id = :etudiantId")
    List<Note> findByEtudiantId(@Param("etudiantId") String etudiantId);
    
    /**
     * Trouve toutes les notes d'un cours par son code
     * @param coursCode le code du cours
     * @return liste des notes
     */
    @Query("SELECT n FROM Note n WHERE n.cours.code = :coursCode")
    List<Note> findByCoursCode(@Param("coursCode") String coursCode);
    
    /**
     * Calcule la moyenne d'un étudiant pour un cours spécifique
     * @param etudiantId l'ID de l'étudiant
     * @param coursCode le code du cours
     * @return la moyenne (null si aucune note)
     */
    @Query("SELECT AVG(n.valeur) FROM Note n " +
           "WHERE n.etudiant.id = :etudiantId AND n.cours.code = :coursCode")
    Double calculerMoyenneEtudiantCours(@Param("etudiantId") String etudiantId, 
                                         @Param("coursCode") String coursCode);
    
    /**
     * Calcule la moyenne générale d'un étudiant (tous cours confondus)
     * @param etudiantId l'ID de l'étudiant
     * @return la moyenne générale
     */
    @Query("SELECT AVG(n.valeur) FROM Note n WHERE n.etudiant.id = :etudiantId")
    Double calculerMoyenneGeneraleEtudiant(@Param("etudiantId") String etudiantId);
    
    /**
     * Calcule la moyenne d'un cours (tous étudiants confondus)
     * @param coursCode le code du cours
     * @return la moyenne du cours
     */
    @Query("SELECT AVG(n.valeur) FROM Note n WHERE n.cours.code = :coursCode")
    Double calculerMoyenneCours(@Param("coursCode") String coursCode);
    
    /**
     * Compte le nombre d'étudiants ayant une note pour un cours
     * @param coursCode le code du cours
     * @return nombre d'étudiants notés
     */
    @Query("SELECT COUNT(DISTINCT n.etudiant.id) FROM Note n WHERE n.cours.code = :coursCode")
    long countEtudiantsNotesByCoursCode(@Param("coursCode") String coursCode);
    
    /**
     * Trouve toutes les notes supérieures ou égales à une valeur
     * @param valeur la valeur minimale
     * @return liste des notes
     */
    List<Note> findByValeurGreaterThanEqual(Float valeur);
    
    /**
     * Trouve toutes les notes inférieures à une valeur
     * @param valeur la valeur maximale
     * @return liste des notes
     */
    List<Note> findByValeurLessThan(Float valeur);
}

