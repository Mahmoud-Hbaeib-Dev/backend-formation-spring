package com.formation.app.controller.api;

import com.formation.app.entity.Inscription;
import com.formation.app.service.InscriptionService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST pour la gestion des inscriptions
 */
@RestController
@RequestMapping("/api/inscriptions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InscriptionRestController {
    
    private final InscriptionService inscriptionService;
    
    /**
     * Liste toutes les inscriptions
     * GET /api/inscriptions
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR', 'ETUDIANT')")
    public ResponseEntity<List<Inscription>> getAllInscriptions() {
        // Cette méthode nécessiterait un repository pour lister toutes les inscriptions
        // Pour l'instant, on retourne une liste vide ou on peut filtrer par cours/étudiant
        return ResponseEntity.ok(List.of());
    }
    
    /**
     * Trouve une inscription par son ID
     * GET /api/inscriptions/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR', 'ETUDIANT')")
    public ResponseEntity<Inscription> getInscriptionById(@PathVariable String id) {
        // Cette méthode nécessiterait un repository pour trouver par ID
        // Pour l'instant, on retourne une erreur
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Inscrit un étudiant à un cours
     * POST /api/inscriptions
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR', 'ETUDIANT')")
    public ResponseEntity<Inscription> inscrireEtudiant(@RequestBody InscriptionRequest request) {
        Inscription inscription = inscriptionService.inscrireEtudiant(
                request.getEtudiantId(),
                request.getCoursCode()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(inscription);
    }
    
    /**
     * Désinscrit un étudiant d'un cours
     * DELETE /api/inscriptions/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR', 'ETUDIANT')")
    public ResponseEntity<Void> desinscrireEtudiant(@PathVariable String id) {
        inscriptionService.desinscrireEtudiant(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Trouve toutes les inscriptions d'un étudiant
     * GET /api/inscriptions/etudiant/{etudiantId}
     */
    @GetMapping("/etudiant/{etudiantId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR', 'ETUDIANT')")
    public ResponseEntity<List<Inscription>> getInscriptionsByEtudiant(@PathVariable String etudiantId) {
        List<Inscription> inscriptions = inscriptionService.getInscriptionsByEtudiant(etudiantId);
        return ResponseEntity.ok(inscriptions);
    }
    
    /**
     * Trouve toutes les inscriptions d'un cours
     * GET /api/inscriptions/cours/{coursCode}
     */
    @GetMapping("/cours/{coursCode}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR', 'ETUDIANT')")
    public ResponseEntity<List<Inscription>> getInscriptionsByCours(@PathVariable String coursCode) {
        List<Inscription> inscriptions = inscriptionService.getInscriptionsByCours(coursCode);
        return ResponseEntity.ok(inscriptions);
    }
    
    /**
     * DTO pour la requête d'inscription
     */
    @Data
    static class InscriptionRequest {
        private String etudiantId;
        private String coursCode;
    }
}

