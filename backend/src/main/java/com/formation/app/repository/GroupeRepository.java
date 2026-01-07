package com.formation.app.repository;

import com.formation.app.entity.Groupe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Groupe
 */
@Repository
public interface GroupeRepository extends JpaRepository<Groupe, String> {
    
    /**
     * Trouve un groupe par son nom
     * @param nom le nom du groupe
     * @return Optional contenant le groupe si trouvé
     */
    Optional<Groupe> findByNom(String nom);
    
    /**
     * Trouve tous les groupes par nom (recherche partielle)
     * @param nom le nom à rechercher
     * @return liste des groupes correspondants
     */
    List<Groupe> findByNomContainingIgnoreCase(String nom);
    
    /**
     * Vérifie si un groupe existe avec ce nom
     * @param nom le nom à vérifier
     * @return true si le groupe existe
     */
    boolean existsByNom(String nom);
}

