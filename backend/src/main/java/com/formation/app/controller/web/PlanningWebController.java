package com.formation.app.controller.web;

import com.formation.app.entity.Seance;
import com.formation.app.service.SeanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller web pour le planning et l'emploi du temps
 */
@Controller
@RequestMapping("/admin/planning")
@RequiredArgsConstructor
public class PlanningWebController {
    
    private final SeanceService seanceService;
    
    /**
     * Affiche le planning général
     * GET /admin/planning
     */
    @GetMapping
    public String showPlanning(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model
    ) {
        if (date == null) {
            date = LocalDate.now();
        }
        
        List<Seance> seances = seanceService.getSeancesByDate(date);
        model.addAttribute("seances", seances);
        model.addAttribute("selectedDate", date);
        model.addAttribute("title", "Planning");
        return "admin/planning/view";
    }
    
    /**
     * Affiche l'emploi du temps d'un étudiant
     * GET /admin/planning/etudiant/{etudiantId}
     */
    @GetMapping("/etudiant/{etudiantId}")
    public String showEmploiDuTempsEtudiant(
            @PathVariable String etudiantId,
            Model model
    ) {
        List<Seance> seances = seanceService.getEmploiDuTempsEtudiant(etudiantId);
        model.addAttribute("seances", seances);
        model.addAttribute("etudiantId", etudiantId);
        model.addAttribute("title", "Emploi du Temps Étudiant");
        return "admin/planning/etudiant";
    }
    
    /**
     * Affiche le planning d'un formateur
     * GET /admin/planning/formateur/{formateurId}
     */
    @GetMapping("/formateur/{formateurId}")
    public String showPlanningFormateur(
            @PathVariable String formateurId,
            Model model
    ) {
        List<Seance> seances = seanceService.getSeancesByFormateur(formateurId);
        model.addAttribute("seances", seances);
        model.addAttribute("formateurId", formateurId);
        model.addAttribute("title", "Planning Formateur");
        return "admin/planning/formateur";
    }
}

