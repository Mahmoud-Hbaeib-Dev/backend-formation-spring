package com.formation.app.repository;

import com.formation.app.entity.Etudiant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intégration pour EtudiantRepository
 */
@DataJpaTest
@ActiveProfiles("test")
class EtudiantRepositoryTest {
    
    @Autowired
    private EtudiantRepository etudiantRepository;
    
    @Test
    void testSaveAndFindById() {
        // Given
        Etudiant etudiant = new Etudiant("ETU001", "MAT001", "Dupont", "Jean", "jean@email.com", LocalDate.now());
        
        // When
        Etudiant saved = etudiantRepository.save(etudiant);
        Optional<Etudiant> found = etudiantRepository.findById(saved.getId());
        
        // Then
        assertTrue(found.isPresent());
        assertEquals("MAT001", found.get().getMatricule());
        assertEquals("Dupont", found.get().getNom());
    }
    
    @Test
    void testFindByMatricule() {
        // Given
        Etudiant etudiant = new Etudiant("ETU001", "MAT001", "Dupont", "Jean", "jean@email.com", LocalDate.now());
        etudiantRepository.save(etudiant);
        
        // When
        Optional<Etudiant> found = etudiantRepository.findByMatricule("MAT001");
        
        // Then
        assertTrue(found.isPresent());
        assertEquals("MAT001", found.get().getMatricule());
    }
    
    @Test
    void testFindByEmail() {
        // Given
        Etudiant etudiant = new Etudiant("ETU001", "MAT001", "Dupont", "Jean", "jean@email.com", LocalDate.now());
        etudiantRepository.save(etudiant);
        
        // When
        Optional<Etudiant> found = etudiantRepository.findByEmail("jean@email.com");
        
        // Then
        assertTrue(found.isPresent());
        assertEquals("jean@email.com", found.get().getEmail());
    }
    
    @Test
    void testFindByNomContainingIgnoreCase() {
        // Given
        Etudiant etudiant1 = new Etudiant("ETU001", "MAT001", "Dupont", "Jean", "jean@email.com", LocalDate.now());
        Etudiant etudiant2 = new Etudiant("ETU002", "MAT002", "Dupuis", "Marie", "marie@email.com", LocalDate.now());
        etudiantRepository.save(etudiant1);
        etudiantRepository.save(etudiant2);
        
        // When
        List<Etudiant> found = etudiantRepository.findByNomContainingIgnoreCase("dup");
        
        // Then
        assertFalse(found.isEmpty());
        assertTrue(found.size() >= 2);
    }
}

