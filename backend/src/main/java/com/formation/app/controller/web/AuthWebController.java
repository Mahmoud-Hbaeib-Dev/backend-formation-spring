package com.formation.app.controller.web;

import com.formation.app.service.EtudiantService;
import com.formation.app.service.FormateurService;
import com.formation.app.service.CoursService;
import com.formation.app.service.InscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller web pour l'authentification (Thymeleaf)
 */
@Controller
@RequiredArgsConstructor
public class AuthWebController {
    
    private final EtudiantService etudiantService;
    private final FormateurService formateurService;
    private final CoursService coursService;
    private final InscriptionService inscriptionService;
    
    /**
     * Route racine - redirige vers login ou dashboard
     * GET /
     */
    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }
    
    /**
     * Page de connexion
     * GET /login
     */
    @GetMapping("/login")
    public String login(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Model model
    ) {
        if (error != null) {
            model.addAttribute("error", "Nom d'utilisateur ou mot de passe incorrect");
        }
        if (logout != null) {
            model.addAttribute("message", "Vous avez été déconnecté avec succès");
        }
        return "login";
    }
    
    /**
     * Page d'accueil admin (dashboard)
     * GET /admin/dashboard
     */
    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("etudiants", (long) etudiantService.getAllEtudiants().size());
        stats.put("formateurs", (long) formateurService.getAllFormateurs().size());
        stats.put("cours", (long) coursService.getAllCours().size());
        // Compter les inscriptions actives
        long nbInscriptions = coursService.getAllCours().stream()
                .mapToLong(c -> inscriptionService.countActiveInscriptions(c.getCode()))
                .sum();
        stats.put("inscriptions", nbInscriptions);
        
        model.addAttribute("stats", stats);
        model.addAttribute("title", "Dashboard");
        return "admin/dashboard";
    }
}

