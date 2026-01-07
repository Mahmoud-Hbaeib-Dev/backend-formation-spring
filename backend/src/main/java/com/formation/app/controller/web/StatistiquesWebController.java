package com.formation.app.controller.web;

import com.formation.app.service.ReportService;
import com.formation.app.service.NoteService;
import com.formation.app.service.CoursService;
import com.formation.app.service.InscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller web pour les statistiques et rapports
 */
@Controller
@RequestMapping("/admin/statistiques")
@RequiredArgsConstructor
public class StatistiquesWebController {
    
    private final ReportService reportService;
    private final NoteService noteService;
    private final CoursService coursService;
    private final InscriptionService inscriptionService;
    
    /**
     * Affiche le dashboard des statistiques
     * GET /admin/statistiques
     */
    @GetMapping
    public String showStatistiques(Model model) {
        Map<String, Object> stats = reportService.getStatistiquesCours();
        model.addAttribute("stats", stats);
        model.addAttribute("title", "Statistiques");
        return "admin/statistiques/dashboard";
    }
    
    /**
     * Affiche les statistiques d'un cours
     * GET /admin/statistiques/cours/{coursCode}
     */
    @GetMapping("/cours/{coursCode}")
    public String showStatistiquesCours(@PathVariable String coursCode, Model model) {
        Map<String, Object> stats = new HashMap<>();
        
        Double moyenne = noteService.calculerMoyenneCours(coursCode);
        Double tauxReussite = noteService.calculerTauxReussite(coursCode);
        long nbInscriptions = inscriptionService.countActiveInscriptions(coursCode);
        long nbEtudiantsNotes = noteService.countEtudiantsNotes(coursCode);
        
        stats.put("coursCode", coursCode);
        stats.put("moyenne", moyenne);
        stats.put("tauxReussite", tauxReussite);
        stats.put("nombreInscriptions", nbInscriptions);
        stats.put("nombreEtudiantsNotes", nbEtudiantsNotes);
        
        model.addAttribute("stats", stats);
        model.addAttribute("cours", coursService.getCoursByCode(coursCode));
        model.addAttribute("title", "Statistiques du Cours");
        return "admin/statistiques/cours";
    }
}

