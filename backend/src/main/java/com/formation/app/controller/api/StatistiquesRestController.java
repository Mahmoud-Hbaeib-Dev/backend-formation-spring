package com.formation.app.controller.api;

import com.formation.app.entity.Cours;
import com.formation.app.entity.Etudiant;
import com.formation.app.entity.Role;
import com.formation.app.entity.User;
import com.formation.app.exception.ResourceNotFoundException;
import com.formation.app.repository.EtudiantRepository;
import com.formation.app.repository.UserRepository;
import com.formation.app.security.UserDetailsImpl;
import com.formation.app.service.CoursService;
import com.formation.app.service.EtudiantService;
import com.formation.app.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final EtudiantService etudiantService;
    private final CoursService coursService;
    private final UserRepository userRepository;
    private final EtudiantRepository etudiantRepository;
    
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
    
    /**
     * Génère un rapport PDF de notes pour un étudiant
     * GET /api/statistiques/rapport-notes/{etudiantId}
     */
    @GetMapping("/rapport-notes/{etudiantId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR', 'ETUDIANT')")
    public ResponseEntity<byte[]> genererRapportNotesPDF(@PathVariable String etudiantId) {
        Etudiant etudiant = etudiantService.getEtudiantById(etudiantId);
        if (etudiant == null) {
            throw new ResourceNotFoundException("Étudiant non trouvé avec l'ID: " + etudiantId);
        }
        
        // Vérifier que l'étudiant peut seulement accéder à son propre rapport
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Role role = userDetails.getRole();
            
            // Si c'est un étudiant, vérifier qu'il accède à son propre rapport
            if (role == Role.ETUDIANT) {
                User user = userRepository.findById(userDetails.getUserId()).orElse(null);
                if (user != null) {
                    Etudiant currentEtudiant = etudiantRepository.findByUser(user).orElse(null);
                    if (currentEtudiant == null || !currentEtudiant.getId().equals(etudiantId)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }
                }
            }
        }
        
        byte[] pdfBytes = reportService.genererRapportNotes(etudiant);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", 
            "rapport-notes-" + etudiant.getMatricule() + ".pdf");
        headers.setContentLength(pdfBytes.length);
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(pdfBytes);
    }
    
    /**
     * Génère un rapport PDF pour un cours
     * GET /api/statistiques/rapport-cours/{coursCode}
     */
    @GetMapping("/rapport-cours/{coursCode}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR')")
    public ResponseEntity<byte[]> genererRapportCoursPDF(@PathVariable String coursCode) {
        Cours cours = coursService.getCoursByCode(coursCode);
        if (cours == null) {
            throw new ResourceNotFoundException("Cours non trouvé avec le code: " + coursCode);
        }
        
        byte[] pdfBytes = reportService.genererRapportCours(cours);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", 
            "rapport-cours-" + cours.getCode() + ".pdf");
        headers.setContentLength(pdfBytes.length);
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(pdfBytes);
    }
}

