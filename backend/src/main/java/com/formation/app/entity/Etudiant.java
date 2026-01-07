package com.formation.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un étudiant
 */
@Entity
@Table(name = "etudiants", uniqueConstraints = {
    @UniqueConstraint(columnNames = "matricule"),
    @UniqueConstraint(columnNames = "email")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Etudiant {
    
    @Id
    @Column(length = 50)
    private String id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String matricule;
    
    @Column(nullable = false, length = 100)
    private String nom;
    
    @Column(nullable = false, length = 100)
    private String prenom;
    
    @Column(nullable = false, unique = true, length = 150)
    private String email;
    
    @Column(name = "date_inscription", nullable = false)
    private LocalDate dateInscription;
    
    // Relation OneToOne avec User
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    private User user;
    
    // Relation OneToMany avec Inscription
    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inscription> inscriptions = new ArrayList<>();
    
    // Relation OneToMany avec Note
    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Note> notes = new ArrayList<>();
    
    // Constructeur sans relations
    public Etudiant(String id, String matricule, String nom, String prenom, String email, LocalDate dateInscription) {
        this.id = id;
        this.matricule = matricule;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.dateInscription = dateInscription;
    }
    
    // Méthode utilitaire pour obtenir le nom complet
    public String getNomComplet() {
        return prenom + " " + nom;
    }
}

