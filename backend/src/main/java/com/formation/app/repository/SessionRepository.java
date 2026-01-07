package com.formation.app.repository;

import com.formation.app.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Session
 */
@Repository
public interface SessionRepository extends JpaRepository<Session, String> {
    
    /**
     * Trouve toutes les sessions par année scolaire
     * @param anneeScolaire l'année scolaire (ex: "2024-2025")
     * @return liste des sessions pour cette année
     */
    List<Session> findByAnneeScolaire(String anneeScolaire);
    
    /**
     * Trouve toutes les sessions par semestre
     * @param semestre le semestre (ex: "S1", "S2")
     * @return liste des sessions pour ce semestre
     */
    List<Session> findBySemestre(String semestre);
    
    /**
     * Trouve une session par semestre et année scolaire
     * @param semestre le semestre
     * @param anneeScolaire l'année scolaire
     * @return Optional contenant la session si trouvée
     */
    Optional<Session> findBySemestreAndAnneeScolaire(String semestre, String anneeScolaire);
}

