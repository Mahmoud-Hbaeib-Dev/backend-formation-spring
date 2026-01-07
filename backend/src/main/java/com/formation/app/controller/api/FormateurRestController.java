package com.formation.app.controller.api;

import com.formation.app.entity.Cours;
import com.formation.app.entity.Formateur;
import com.formation.app.service.CoursService;
import com.formation.app.service.FormateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST pour la gestion des formateurs
 */
@RestController
@RequestMapping("/api/formateurs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FormateurRestController {
    
    private final FormateurService formateurService;
    private final CoursService coursService;
    
    /**
     * Liste tous les formateurs
     * GET /api/formateurs
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR')")
    public ResponseEntity<List<Formateur>> getAllFormateurs() {
        List<Formateur> formateurs = formateurService.getAllFormateurs();
        return ResponseEntity.ok(formateurs);
    }
    
    /**
     * Trouve un formateur par son ID
     * GET /api/formateurs/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR')")
    public ResponseEntity<Formateur> getFormateurById(@PathVariable String id) {
        Formateur formateur = formateurService.getFormateurById(id);
        return ResponseEntity.ok(formateur);
    }
    
    /**
     * Crée un nouveau formateur
     * POST /api/formateurs
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Formateur> createFormateur(@RequestBody Formateur formateur) {
        Formateur created = formateurService.createFormateur(formateur);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    /**
     * Met à jour un formateur
     * PUT /api/formateurs/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Formateur> updateFormateur(
            @PathVariable String id,
            @RequestBody Formateur formateur
    ) {
        Formateur updated = formateurService.updateFormateur(id, formateur);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * Supprime un formateur
     * DELETE /api/formateurs/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFormateur(@PathVariable String id) {
        formateurService.deleteFormateur(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Trouve tous les cours d'un formateur
     * GET /api/formateurs/{id}/cours
     */
    @GetMapping("/{id}/cours")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR')")
    public ResponseEntity<List<Cours>> getCoursByFormateur(@PathVariable String id) {
        List<Cours> cours = coursService.getCoursByFormateur(id);
        return ResponseEntity.ok(cours);
    }
    
    /**
     * Trouve tous les formateurs d'une spécialité
     * GET /api/formateurs/specialite/{specialite}
     */
    @GetMapping("/specialite/{specialite}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR')")
    public ResponseEntity<List<Formateur>> getFormateursBySpecialite(@PathVariable String specialite) {
        List<Formateur> formateurs = formateurService.getFormateursBySpecialite(specialite);
        return ResponseEntity.ok(formateurs);
    }
}

