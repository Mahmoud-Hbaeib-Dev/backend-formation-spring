package com.formation.app.service;

import com.formation.app.entity.Etudiant;
import com.formation.app.exception.BadRequestException;
import com.formation.app.repository.EtudiantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour EtudiantService
 */
@ExtendWith(MockitoExtension.class)
class EtudiantServiceTest {
    
    @Mock
    private EtudiantRepository etudiantRepository;
    
    @InjectMocks
    private EtudiantService etudiantService;
    
    private Etudiant testEtudiant;
    
    @BeforeEach
    void setUp() {
        testEtudiant = new Etudiant();
        testEtudiant.setId("ETU001");
        testEtudiant.setMatricule("MAT001");
        testEtudiant.setNom("Dupont");
        testEtudiant.setPrenom("Jean");
        testEtudiant.setEmail("jean@email.com");
        testEtudiant.setDateInscription(LocalDate.now());
    }
    
    @Test
    void testCreateEtudiant_Success() {
        // Given
        when(etudiantRepository.existsByMatricule("MAT001")).thenReturn(false);
        when(etudiantRepository.existsByEmail("jean@email.com")).thenReturn(false);
        when(etudiantRepository.save(any(Etudiant.class))).thenReturn(testEtudiant);
        
        // When
        Etudiant created = etudiantService.createEtudiant(testEtudiant);
        
        // Then
        assertNotNull(created);
        verify(etudiantRepository, times(1)).save(any(Etudiant.class));
    }
    
    @Test
    void testCreateEtudiant_DuplicateMatricule() {
        // Given
        when(etudiantRepository.existsByMatricule("MAT001")).thenReturn(true);
        
        // When & Then
        assertThrows(BadRequestException.class, () -> {
            etudiantService.createEtudiant(testEtudiant);
        });
    }
    
    @Test
    void testGetEtudiantById_Success() {
        // Given
        when(etudiantRepository.findById("ETU001")).thenReturn(Optional.of(testEtudiant));
        
        // When
        Etudiant found = etudiantService.getEtudiantById("ETU001");
        
        // Then
        assertNotNull(found);
        assertEquals("ETU001", found.getId());
        assertEquals("MAT001", found.getMatricule());
    }
    
    @Test
    void testGetEtudiantByMatricule_Success() {
        // Given
        when(etudiantRepository.findByMatricule("MAT001")).thenReturn(Optional.of(testEtudiant));
        
        // When
        Etudiant found = etudiantService.getEtudiantByMatricule("MAT001");
        
        // Then
        assertNotNull(found);
        assertEquals("MAT001", found.getMatricule());
    }
}

