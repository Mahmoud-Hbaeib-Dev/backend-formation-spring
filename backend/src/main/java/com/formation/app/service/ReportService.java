package com.formation.app.service;

import com.formation.app.entity.Cours;
import com.formation.app.entity.Etudiant;
import com.formation.app.entity.Note;
import com.formation.app.repository.CoursRepository;
import com.formation.app.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service pour la génération de rapports et statistiques
 * Pour le développement, on génère des données JSON
 * En production, on peut utiliser JasperReports ou iText pour générer des PDFs
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReportService {
    
    private final NoteRepository noteRepository;
    private final CoursRepository coursRepository;
    private final NoteService noteService;
    
    /**
     * Génère un rapport de notes pour un étudiant
     * @return Map contenant les données du rapport
     */
    public Map<String, Object> genererRapportNotes(Etudiant etudiant) {
        Map<String, Object> rapport = new HashMap<>();
        
        rapport.put("etudiant", Map.of(
            "id", etudiant.getId(),
            "matricule", etudiant.getMatricule(),
            "nom", etudiant.getNom(),
            "prenom", etudiant.getPrenom(),
            "email", etudiant.getEmail()
        ));
        
        List<Note> notes = noteRepository.findByEtudiantId(etudiant.getId());
        rapport.put("notes", notes.stream()
            .map(n -> Map.of(
                "coursCode", n.getCours().getCode(),
                "coursTitre", n.getCours().getTitre(),
                "valeur", n.getValeur(),
                "dateSaisie", n.getDateSaisie().toString()
            ))
            .collect(Collectors.toList()));
        
        Double moyenneGenerale = noteService.calculerMoyenneGeneraleEtudiant(etudiant.getId());
        rapport.put("moyenneGenerale", moyenneGenerale);
        rapport.put("nombreNotes", notes.size());
        
        log.info("Rapport de notes généré pour l'étudiant: {}", etudiant.getMatricule());
        
        return rapport;
    }
    
    /**
     * Génère un rapport pour un cours
     * @return Map contenant les données du rapport
     */
    public Map<String, Object> genererRapportCours(Cours cours) {
        Map<String, Object> rapport = new HashMap<>();
        
        rapport.put("cours", Map.of(
            "code", cours.getCode(),
            "titre", cours.getTitre(),
            "description", cours.getDescription() != null ? cours.getDescription() : ""
        ));
        
        List<Note> notes = noteRepository.findByCoursCode(cours.getCode());
        rapport.put("notes", notes.stream()
            .map(n -> Map.of(
                "etudiantId", n.getEtudiant().getId(),
                "etudiantNom", n.getEtudiant().getNomComplet(),
                "matricule", n.getEtudiant().getMatricule(),
                "valeur", n.getValeur(),
                "dateSaisie", n.getDateSaisie().toString()
            ))
            .collect(Collectors.toList()));
        
        Double moyenne = noteService.calculerMoyenneCours(cours.getCode());
        Double tauxReussite = noteService.calculerTauxReussite(cours.getCode());
        
        rapport.put("moyenne", moyenne);
        rapport.put("tauxReussite", tauxReussite);
        rapport.put("nombreEtudiantsNotes", notes.size());
        
        log.info("Rapport de cours généré pour: {}", cours.getCode());
        
        return rapport;
    }
    
    /**
     * Obtient les statistiques générales des cours
     * @return Map contenant les statistiques
     */
    public Map<String, Object> getStatistiquesCours() {
        Map<String, Object> stats = new HashMap<>();
        
        List<Cours> tousLesCours = coursRepository.findAll();
        stats.put("nombreTotalCours", tousLesCours.size());
        
        // Cours les plus suivis (par nombre d'inscriptions)
        // Cette logique sera implémentée dans InscriptionService
        
        log.info("Statistiques des cours générées");
        
        return stats;
    }
    
    /**
     * Obtient la liste des cours les plus suivis
     * @return Liste des cours avec leur nombre d'inscriptions
     */
    public List<Map<String, Object>> getCoursPlusSuivis() {
        // Cette méthode nécessitera une requête plus complexe
        // Pour l'instant, on retourne une liste vide
        log.info("Liste des cours les plus suivis générée");
        return List.of();
    }
}

