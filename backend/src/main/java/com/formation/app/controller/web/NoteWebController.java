package com.formation.app.controller.web;

import com.formation.app.entity.Cours;
import com.formation.app.entity.Note;
import com.formation.app.service.CoursService;
import com.formation.app.service.EtudiantService;
import com.formation.app.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/notes")
@RequiredArgsConstructor
public class NoteWebController {
    
    private final NoteService noteService;
    private final EtudiantService etudiantService;
    private final CoursService coursService;
    
    @GetMapping
    public String listNotes(Model model) {
        // Récupérer toutes les notes via les cours
        List<Cours> cours = coursService.getAllCours();
        List<Note> notes = cours.stream()
                .flatMap(c -> noteService.getNotesByCours(c.getCode()).stream())
                .toList();
        model.addAttribute("notes", notes);
        model.addAttribute("title", "Gestion des Notes");
        return "admin/notes/list";
    }
    
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("note", new Note());
        model.addAttribute("etudiants", etudiantService.getAllEtudiants());
        model.addAttribute("cours", coursService.getAllCours());
        model.addAttribute("title", "Nouvelle Note");
        return "admin/notes/form";
    }
    
    @PostMapping
    public String createNote(
            @RequestParam String etudiantId,
            @RequestParam String coursCode,
            @RequestParam Float valeur,
            RedirectAttributes redirectAttributes
    ) {
        try {
            noteService.attribuerNote(etudiantId, coursCode, valeur);
            redirectAttributes.addFlashAttribute("message", "Note attribuée avec succès");
            return "redirect:/admin/notes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/admin/notes/new";
        }
    }
    
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable String id, Model model) {
        Note note = noteService.getNoteById(id);
        model.addAttribute("note", note);
        model.addAttribute("title", "Modifier la Note");
        return "admin/notes/form-edit";
    }
    
    @PostMapping("/{id}")
    public String updateNote(
            @PathVariable String id,
            @RequestParam Float valeur,
            RedirectAttributes redirectAttributes
    ) {
        try {
            noteService.updateNote(id, valeur);
            redirectAttributes.addFlashAttribute("message", "Note mise à jour avec succès");
            return "redirect:/admin/notes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/admin/notes/" + id + "/edit";
        }
    }
}

