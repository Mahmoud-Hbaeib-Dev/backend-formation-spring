package com.formation.app.controller.api;

import com.formation.app.entity.Cours;
import com.formation.app.entity.Groupe;
import com.formation.app.entity.Inscription;
import com.formation.app.entity.Note;
import com.formation.app.service.CoursService;
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
 * Controller REST pour la gestion des cours
 */
@RestController
@RequestMapping("/api/cours")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CoursRestController {
    
    private final CoursService coursService;
    private final InscriptionService inscriptionService;
    private final NoteService noteService;
    
    /**
     * Liste tous les cours
     * GET /api/cours
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR', 'ETUDIANT')")
    public ResponseEntity<List<Cours>> getAllCours() {
        List<Cours> cours = coursService.getAllCours();
        return ResponseEntity.ok(cours);
    }
    
    /**
     * Trouve un cours par son code
     * GET /api/cours/{code}
     */
    @GetMapping("/{code}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR', 'ETUDIANT')")
    public ResponseEntity<Cours> getCoursByCode(@PathVariable String code) {
        Cours cours = coursService.getCoursByCode(code);
        return ResponseEntity.ok(cours);
    }
    
    /**
     * Crée un nouveau cours
     * POST /api/cours
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR')")
    public ResponseEntity<Cours> createCours(@RequestBody Cours cours) {
        Cours created = coursService.createCours(cours);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    /**
     * Met à jour un cours
     * PUT /api/cours/{code}
     */
    @PutMapping("/{code}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR')")
    public ResponseEntity<Cours> updateCours(
            @PathVariable String code,
            @RequestBody Cours cours
    ) {
        Cours updated = coursService.updateCours(code, cours);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * Supprime un cours
     * DELETE /api/cours/{code}
     */
    @DeleteMapping("/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCours(@PathVariable String code) {
        coursService.deleteCours(code);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Trouve tous les étudiants inscrits à un cours
     * GET /api/cours/{code}/etudiants
     */
    @GetMapping("/{code}/etudiants")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR', 'ETUDIANT')")
    public ResponseEntity<List<Inscription>> getEtudiantsByCours(@PathVariable String code) {
        List<Inscription> inscriptions = inscriptionService.getInscriptionsByCours(code);
        return ResponseEntity.ok(inscriptions);
    }
    
    /**
     * Trouve toutes les notes d'un cours
     * GET /api/cours/{code}/notes
     */
    @GetMapping("/{code}/notes")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR')")
    public ResponseEntity<List<Note>> getNotesByCours(@PathVariable String code) {
        List<Note> notes = noteService.getNotesByCours(code);
        return ResponseEntity.ok(notes);
    }
    
    /**
     * Obtient les statistiques d'un cours
     * GET /api/cours/{code}/statistiques
     */
    @GetMapping("/{code}/statistiques")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR')")
    public ResponseEntity<Map<String, Object>> getStatistiques(@PathVariable String code) {
        Cours cours = coursService.getCoursByCode(code);
        
        Double moyenne = noteService.calculerMoyenneCours(code);
        Double tauxReussite = noteService.calculerTauxReussite(code);
        long nbInscriptions = inscriptionService.countActiveInscriptions(code);
        long nbEtudiantsNotes = noteService.countEtudiantsNotes(code);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("coursCode", code);
        stats.put("coursTitre", cours.getTitre());
        stats.put("moyenne", moyenne);
        stats.put("tauxReussite", tauxReussite);
        stats.put("nombreInscriptions", nbInscriptions);
        stats.put("nombreEtudiantsNotes", nbEtudiantsNotes);
        
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Trouve tous les groupes d'un cours
     * GET /api/cours/{code}/groupes
     */
    @GetMapping("/{code}/groupes")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR')")
    public ResponseEntity<List<Groupe>> getGroupesByCours(@PathVariable String code) {
        List<Groupe> groupes = coursService.getGroupesByCours(code);
        return ResponseEntity.ok(groupes);
    }
    
    /**
     * Recherche des cours par titre
     * GET /api/cours/search/titre?titre=...
     */
    @GetMapping("/search/titre")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR', 'ETUDIANT')")
    public ResponseEntity<List<Cours>> searchByTitre(@RequestParam String titre) {
        List<Cours> cours = coursService.searchByTitre(titre);
        return ResponseEntity.ok(cours);
    }
}

