package com.formation.app.service;

import com.formation.app.entity.Cours;
import com.formation.app.entity.Etudiant;
import com.formation.app.entity.Note;
import com.formation.app.exception.BadRequestException;
import com.formation.app.exception.ResourceNotFoundException;
import com.formation.app.repository.CoursRepository;
import com.formation.app.repository.EtudiantRepository;
import com.formation.app.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service pour la gestion des notes
 */
@Service
@RequiredArgsConstructor
@Transactional
public class NoteService {
    
    private final NoteRepository noteRepository;
    private final EtudiantRepository etudiantRepository;
    private final CoursRepository coursRepository;
    
    /**
     * Attribue une note à un étudiant pour un cours
     */
    public Note attribuerNote(String etudiantId, String coursCode, Float valeur) {
        // Valider la valeur de la note
        if (valeur < 0 || valeur > 20) {
            throw new BadRequestException("La note doit être entre 0 et 20");
        }
        
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new ResourceNotFoundException("Etudiant", "id", etudiantId));
        
        Cours cours = coursRepository.findByCode(coursCode)
                .orElseThrow(() -> new ResourceNotFoundException("Cours", "code", coursCode));
        
        // Vérifier si une note existe déjà
        Optional<Note> existingNote = noteRepository.findByEtudiantAndCours(etudiant, cours);
        
        if (existingNote.isPresent()) {
            // Mettre à jour la note existante
            Note note = existingNote.get();
            note.setValeur(valeur);
            note.setDateSaisie(LocalDate.now());
            return noteRepository.save(note);
        } else {
            // Créer une nouvelle note
            Note note = new Note();
            note.setId(UUID.randomUUID().toString());
            note.setEtudiant(etudiant);
            note.setCours(cours);
            note.setValeur(valeur);
            note.setDateSaisie(LocalDate.now());
            return noteRepository.save(note);
        }
    }
    
    /**
     * Met à jour une note
     */
    public Note updateNote(String noteId, Float valeur) {
        // Valider la valeur de la note
        if (valeur < 0 || valeur > 20) {
            throw new BadRequestException("La note doit être entre 0 et 20");
        }
        
        Note note = getNoteById(noteId);
        note.setValeur(valeur);
        note.setDateSaisie(LocalDate.now());
        return noteRepository.save(note);
    }
    
    /**
     * Trouve toutes les notes d'un étudiant
     */
    @Transactional(readOnly = true)
    public List<Note> getNotesByEtudiant(String etudiantId) {
        return noteRepository.findByEtudiantId(etudiantId);
    }
    
    /**
     * Trouve toutes les notes d'un cours
     */
    @Transactional(readOnly = true)
    public List<Note> getNotesByCours(String coursCode) {
        return noteRepository.findByCoursCode(coursCode);
    }
    
    /**
     * Calcule la moyenne d'un étudiant pour un cours
     */
    @Transactional(readOnly = true)
    public Double calculerMoyenneEtudiant(String etudiantId, String coursCode) {
        Double moyenne = noteRepository.calculerMoyenneEtudiantCours(etudiantId, coursCode);
        return moyenne != null ? moyenne : 0.0;
    }
    
    /**
     * Calcule la moyenne générale d'un étudiant (tous cours confondus)
     */
    @Transactional(readOnly = true)
    public Double calculerMoyenneGeneraleEtudiant(String etudiantId) {
        Double moyenne = noteRepository.calculerMoyenneGeneraleEtudiant(etudiantId);
        return moyenne != null ? moyenne : 0.0;
    }
    
    /**
     * Calcule la moyenne d'un cours (tous étudiants confondus)
     */
    @Transactional(readOnly = true)
    public Double calculerMoyenneCours(String coursCode) {
        Double moyenne = noteRepository.calculerMoyenneCours(coursCode);
        return moyenne != null ? moyenne : 0.0;
    }
    
    /**
     * Calcule le taux de réussite d'un cours (notes >= 10)
     */
    @Transactional(readOnly = true)
    public Double calculerTauxReussite(String coursCode) {
        List<Note> notes = noteRepository.findByCoursCode(coursCode);
        if (notes.isEmpty()) {
            return 0.0;
        }
        
        long notesReussies = notes.stream()
                .filter(n -> n.getValeur() >= 10.0)
                .count();
        
        return (double) notesReussies / notes.size() * 100;
    }
    
    /**
     * Trouve une note par son ID
     */
    @Transactional(readOnly = true)
    public Note getNoteById(String id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", id));
    }
    
    /**
     * Compte le nombre d'étudiants notés pour un cours
     */
    @Transactional(readOnly = true)
    public long countEtudiantsNotes(String coursCode) {
        return noteRepository.countEtudiantsNotesByCoursCode(coursCode);
    }
}

