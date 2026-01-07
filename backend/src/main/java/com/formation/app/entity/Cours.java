package com.formation.app.entity;

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
    private Formateur formateur;
    
    // Relation ManyToOne avec Session
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;
    
    // Relation OneToMany avec Inscription
    @OneToMany(mappedBy = "cours", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inscription> inscriptions = new ArrayList<>();
    
    // Relation OneToMany avec Note
    @OneToMany(mappedBy = "cours", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Note> notes = new ArrayList<>();
    
    // Relation OneToMany avec Seance
    @OneToMany(mappedBy = "cours", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seance> seances = new ArrayList<>();
    
    // Relation ManyToMany avec Groupe (via CoursGroupe)
    @OneToMany(mappedBy = "cours", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CoursGroupe> coursGroupes = new ArrayList<>();
    
    // Constructeur sans relations
    public Cours(String code, String titre, String description) {
        this.code = code;
        this.titre = titre;
        this.description = description;
    }
}

