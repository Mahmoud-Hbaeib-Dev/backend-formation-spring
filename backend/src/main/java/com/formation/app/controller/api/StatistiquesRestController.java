package com.formation.app.controller.api;

import com.formation.app.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller REST pour les statistiques et rapports
 */
@RestController
@RequestMapping("/api/statistiques")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StatistiquesRestController {
    
    private final ReportService reportService;
    
    /**
     * Obtient les statistiques du dashboard
     * GET /api/statistiques/dashboard
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR')")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Map<String, Object> stats = reportService.getStatistiquesCours();
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Obtient la liste des cours les plus suivis
     * GET /api/statistiques/cours-plus-suivis
     */
    @GetMapping("/cours-plus-suivis")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR')")
    public ResponseEntity<?> getCoursPlusSuivis() {
        return ResponseEntity.ok(reportService.getCoursPlusSuivis());
    }
    
    /**
     * Obtient le taux de réussite d'un cours
     * GET /api/statistiques/taux-reussite/{coursCode}
     */
    @GetMapping("/taux-reussite/{coursCode}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR')")
    public ResponseEntity<Map<String, Object>> getTauxReussite(@PathVariable String coursCode) {
        // Cette méthode nécessiterait NoteService
        // Pour l'instant, on retourne une structure vide
        return ResponseEntity.ok(Map.of("coursCode", coursCode, "tauxReussite", 0.0));
    }
}

