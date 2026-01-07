package com.formation.app.controller.api;

import com.formation.app.entity.Etudiant;
import com.formation.app.entity.Inscription;
import com.formation.app.entity.Note;
import com.formation.app.service.EtudiantService;
import com.formation.app.service.InscriptionService;
import com.formation.app.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST pour la gestion des étudiants
 */
@RestController
@RequestMapping("/api/etudiants")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EtudiantRestController {
    
    private final EtudiantService etudiantService;
    private final InscriptionService inscriptionService;
    private final NoteService noteService;
    
    /**
     * Liste tous les étudiants
     * GET /api/etudiants
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR', 'ETUDIANT')")
    public ResponseEntity<List<Etudiant>> getAllEtudiants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<Etudiant> etudiants = etudiantService.getAllEtudiants();
        return ResponseEntity.ok(etudiants);
    }
    
    /**
     * Trouve un étudiant par son ID
     * GET /api/etudiants/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR', 'ETUDIANT')")
    public ResponseEntity<Etudiant> getEtudiantById(@PathVariable String id) {
        Etudiant etudiant = etudiantService.getEtudiantById(id);
        return ResponseEntity.ok(etudiant);
    }
    
    /**
     * Trouve un étudiant par son matricule
     * GET /api/etudiants/matricule/{matricule}
     */
    @GetMapping("/matricule/{matricule}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR', 'ETUDIANT')")
    public ResponseEntity<Etudiant> getEtudiantByMatricule(@PathVariable String matricule) {
        Etudiant etudiant = etudiantService.getEtudiantByMatricule(matricule);
        return ResponseEntity.ok(etudiant);
    }
    
    /**
     * Crée un nouvel étudiant
     * POST /api/etudiants
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Etudiant> createEtudiant(@RequestBody Etudiant etudiant) {
        Etudiant created = etudiantService.createEtudiant(etudiant);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    /**
     * Met à jour un étudiant
     * PUT /api/etudiants/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Etudiant> updateEtudiant(
            @PathVariable String id,
            @RequestBody Etudiant etudiant
    ) {
        Etudiant updated = etudiantService.updateEtudiant(id, etudiant);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * Supprime un étudiant
     * DELETE /api/etudiants/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEtudiant(@PathVariable String id) {
        etudiantService.deleteEtudiant(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Recherche des étudiants par nom
     * GET /api/etudiants/search/nom?nom=...
     */
    @GetMapping("/search/nom")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR', 'ETUDIANT')")
    public ResponseEntity<List<Etudiant>> searchByNom(@RequestParam String nom) {
        List<Etudiant> etudiants = etudiantService.searchByNom(nom);
        return ResponseEntity.ok(etudiants);
    }
    
    /**
     * Obtient toutes les inscriptions d'un étudiant
     * GET /api/etudiants/{id}/inscriptions
     */
    @GetMapping("/{id}/inscriptions")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR', 'ETUDIANT')")
    public ResponseEntity<List<Inscription>> getInscriptions(@PathVariable String id) {
        List<Inscription> inscriptions = inscriptionService.getInscriptionsByEtudiant(id);
        return ResponseEntity.ok(inscriptions);
    }
    
    /**
     * Obtient toutes les notes d'un étudiant
     * GET /api/etudiants/{id}/notes
     */
    @GetMapping("/{id}/notes")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR', 'ETUDIANT')")
    public ResponseEntity<List<Note>> getNotes(@PathVariable String id) {
        List<Note> notes = noteService.getNotesByEtudiant(id);
        return ResponseEntity.ok(notes);
    }
    
    /**
     * Obtient la moyenne générale d'un étudiant
     * GET /api/etudiants/{id}/moyenne
     */
    @GetMapping("/{id}/moyenne")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR', 'ETUDIANT')")
    public ResponseEntity<Map<String, Object>> getMoyenne(@PathVariable String id) {
        Double moyenne = noteService.calculerMoyenneGeneraleEtudiant(id);
        Map<String, Object> response = new HashMap<>();
        response.put("etudiantId", id);
        response.put("moyenneGenerale", moyenne);
        return ResponseEntity.ok(response);
    }
}

