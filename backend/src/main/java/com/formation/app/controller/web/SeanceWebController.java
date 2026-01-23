package com.formation.app.controller.web;

import com.formation.app.entity.Seance;
import com.formation.app.service.CoursService;
import com.formation.app.service.FormateurService;
import com.formation.app.service.SeanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/seances")
@RequiredArgsConstructor
public class SeanceWebController {
    
    private final SeanceService seanceService;
    private final CoursService coursService;
    private final FormateurService formateurService;
    
    @GetMapping
    public String listSeances(Model model) {
        // Récupérer toutes les séances futures (à partir d'aujourd'hui) triées par date et heure
        List<Seance> seances = seanceService.getSeancesFutures();
        model.addAttribute("seances", seances);
        model.addAttribute("title", "Gestion des Séances");
        return "admin/seances/list";
    }
    
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("seance", new Seance());
        model.addAttribute("cours", coursService.getAllCours());
        model.addAttribute("formateurs", formateurService.getAllFormateurs());
        model.addAttribute("title", "Nouvelle Séance");
        return "admin/seances/form";
    }
    
    @PostMapping
    public String createSeance(@ModelAttribute Seance seance, RedirectAttributes redirectAttributes) {
        try {
            seanceService.createSeance(seance);
            redirectAttributes.addFlashAttribute("message", "Séance créée avec succès");
            return "redirect:/admin/seances";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/admin/seances/new";
        }
    }
    
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable String id, Model model) {
        Seance seance = seanceService.getSeanceById(id);
        model.addAttribute("seance", seance);
        model.addAttribute("cours", coursService.getAllCours());
        model.addAttribute("formateurs", formateurService.getAllFormateurs());
        model.addAttribute("title", "Modifier la Séance");
        return "admin/seances/form";
    }
    
    @PostMapping("/{id}")
    public String updateSeance(
            @PathVariable String id,
            @ModelAttribute Seance seance,
            RedirectAttributes redirectAttributes
    ) {
        try {
            seanceService.updateSeance(id, seance);
            redirectAttributes.addFlashAttribute("message", "Séance mise à jour avec succès");
            return "redirect:/admin/seances";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/admin/seances/" + id + "/edit";
        }
    }
    
    @PostMapping("/{id}/delete")
    public String deleteSeance(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            seanceService.deleteSeance(id);
            redirectAttributes.addFlashAttribute("message", "Séance supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/admin/seances";
    }
}

