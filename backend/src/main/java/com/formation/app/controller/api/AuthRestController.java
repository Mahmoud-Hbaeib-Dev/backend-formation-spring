package com.formation.app.controller.api;

import com.formation.app.entity.Etudiant;
import com.formation.app.entity.Formateur;
import com.formation.app.entity.Role;
import com.formation.app.entity.User;
import com.formation.app.repository.EtudiantRepository;
import com.formation.app.repository.FormateurRepository;
import com.formation.app.repository.UserRepository;
import com.formation.app.security.JwtTokenService;
import com.formation.app.security.UserDetailsImpl;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller REST pour l'authentification
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class AuthRestController {
    
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final EtudiantRepository etudiantRepository;
    private final FormateurRepository formateurRepository;
    
    /**
     * Endpoint de connexion
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        log.info("🔐 [AUTH API] Tentative de connexion reçue");
        log.info("📧 Login/Email: {}", request.getLogin());
        log.info("🔑 Password: {}", request.getPassword() != null ? "***" : "null");
        
        try {
            log.info("🔄 [AUTH API] Authentification en cours...");
            // Authentifier l'utilisateur
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getLogin(),
                    request.getPassword()
                )
            );
            log.info("✅ [AUTH API] Authentification réussie");
            
            log.info("📥 [AUTH API] Chargement des UserDetails...");
            // Charger les UserDetails
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getLogin());
            log.info("✅ [AUTH API] UserDetails chargés: {}", userDetails.getUsername());
            
            log.info("🎫 [AUTH API] Génération du token JWT...");
            // Générer le token JWT
            String token = jwtTokenService.generateToken(userDetails);
            log.info("✅ [AUTH API] Token généré");
            
            // Récupérer le rôle
            UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
            Role role = userDetailsImpl.getRole();
            log.info("👤 [AUTH API] Rôle détecté: {}", role);
            
            // Récupérer l'ID de l'étudiant/formateur si applicable
            String etudiantId = null;
            String formateurId = null;
            User user = userRepository.findById(userDetailsImpl.getUserId()).orElse(null);
            if (user != null) {
                if (role == Role.ETUDIANT) {
                    Etudiant etudiant = etudiantRepository.findByUser(user).orElse(null);
                    if (etudiant != null) {
                        etudiantId = etudiant.getId();
                        log.info("✅ [AUTH API] Étudiant ID trouvé: {}", etudiantId);
                    }
                } else if (role == Role.FORMATEUR) {
                    Formateur formateur = formateurRepository.findByUser(user).orElse(null);
                    if (formateur != null) {
                        formateurId = formateur.getId();
                        log.info("✅ [AUTH API] Formateur ID trouvé: {}", formateurId);
                    }
                }
            }
            
            // Construire la réponse
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("type", "Bearer");
            response.put("username", userDetails.getUsername());
            response.put("roles", new String[]{role.name()});
            response.put("userId", userDetailsImpl.getUserId());
            if (etudiantId != null) {
                response.put("etudiantId", etudiantId);
            }
            if (formateurId != null) {
                response.put("formateurId", formateurId);
            }
            
            log.info("✅ [AUTH API] Connexion réussie pour: {} (Rôle: {})", userDetails.getUsername(), role);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ [AUTH API] Erreur lors de la connexion: {}", e.getMessage());
            log.error("📊 [AUTH API] Type d'erreur: {}", e.getClass().getSimpleName());
            if (e.getCause() != null) {
                log.error("🔍 [AUTH API] Cause: {}", e.getCause().getMessage());
            }
            e.printStackTrace();
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid credentials");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(401).body(errorResponse);
        }
    }
    
    /**
     * Endpoint de test pour vérifier que le backend fonctionne
     * GET /api/auth/test
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Backend is running!");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint pour obtenir les informations de l'utilisateur connecté
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.substring(7); // Enlever "Bearer "
            String username = jwtTokenService.extractUsername(token);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
            Role role = userDetailsImpl.getRole();

            // Récupérer l'ID de l'étudiant/formateur si applicable
            String etudiantId = null;
            String formateurId = null;
            User user = userRepository.findById(userDetailsImpl.getUserId()).orElse(null);
            if (user != null) {
                if (role == Role.ETUDIANT) {
                    Etudiant etudiant = etudiantRepository.findByUser(user).orElse(null);
                    if (etudiant != null) {
                        etudiantId = etudiant.getId();
                    }
                } else if (role == Role.FORMATEUR) {
                    Formateur formateur = formateurRepository.findByUser(user).orElse(null);
                    if (formateur != null) {
                        formateurId = formateur.getId();
                    }
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("username", userDetails.getUsername());
            response.put("roles", userDetails.getAuthorities().stream()
                    .map(a -> a.getAuthority().replace("ROLE_", ""))
                    .toArray());
            response.put("userId", userDetailsImpl.getUserId());
            if (etudiantId != null) {
                response.put("etudiantId", etudiantId);
            }
            if (formateurId != null) {
                response.put("formateurId", formateurId);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid token");
            return ResponseEntity.status(401).body(errorResponse);
        }
    }
    
    /**
     * DTO pour la requête de login
     */
    @Data
    static class LoginRequest {
        private String login;
        private String password;
    }
}

