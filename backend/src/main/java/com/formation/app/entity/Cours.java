package com.formation.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un cours
 */
@Entity
@Table(name = "cours")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cours {
    
    @Id
    @Column(length = 50)
    private String code;
    
    @Column(nullable = false, length = 200)
    private String titre;
    
    @Column(length = 1000)
    private String description;
    
    // Relation ManyToOne avec Formateur
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formateur_id", nullable = false)
    @JsonIgnore
    private Formateur formateur;
    
    // Relation ManyToOne avec Session
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    @JsonIgnore
    private Session session;
    
    // Relation OneToMany avec Inscription
    @OneToMany(mappedBy = "cours", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Inscription> inscriptions = new ArrayList<>();
    
    // Relation OneToMany avec Note
    @OneToMany(mappedBy = "cours", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Note> notes = new ArrayList<>();
    
    // Relation OneToMany avec Seance
    @OneToMany(mappedBy = "cours", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Seance> seances = new ArrayList<>();
    
    // Relation ManyToMany avec Groupe (via CoursGroupe)
    @OneToMany(mappedBy = "cours", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<CoursGroupe> coursGroupes = new ArrayList<>();
    
    // Getters personnalisés pour la sérialisation JSON (simplifiés)
    @JsonProperty("formateur")
    public FormateurInfo getFormateurInfo() {
        if (formateur == null) return null;
        return new FormateurInfo(formateur.getId(), formateur.getNom(), formateur.getSpecialite());
    }
    
    @JsonProperty("session")
    public SessionInfo getSessionInfo() {
        if (session == null) return null;
        return new SessionInfo(session.getId(), session.getSemestre(), session.getAnneeScolaire());
    }
    
    // Classes internes pour la sérialisation
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class FormateurInfo {
        private String id;
        private String nom;
        private String specialite;
    }
    
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class SessionInfo {
        private String id;
        private String semestre;
        private String anneeScolaire;
    }
    
    // Constructeur sans relations
    public Cours(String code, String titre, String description) {
        this.code = code;
        this.titre = titre;
        this.description = description;
    }
}

