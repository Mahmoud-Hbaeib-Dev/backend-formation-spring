package com.formation.app.controller.web;

import com.formation.app.entity.Session;
import com.formation.app.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller web pour la gestion des sessions pédagogiques
 */
@Controller
@RequestMapping("/admin/sessions")
@RequiredArgsConstructor
public class SessionWebController {
    
    private final SessionService sessionService;
    
    @GetMapping
    public String listSessions(Model model) {
        List<Session> sessions = sessionService.getAllSessions();
        model.addAttribute("sessions", sessions);
        model.addAttribute("title", "Gestion des Sessions");
        return "admin/sessions/list";
    }
    
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("session", new Session());
        model.addAttribute("title", "Nouvelle Session");
        return "admin/sessions/form";
    }
    
    @PostMapping
    public String createSession(@ModelAttribute Session session, RedirectAttributes redirectAttributes) {
        try {
            sessionService.createSession(session);
            redirectAttributes.addFlashAttribute("message", "Session créée avec succès");
            return "redirect:/admin/sessions";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/admin/sessions/new";
        }
    }
    
    @GetMapping("/{id}")
    public String showSession(@PathVariable String id, Model model) {
        Session session = sessionService.getSessionById(id);
        model.addAttribute("session", session);
        model.addAttribute("title", "Détails de la Session");
        return "admin/sessions/details";
    }
    
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable String id, Model model) {
        Session session = sessionService.getSessionById(id);
        model.addAttribute("session", session);
        model.addAttribute("title", "Modifier la Session");
        return "admin/sessions/form";
    }
    
    @PostMapping("/{id}")
    public String updateSession(
            @PathVariable String id,
            @ModelAttribute Session session,
            RedirectAttributes redirectAttributes
    ) {
        try {
            sessionService.updateSession(id, session);
            redirectAttributes.addFlashAttribute("message", "Session mise à jour avec succès");
            return "redirect:/admin/sessions";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/admin/sessions/" + id + "/edit";
        }
    }
    
    @PostMapping("/{id}/delete")
    public String deleteSession(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            sessionService.deleteSession(id);
            redirectAttributes.addFlashAttribute("message", "Session supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/admin/sessions";
    }
}

