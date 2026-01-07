package com.formation.app.controller.web;

import com.formation.app.entity.Etudiant;
import com.formation.app.service.EtudiantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller web pour la gestion des étudiants (Thymeleaf)
 */
@Controller
@RequestMapping("/admin/etudiants")
@RequiredArgsConstructor
public class EtudiantWebController {
    
    private final EtudiantService etudiantService;
    
    /**
     * Liste tous les étudiants
     * GET /admin/etudiants
     */
    @GetMapping
    public String listEtudiants(Model model) {
        List<Etudiant> etudiants = etudiantService.getAllEtudiants();
        model.addAttribute("etudiants", etudiants);
        model.addAttribute("title", "Gestion des Étudiants");
        return "admin/etudiants/list";
    }
    
    /**
     * Affiche le formulaire de création
     * GET /admin/etudiants/new
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("etudiant", new Etudiant());
        model.addAttribute("title", "Nouvel Étudiant");
        return "admin/etudiants/form";
    }
    
    /**
     * Crée un nouvel étudiant
     * POST /admin/etudiants
     */
    @PostMapping
    public String createEtudiant(@ModelAttribute Etudiant etudiant, RedirectAttributes redirectAttributes) {
        try {
            etudiantService.createEtudiant(etudiant);
            redirectAttributes.addFlashAttribute("message", "Étudiant créé avec succès");
            return "redirect:/admin/etudiants";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la création: " + e.getMessage());
            return "redirect:/admin/etudiants/new";
        }
    }
    
    /**
     * Affiche les détails d'un étudiant
     * GET /admin/etudiants/{id}
     */
    @GetMapping("/{id}")
    public String showEtudiant(@PathVariable String id, Model model) {
        Etudiant etudiant = etudiantService.getEtudiantById(id);
        model.addAttribute("etudiant", etudiant);
        model.addAttribute("title", "Détails de l'Étudiant");
        return "admin/etudiants/details";
    }
    
    /**
     * Affiche le formulaire d'édition
     * GET /admin/etudiants/{id}/edit
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable String id, Model model) {
        Etudiant etudiant = etudiantService.getEtudiantById(id);
        model.addAttribute("etudiant", etudiant);
        model.addAttribute("title", "Modifier l'Étudiant");
        return "admin/etudiants/form";
    }
    
    /**
     * Met à jour un étudiant
     * PUT /admin/etudiants/{id}
     */
    @PostMapping("/{id}")
    public String updateEtudiant(
            @PathVariable String id,
            @ModelAttribute Etudiant etudiant,
            RedirectAttributes redirectAttributes
    ) {
        try {
            etudiantService.updateEtudiant(id, etudiant);
            redirectAttributes.addFlashAttribute("message", "Étudiant mis à jour avec succès");
            return "redirect:/admin/etudiants";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour: " + e.getMessage());
            return "redirect:/admin/etudiants/" + id + "/edit";
        }
    }
    
    /**
     * Supprime un étudiant
     * DELETE /admin/etudiants/{id}
     */
    @PostMapping("/{id}/delete")
    public String deleteEtudiant(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            etudiantService.deleteEtudiant(id);
            redirectAttributes.addFlashAttribute("message", "Étudiant supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression: " + e.getMessage());
        }
        return "redirect:/admin/etudiants";
    }
}

