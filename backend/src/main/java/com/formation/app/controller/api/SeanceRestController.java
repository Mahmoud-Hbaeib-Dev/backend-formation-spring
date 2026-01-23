package com.formation.app.controller.api;

import com.formation.app.entity.Cours;
import com.formation.app.entity.Formateur;
import com.formation.app.entity.Role;
import com.formation.app.entity.Seance;
import com.formation.app.entity.User;
import com.formation.app.repository.CoursRepository;
import com.formation.app.repository.FormateurRepository;
import com.formation.app.repository.UserRepository;
import com.formation.app.security.UserDetailsImpl;
import com.formation.app.service.SeanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controller REST pour la gestion des séances
 */
@RestController
@RequestMapping("/api/seances")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SeanceRestController {
    
    private final SeanceService seanceService;
    private final CoursRepository coursRepository;
    private final FormateurRepository formateurRepository;
    private final UserRepository userRepository;
    
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
    public ResponseEntity<Seance> createSeance(@RequestBody Map<String, Object> request) {
        // Créer une nouvelle séance à partir des données de la requête
        Seance seance = new Seance();
        seance.setDate(LocalDate.parse((String) request.get("date")));
        seance.setHeure(java.time.LocalTime.parse((String) request.get("heure")));
        seance.setSalle((String) request.get("salle"));
        
        // Extraire le cours depuis la requête
        Map<String, Object> coursMap = (Map<String, Object>) request.get("cours");
        if (coursMap != null && coursMap.get("code") != null) {
            String coursCode = (String) coursMap.get("code");
            Cours cours = coursRepository.findByCode(coursCode)
                    .orElseThrow(() -> new com.formation.app.exception.ResourceNotFoundException("Cours", "code", coursCode));
            seance.setCours(cours);
        } else {
            throw new com.formation.app.exception.BadRequestException("Le cours est obligatoire");
        }
        
        // Extraire le formateur depuis la requête ou utiliser l'utilisateur authentifié
        Map<String, Object> formateurMap = (Map<String, Object>) request.get("formateur");
        Formateur formateur = null;
        
        if (formateurMap != null && formateurMap.get("id") != null) {
            String formateurId = (String) formateurMap.get("id");
            formateur = formateurRepository.findById(formateurId)
                    .orElseThrow(() -> new com.formation.app.exception.ResourceNotFoundException("Formateur", "id", formateurId));
        } else {
            // Si le formateur n'est pas fourni, l'assigner automatiquement depuis l'utilisateur authentifié
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                Role role = userDetails.getRole();
                
                if (role == Role.FORMATEUR) {
                    User user = userRepository.findById(userDetails.getUserId()).orElse(null);
                    if (user != null) {
                        formateur = formateurRepository.findByUser(user).orElse(null);
                    }
                }
            }
        }
        
        if (formateur == null) {
            throw new com.formation.app.exception.BadRequestException("Le formateur est obligatoire");
        }
        
        seance.setFormateur(formateur);
        
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
        System.out.println("🔍 [SEANCE API] FormateurId recherché: " + formateurId);
        System.out.println("🔍 [SEANCE API] Nombre de séances trouvées: " + seances.size());
        if (!seances.isEmpty()) {
            System.out.println("🔍 [SEANCE API] Première séance - ID: " + seances.get(0).getId() + ", Formateur ID: " + (seances.get(0).getFormateur() != null ? seances.get(0).getFormateur().getId() : "null"));
        }
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

