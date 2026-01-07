package com.formation.app.controller.web;

import com.formation.app.entity.Formateur;
import com.formation.app.service.FormateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller web pour la gestion des formateurs (Thymeleaf)
 */
@Controller
@RequestMapping("/admin/formateurs")
@RequiredArgsConstructor
public class FormateurWebController {
    
    private final FormateurService formateurService;
    
    @GetMapping
    public String listFormateurs(Model model) {
        List<Formateur> formateurs = formateurService.getAllFormateurs();
        model.addAttribute("formateurs", formateurs);
        model.addAttribute("title", "Gestion des Formateurs");
        return "admin/formateurs/list";
    }
    
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("formateur", new Formateur());
        model.addAttribute("title", "Nouveau Formateur");
        return "admin/formateurs/form";
    }
    
    @PostMapping
    public String createFormateur(@ModelAttribute Formateur formateur, RedirectAttributes redirectAttributes) {
        try {
            formateurService.createFormateur(formateur);
            redirectAttributes.addFlashAttribute("message", "Formateur créé avec succès");
            return "redirect:/admin/formateurs";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/admin/formateurs/new";
        }
    }
    
    @GetMapping("/{id}")
    public String showFormateur(@PathVariable String id, Model model) {
        Formateur formateur = formateurService.getFormateurById(id);
        model.addAttribute("formateur", formateur);
        model.addAttribute("title", "Détails du Formateur");
        return "admin/formateurs/details";
    }
    
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable String id, Model model) {
        Formateur formateur = formateurService.getFormateurById(id);
        model.addAttribute("formateur", formateur);
        model.addAttribute("title", "Modifier le Formateur");
        return "admin/formateurs/form";
    }
    
    @PostMapping("/{id}")
    public String updateFormateur(
            @PathVariable String id,
            @ModelAttribute Formateur formateur,
            RedirectAttributes redirectAttributes
    ) {
        try {
            formateurService.updateFormateur(id, formateur);
            redirectAttributes.addFlashAttribute("message", "Formateur mis à jour avec succès");
            return "redirect:/admin/formateurs";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/admin/formateurs/" + id + "/edit";
        }
    }
    
    @PostMapping("/{id}/delete")
    public String deleteFormateur(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            formateurService.deleteFormateur(id);
            redirectAttributes.addFlashAttribute("message", "Formateur supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/admin/formateurs";
    }
}

