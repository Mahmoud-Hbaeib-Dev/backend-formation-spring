package com.formation.app.controller.web;

import com.formation.app.entity.Cours;
import com.formation.app.entity.Inscription;
import com.formation.app.service.CoursService;
import com.formation.app.service.EtudiantService;
import com.formation.app.service.InscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/inscriptions")
@RequiredArgsConstructor
public class InscriptionWebController {
    
    private final InscriptionService inscriptionService;
    private final EtudiantService etudiantService;
    private final CoursService coursService;
    
    @GetMapping
    public String listInscriptions(Model model) {
        // Récupérer toutes les inscriptions via les cours
        List<Cours> cours = coursService.getAllCours();
        List<Inscription> inscriptions = cours.stream()
                .flatMap(c -> inscriptionService.getInscriptionsByCours(c.getCode()).stream())
                .toList();
        model.addAttribute("inscriptions", inscriptions);
        model.addAttribute("title", "Gestion des Inscriptions");
        return "admin/inscriptions/list";
    }
    
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("inscription", new Inscription());
        model.addAttribute("etudiants", etudiantService.getAllEtudiants());
        model.addAttribute("cours", coursService.getAllCours());
        model.addAttribute("title", "Nouvelle Inscription");
        return "admin/inscriptions/form";
    }
    
    @PostMapping
    public String createInscription(
            @RequestParam String etudiantId,
            @RequestParam String coursCode,
            RedirectAttributes redirectAttributes
    ) {
        try {
            inscriptionService.inscrireEtudiant(etudiantId, coursCode);
            redirectAttributes.addFlashAttribute("message", "Inscription créée avec succès");
            return "redirect:/admin/inscriptions";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/admin/inscriptions/new";
        }
    }
    
    @PostMapping("/{id}/delete")
    public String deleteInscription(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            inscriptionService.desinscrireEtudiant(id);
            redirectAttributes.addFlashAttribute("message", "Inscription annulée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/admin/inscriptions";
    }
}

