package com.formation.app.controller.api;

import com.formation.app.entity.Etudiant;
import com.formation.app.entity.Formateur;
import com.formation.app.entity.User;
import com.formation.app.repository.EtudiantRepository;
import com.formation.app.repository.FormateurRepository;
import com.formation.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller REST pour le diagnostic et le débogage
 */
@RestController
@RequestMapping("/api/diagnostic")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class DiagnosticRestController {

    private final UserRepository userRepository;
    private final EtudiantRepository etudiantRepository;
    private final FormateurRepository formateurRepository;

    /**
     * Endpoint pour vérifier l'état de la base de données
     * GET /api/diagnostic/status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        log.info("🔍 [DIAGNOSTIC] Vérification de l'état de la base de données...");
        
        Map<String, Object> status = new HashMap<>();
        
        // Compter les Users
        long userCount = userRepository.count();
        status.put("totalUsers", userCount);
        
        // Compter les Étudiants
        long etudiantCount = etudiantRepository.count();
        status.put("totalEtudiants", etudiantCount);
        
        // Compter les Formateurs
        long formateurCount = formateurRepository.count();
        status.put("totalFormateurs", formateurCount);
        
        // Vérifier les étudiants sans User
        List<Etudiant> etudiantsSansUser = etudiantRepository.findAll().stream()
            .filter(e -> e.getUser() == null)
            .collect(Collectors.toList());
        status.put("etudiantsSansUser", etudiantsSansUser.size());
        status.put("etudiantsSansUserDetails", etudiantsSansUser.stream()
            .map(e -> Map.of(
                "id", e.getId() != null ? e.getId() : "null",
                "matricule", e.getMatricule(),
                "nom", e.getNom(),
                "prenom", e.getPrenom(),
                "email", e.getEmail()
            ))
            .collect(Collectors.toList()));
        
        // Vérifier les formateurs sans User
        List<Formateur> formateursSansUser = formateurRepository.findAll().stream()
            .filter(f -> f.getUser() == null)
            .collect(Collectors.toList());
        status.put("formateursSansUser", formateursSansUser.size());
        status.put("formateursSansUserDetails", formateursSansUser.stream()
            .map(f -> Map.of(
                "id", f.getId() != null ? f.getId() : "null",
                "nom", f.getNom(),
                "email", f.getEmail()
            ))
            .collect(Collectors.toList()));
        
        // Lister tous les Users avec leurs rôles
        List<Map<String, Object>> users = userRepository.findAll().stream()
            .map(u -> {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", u.getId());
                userMap.put("login", u.getLogin());
                userMap.put("role", u.getRoles().name());
                return userMap;
            })
            .collect(Collectors.toList());
        status.put("users", users);
        
        // Lister les étudiants avec leurs Users
        List<Map<String, Object>> etudiantsAvecUser = etudiantRepository.findAll().stream()
            .map(e -> {
                Map<String, Object> etudiantMap = new HashMap<>();
                etudiantMap.put("id", e.getId());
                etudiantMap.put("matricule", e.getMatricule());
                etudiantMap.put("nom", e.getNom());
                etudiantMap.put("prenom", e.getPrenom());
                etudiantMap.put("email", e.getEmail());
                etudiantMap.put("hasUser", e.getUser() != null);
                if (e.getUser() != null) {
                    etudiantMap.put("userLogin", e.getUser().getLogin());
                    etudiantMap.put("userRole", e.getUser().getRoles().name());
                }
                return etudiantMap;
            })
            .collect(Collectors.toList());
        status.put("etudiantsAvecUser", etudiantsAvecUser);
        
        log.info("✅ [DIAGNOSTIC] État de la base de données récupéré");
        return ResponseEntity.ok(status);
    }

    /**
     * Endpoint pour tester la recherche d'un utilisateur par email
     * GET /api/diagnostic/test-user?email=ahmed@email.com
     */
    @GetMapping("/test-user")
    public ResponseEntity<Map<String, Object>> testUser(@RequestParam String email) {
        log.info("🔍 [DIAGNOSTIC] Test de recherche d'utilisateur avec: {}", email);
        
        Map<String, Object> result = new HashMap<>();
        result.put("email", email);
        
        // 1. Chercher par login
        User userByLogin = userRepository.findByLogin(email).orElse(null);
        result.put("foundByLogin", userByLogin != null);
        if (userByLogin != null) {
            result.put("userByLogin", Map.of(
                "id", userByLogin.getId(),
                "login", userByLogin.getLogin(),
                "role", userByLogin.getRoles().name()
            ));
        }
        
        // 2. Chercher par email (Etudiant)
        Etudiant etudiant = etudiantRepository.findByEmail(email).orElse(null);
        result.put("foundEtudiant", etudiant != null);
        if (etudiant != null) {
            result.put("etudiant", Map.of(
                "id", etudiant.getId() != null ? etudiant.getId() : "null",
                "matricule", etudiant.getMatricule(),
                "nom", etudiant.getNom(),
                "prenom", etudiant.getPrenom(),
                "email", etudiant.getEmail(),
                "hasUser", etudiant.getUser() != null
            ));
            if (etudiant.getUser() != null) {
                result.put("etudiantUser", Map.of(
                    "id", etudiant.getUser().getId(),
                    "login", etudiant.getUser().getLogin(),
                    "role", etudiant.getUser().getRoles().name()
                ));
            }
        }
        
        // 3. Chercher par email (Formateur)
        Formateur formateur = formateurRepository.findByEmail(email).orElse(null);
        result.put("foundFormateur", formateur != null);
        if (formateur != null) {
            result.put("formateur", Map.of(
                "id", formateur.getId() != null ? formateur.getId() : "null",
                "nom", formateur.getNom(),
                "email", formateur.getEmail(),
                "hasUser", formateur.getUser() != null
            ));
            if (formateur.getUser() != null) {
                result.put("formateurUser", Map.of(
                    "id", formateur.getUser().getId(),
                    "login", formateur.getUser().getLogin(),
                    "role", formateur.getUser().getRoles().name()
                ));
            }
        }
        
        log.info("✅ [DIAGNOSTIC] Test de recherche terminé");
        return ResponseEntity.ok(result);
    }
}

