package com.formation.app.controller.api;

import com.formation.app.entity.Etudiant;
import com.formation.app.service.EtudiantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour EtudiantRestController
 */
@WebMvcTest(EtudiantRestController.class)
class EtudiantRestControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private EtudiantService etudiantService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllEtudiants() throws Exception {
        // Given
        Etudiant etudiant1 = new Etudiant("ETU001", "MAT001", "Dupont", "Jean", "jean@email.com", LocalDate.now());
        Etudiant etudiant2 = new Etudiant("ETU002", "MAT002", "Martin", "Marie", "marie@email.com", LocalDate.now());
        List<Etudiant> etudiants = Arrays.asList(etudiant1, etudiant2);
        
        when(etudiantService.getAllEtudiants()).thenReturn(etudiants);
        
        // When & Then
        mockMvc.perform(get("/api/etudiants")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetEtudiantById() throws Exception {
        // Given
        Etudiant etudiant = new Etudiant("ETU001", "MAT001", "Dupont", "Jean", "jean@email.com", LocalDate.now());
        
        when(etudiantService.getEtudiantById("ETU001")).thenReturn(etudiant);
        
        // When & Then
        mockMvc.perform(get("/api/etudiants/ETU001")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("ETU001"))
                .andExpect(jsonPath("$.matricule").value("MAT001"));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateEtudiant() throws Exception {
        // Given
        Etudiant etudiant = new Etudiant("ETU001", "MAT001", "Dupont", "Jean", "jean@email.com", LocalDate.now());
        
        when(etudiantService.createEtudiant(any(Etudiant.class))).thenReturn(etudiant);
        
        // When & Then
        mockMvc.perform(post("/api/etudiants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(etudiant))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("ETU001"));
    }
}

