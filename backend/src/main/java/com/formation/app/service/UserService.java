package com.formation.app.service;

import com.formation.app.entity.Role;
import com.formation.app.entity.User;
import com.formation.app.exception.BadRequestException;
import com.formation.app.exception.ResourceNotFoundException;
import com.formation.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service pour la gestion des utilisateurs
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Crée un nouvel utilisateur
     */
    public User createUser(String login, String password, Role role) {
        if (userRepository.existsByLogin(login)) {
            throw new BadRequestException("Un utilisateur avec ce login existe déjà");
        }
        
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setLogin(login);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(role);
        
        return userRepository.save(user);
    }
    
    /**
     * Trouve un utilisateur par son ID
     */
    @Transactional(readOnly = true)
    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }
    
    /**
     * Trouve un utilisateur par son login
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserByLogin(String login) {
        return userRepository.findByLogin(login);
    }
    
    /**
     * Trouve tous les utilisateurs d'un rôle
     */
    @Transactional(readOnly = true)
    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRoles(role);
    }
    
    /**
     * Change le mot de passe d'un utilisateur
     */
    public void changePassword(String userId, String newPassword) {
        User user = getUserById(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    /**
     * Vérifie les credentials d'un utilisateur
     */
    @Transactional(readOnly = true)
    public boolean authenticate(String login, String password) {
        Optional<User> userOpt = userRepository.findByLogin(login);
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        return passwordEncoder.matches(password, user.getPassword());
    }
    
    /**
     * Supprime un utilisateur
     */
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
    }
}

