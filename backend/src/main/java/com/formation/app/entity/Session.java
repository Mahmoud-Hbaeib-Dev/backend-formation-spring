package com.formation.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant une session pédagogique
 */
@Entity
@Table(name = "sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Session {
    
    @Id
    @Column(length = 50)
    private String id;
    
    @Column(nullable = false, length = 10)
    private String semestre; // Ex: "S1", "S2"
    
    @Column(name = "annee_scolaire", nullable = false, length = 20)
    private String anneeScolaire; // Ex: "2024-2025"
    
    // Relation OneToMany avec Cours
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cours> cours = new ArrayList<>();
    
    // Constructeur sans relations
    public Session(String id, String semestre, String anneeScolaire) {
        this.id = id;
        this.semestre = semestre;
        this.anneeScolaire = anneeScolaire;
    }
    
    // Méthode utilitaire pour obtenir le libellé complet
    public String getLibelle() {
        return semestre + " - " + anneeScolaire;
    }
}

