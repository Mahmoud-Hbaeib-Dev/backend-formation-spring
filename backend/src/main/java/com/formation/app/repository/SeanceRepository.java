package com.formation.app.repository;

import com.formation.app.entity.Cours;
import com.formation.app.entity.Formateur;
import com.formation.app.entity.Seance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Seance
 */
@Repository
public interface SeanceRepository extends JpaRepository<Seance, String> {
    
    /**
     * Trouve toutes les séances d'un cours
     * @param cours le cours
     * @return liste des séances de ce cours
     */
    List<Seance> findByCours(Cours cours);
    
    /**
     * Trouve toutes les séances d'un formateur
     * @param formateur le formateur
     * @return liste des séances animées par ce formateur
     */
    List<Seance> findByFormateur(Formateur formateur);
    
    /**
     * Trouve une séance par date et heure
     * @param date la date
     * @param heure l'heure
     * @return Optional contenant la séance si trouvée
     */
    Optional<Seance> findByDateAndHeure(LocalDate date, LocalTime heure);
    
    /**
     * Trouve toutes les séances d'un cours par son code
     * @param coursCode le code du cours
     * @return liste des séances
     */
    @Query("SELECT s FROM Seance s WHERE s.cours.code = :coursCode ORDER BY s.date, s.heure")
    List<Seance> findByCoursCode(@Param("coursCode") String coursCode);
    
    /**
     * Trouve toutes les séances d'un formateur par son ID
     * @param formateurId l'ID du formateur
     * @return liste des séances
     */
    @Query("SELECT s FROM Seance s WHERE s.formateur.id = :formateurId ORDER BY s.date, s.heure")
    List<Seance> findByFormateurId(@Param("formateurId") String formateurId);
    
    /**
     * Trouve toutes les séances d'un étudiant (via ses inscriptions)
     * @param etudiantId l'ID de l'étudiant
     * @return liste des séances
     */
    @Query("SELECT DISTINCT s FROM Seance s " +
           "JOIN s.cours c " +
           "JOIN c.inscriptions i " +
           "WHERE i.etudiant.id = :etudiantId AND i.status = 'ACTIVE' " +
           "ORDER BY s.date, s.heure")
    List<Seance> findSeancesByEtudiantId(@Param("etudiantId") String etudiantId);
    
    /**
     * Vérifie s'il y a un conflit d'horaire pour un formateur
     * @param formateurId l'ID du formateur
     * @param date la date
     * @param heure l'heure
     * @return true si conflit détecté
     */
    @Query("SELECT COUNT(s) > 0 FROM Seance s " +
           "WHERE s.formateur.id = :formateurId " +
           "AND s.date = :date " +
           "AND s.heure = :heure")
    boolean existsConflitFormateur(@Param("formateurId") String formateurId, 
                                   @Param("date") LocalDate date, 
                                   @Param("heure") LocalTime heure);
    
    /**
     * Trouve les séances d'un formateur à une date et heure spécifiques
     * @param formateurId l'ID du formateur
     * @param date la date
     * @param heure l'heure
     * @return liste des séances
     */
    @Query("SELECT s FROM Seance s " +
           "WHERE s.formateur.id = :formateurId " +
           "AND s.date = :date " +
           "AND s.heure = :heure")
    List<Seance> findByFormateurIdAndDateAndHeure(@Param("formateurId") String formateurId,
                                                   @Param("date") LocalDate date,
                                                   @Param("heure") LocalTime heure);
    
    /**
     * Trouve toutes les séances par date
     * @param date la date
     * @return liste des séances
     */
    List<Seance> findByDate(LocalDate date);
    
    /**
     * Trouve toutes les séances entre deux dates
     * @param dateDebut date de début
     * @param dateFin date de fin
     * @return liste des séances
     */
    @Query("SELECT s FROM Seance s WHERE s.date BETWEEN :dateDebut AND :dateFin ORDER BY s.date, s.heure")
    List<Seance> findByDateBetween(@Param("dateDebut") LocalDate dateDebut, 
                                    @Param("dateFin") LocalDate dateFin);
}

