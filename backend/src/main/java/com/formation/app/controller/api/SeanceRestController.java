package com.formation.app.controller.api;

import com.formation.app.entity.Seance;
import com.formation.app.service.SeanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller REST pour la gestion des séances
 */
@RestController
@RequestMapping("/api/seances")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SeanceRestController {
    
    private final SeanceService seanceService;
    
    /**
     * Liste toutes les séances
     * GET /api/seances
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR', 'ETUDIANT')")
    public ResponseEntity<List<Seance>> getAllSeances() {
        // Cette méthode nécessiterait un repository pour lister toutes les séances
        return ResponseEntity.ok(List.of());
    }
    
    /**
     * Trouve une séance par son ID
     * GET /api/seances/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR', 'ETUDIANT')")
    public ResponseEntity<Seance> getSeanceById(@PathVariable String id) {
        Seance seance = seanceService.getSeanceById(id);
        return ResponseEntity.ok(seance);
    }
    
    /**
     * Crée une nouvelle séance
     * POST /api/seances
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR')")
    public ResponseEntity<Seance> createSeance(@RequestBody Seance seance) {
        Seance created = seanceService.createSeance(seance);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    /**
     * Met à jour une séance
     * PUT /api/seances/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR')")
    public ResponseEntity<Seance> updateSeance(
            @PathVariable String id,
            @RequestBody Seance seance
    ) {
        Seance updated = seanceService.updateSeance(id, seance);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * Supprime une séance
     * DELETE /api/seances/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR')")
    public ResponseEntity<Void> deleteSeance(@PathVariable String id) {
        seanceService.deleteSeance(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Trouve toutes les séances d'un cours
     * GET /api/seances/cours/{coursCode}
     */
    @GetMapping("/cours/{coursCode}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR', 'ETUDIANT')")
    public ResponseEntity<List<Seance>> getSeancesByCours(@PathVariable String coursCode) {
        List<Seance> seances = seanceService.getSeancesByCours(coursCode);
        return ResponseEntity.ok(seances);
    }
    
    /**
     * Trouve toutes les séances d'un formateur
     * GET /api/seances/formateur/{formateurId}
     */
    @GetMapping("/formateur/{formateurId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR')")
    public ResponseEntity<List<Seance>> getSeancesByFormateur(@PathVariable String formateurId) {
        List<Seance> seances = seanceService.getSeancesByFormateur(formateurId);
        return ResponseEntity.ok(seances);
    }
    
    /**
     * Trouve l'emploi du temps d'un étudiant
     * GET /api/seances/etudiant/{etudiantId}
     */
    @GetMapping("/etudiant/{etudiantId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR', 'ETUDIANT')")
    public ResponseEntity<List<Seance>> getEmploiDuTempsEtudiant(@PathVariable String etudiantId) {
        List<Seance> seances = seanceService.getEmploiDuTempsEtudiant(etudiantId);
        return ResponseEntity.ok(seances);
    }
    
    /**
     * Trouve toutes les séances d'une date
     * GET /api/seances/date?date=2024-01-15
     */
    @GetMapping("/date")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR', 'ETUDIANT')")
    public ResponseEntity<List<Seance>> getSeancesByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<Seance> seances = seanceService.getSeancesByDate(date);
        return ResponseEntity.ok(seances);
    }
    
    /**
     * Trouve toutes les séances entre deux dates
     * GET /api/seances/date-between?dateDebut=2024-01-01&dateFin=2024-01-31
     */
    @GetMapping("/date-between")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR', 'ETUDIANT')")
    public ResponseEntity<List<Seance>> getSeancesBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin
    ) {
        List<Seance> seances = seanceService.getSeancesBetweenDates(dateDebut, dateFin);
        return ResponseEntity.ok(seances);
    }
}

