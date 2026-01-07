package com.formation.app.controller.api;

import com.formation.app.entity.Note;
import com.formation.app.service.NoteService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST pour la gestion des notes
 */
@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NoteRestController {
    
    private final NoteService noteService;
    
    /**
     * Liste toutes les notes
     * GET /api/notes
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR')")
    public ResponseEntity<List<Note>> getAllNotes() {
        // Cette méthode nécessiterait un repository pour lister toutes les notes
        return ResponseEntity.ok(List.of());
    }
    
    /**
     * Trouve une note par son ID
     * GET /api/notes/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR')")
    public ResponseEntity<Note> getNoteById(@PathVariable String id) {
        Note note = noteService.getNoteById(id);
        return ResponseEntity.ok(note);
    }
    
    /**
     * Attribue une note à un étudiant pour un cours
     * POST /api/notes
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR')")
    public ResponseEntity<Note> attribuerNote(@RequestBody NoteRequest request) {
        Note note = noteService.attribuerNote(
                request.getEtudiantId(),
                request.getCoursCode(),
                request.getValeur()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(note);
    }
    
    /**
     * Met à jour une note
     * PUT /api/notes/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR')")
    public ResponseEntity<Note> updateNote(
            @PathVariable String id,
            @RequestBody NoteUpdateRequest request
    ) {
        Note note = noteService.updateNote(id, request.getValeur());
        return ResponseEntity.ok(note);
    }
    
    /**
     * Trouve toutes les notes d'un étudiant
     * GET /api/notes/etudiant/{etudiantId}
     */
    @GetMapping("/etudiant/{etudiantId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR', 'ETUDIANT')")
    public ResponseEntity<List<Note>> getNotesByEtudiant(@PathVariable String etudiantId) {
        List<Note> notes = noteService.getNotesByEtudiant(etudiantId);
        return ResponseEntity.ok(notes);
    }
    
    /**
     * Trouve toutes les notes d'un cours
     * GET /api/notes/cours/{coursCode}
     */
    @GetMapping("/cours/{coursCode}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR')")
    public ResponseEntity<List<Note>> getNotesByCours(@PathVariable String coursCode) {
        List<Note> notes = noteService.getNotesByCours(coursCode);
        return ResponseEntity.ok(notes);
    }
    
    /**
     * DTO pour la requête d'attribution de note
     */
    @Data
    static class NoteRequest {
        private String etudiantId;
        private String coursCode;
        private Float valeur;
    }
    
    /**
     * DTO pour la mise à jour de note
     */
    @Data
    static class NoteUpdateRequest {
        private Float valeur;
    }
}

