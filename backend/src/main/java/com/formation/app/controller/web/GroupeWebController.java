package com.formation.app.controller.web;

import com.formation.app.entity.Groupe;
import com.formation.app.service.GroupeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller web pour la gestion des groupes
 */
@Controller
@RequestMapping("/admin/groupes")
@RequiredArgsConstructor
public class GroupeWebController {
    
    private final GroupeService groupeService;
    
    @GetMapping
    public String listGroupes(Model model) {
        List<Groupe> groupes = groupeService.getAllGroupes();
        model.addAttribute("groupes", groupes);
        model.addAttribute("title", "Gestion des Groupes");
        return "admin/groupes/list";
    }
    
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("groupe", new Groupe());
        model.addAttribute("title", "Nouveau Groupe");
        return "admin/groupes/form";
    }
    
    @PostMapping
    public String createGroupe(@ModelAttribute Groupe groupe, RedirectAttributes redirectAttributes) {
        try {
            groupeService.createGroupe(groupe);
            redirectAttributes.addFlashAttribute("message", "Groupe créé avec succès");
            return "redirect:/admin/groupes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/admin/groupes/new";
        }
    }
    
    @GetMapping("/{id}")
    public String showGroupe(@PathVariable String id, Model model) {
        Groupe groupe = groupeService.getGroupeById(id);
        model.addAttribute("groupe", groupe);
        model.addAttribute("title", "Détails du Groupe");
        return "admin/groupes/details";
    }
    
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable String id, Model model) {
        Groupe groupe = groupeService.getGroupeById(id);
        model.addAttribute("groupe", groupe);
        model.addAttribute("title", "Modifier le Groupe");
        return "admin/groupes/form";
    }
    
    @PostMapping("/{id}")
    public String updateGroupe(
            @PathVariable String id,
            @ModelAttribute Groupe groupe,
            RedirectAttributes redirectAttributes
    ) {
        try {
            groupeService.updateGroupe(id, groupe);
            redirectAttributes.addFlashAttribute("message", "Groupe mis à jour avec succès");
            return "redirect:/admin/groupes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/admin/groupes/" + id + "/edit";
        }
    }
    
    @PostMapping("/{id}/delete")
    public String deleteGroupe(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            groupeService.deleteGroupe(id);
            redirectAttributes.addFlashAttribute("message", "Groupe supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/admin/groupes";
    }
}

