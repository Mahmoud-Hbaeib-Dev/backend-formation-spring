package com.formation.app.controller.web;

import com.formation.app.entity.Cours;
import com.formation.app.service.CoursService;
import com.formation.app.service.FormateurService;
import com.formation.app.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/cours")
@RequiredArgsConstructor
public class CoursWebController {
    
    private final CoursService coursService;
    private final FormateurService formateurService;
    private final SessionService sessionService;
    
    @GetMapping
    public String listCours(Model model) {
        List<Cours> cours = coursService.getAllCours();
        model.addAttribute("cours", cours);
        model.addAttribute("title", "Gestion des Cours");
        return "admin/cours/list";
    }
    
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("cours", new Cours());
        model.addAttribute("formateurs", formateurService.getAllFormateurs());
        model.addAttribute("sessions", sessionService.getAllSessions());
        model.addAttribute("title", "Nouveau Cours");
        return "admin/cours/form";
    }
    
    @PostMapping
    public String createCours(@ModelAttribute Cours cours, RedirectAttributes redirectAttributes) {
        try {
            coursService.createCours(cours);
            redirectAttributes.addFlashAttribute("message", "Cours créé avec succès");
            return "redirect:/admin/cours";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/admin/cours/new";
        }
    }
    
    @GetMapping("/{code}")
    public String showCours(@PathVariable String code, Model model) {
        Cours cours = coursService.getCoursByCode(code);
        model.addAttribute("cours", cours);
        model.addAttribute("title", "Détails du Cours");
        return "admin/cours/details";
    }
    
    @GetMapping("/{code}/edit")
    public String showEditForm(@PathVariable String code, Model model) {
        Cours cours = coursService.getCoursByCode(code);
        model.addAttribute("cours", cours);
        model.addAttribute("formateurs", formateurService.getAllFormateurs());
        model.addAttribute("sessions", sessionService.getAllSessions());
        model.addAttribute("title", "Modifier le Cours");
        return "admin/cours/form";
    }
    
    @PostMapping("/{code}")
    public String updateCours(
            @PathVariable String code,
            @ModelAttribute Cours cours,
            RedirectAttributes redirectAttributes
    ) {
        try {
            coursService.updateCours(code, cours);
            redirectAttributes.addFlashAttribute("message", "Cours mis à jour avec succès");
            return "redirect:/admin/cours";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/admin/cours/" + code + "/edit";
        }
    }
    
    @PostMapping("/{code}/delete")
    public String deleteCours(@PathVariable String code, RedirectAttributes redirectAttributes) {
        try {
            coursService.deleteCours(code);
            redirectAttributes.addFlashAttribute("message", "Cours supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/admin/cours";
    }
}

