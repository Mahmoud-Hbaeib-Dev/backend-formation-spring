package com.formation.app.repository;

import com.formation.app.entity.Role;
import com.formation.app.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intégration pour UserRepository
 */
@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void testSaveAndFindById() {
        // Given
        User user = new User("USER001", "testuser", "password", Role.ADMIN);
        
        // When
        User saved = userRepository.save(user);
        Optional<User> found = userRepository.findById(saved.getId());
        
        // Then
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getLogin());
        assertEquals(Role.ADMIN, found.get().getRoles());
    }
    
    @Test
    void testFindByLogin() {
        // Given
        User user = new User("USER001", "testuser", "password", Role.ADMIN);
        userRepository.save(user);
        
        // When
        Optional<User> found = userRepository.findByLogin("testuser");
        
        // Then
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getLogin());
    }
    
    @Test
    void testFindByRoles() {
        // Given
        User admin = new User("USER001", "admin", "password", Role.ADMIN);
        User formateur = new User("USER002", "formateur", "password", Role.FORMATEUR);
        userRepository.save(admin);
        userRepository.save(formateur);
        
        // When
        List<User> admins = userRepository.findByRoles(Role.ADMIN);
        
        // Then
        assertFalse(admins.isEmpty());
        assertTrue(admins.stream().allMatch(u -> u.getRoles() == Role.ADMIN));
    }
    
    @Test
    void testExistsByLogin() {
        // Given
        User user = new User("USER001", "testuser", "password", Role.ADMIN);
        userRepository.save(user);
        
        // When
        boolean exists = userRepository.existsByLogin("testuser");
        boolean notExists = userRepository.existsByLogin("nonexistent");
        
        // Then
        assertTrue(exists);
        assertFalse(notExists);
    }
}

