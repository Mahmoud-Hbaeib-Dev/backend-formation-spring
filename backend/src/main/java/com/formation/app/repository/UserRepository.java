package com.formation.app.repository;

import com.formation.app.entity.Role;
import com.formation.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité User
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    /**
     * Trouve un utilisateur par son login
     * @param login le login de l'utilisateur
     * @return Optional contenant l'utilisateur si trouvé
     */
    Optional<User> findByLogin(String login);
    
    /**
     * Trouve tous les utilisateurs ayant un rôle spécifique
     * @param role le rôle recherché
     * @return liste des utilisateurs avec ce rôle
     */
    List<User> findByRoles(Role role);
    
    /**
     * Vérifie si un utilisateur existe avec ce login
     * @param login le login à vérifier
     * @return true si l'utilisateur existe
     */
    boolean existsByLogin(String login);
}

