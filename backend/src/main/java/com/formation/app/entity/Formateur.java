package com.formation.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un formateur
 */
@Entity
@Table(name = "formateurs", uniqueConstraints = {
    @UniqueConstraint(columnNames = "matricule"),
    @UniqueConstraint(columnNames = "email")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Formateur {
    
    @Id
    @Column(length = 50)
    private String id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String matricule;
    
    @Column(nullable = false, length = 100)
    private String nom;
    
    @Column(nullable = false, length = 100)
    private String specialite;
    
    @Column(nullable = false, unique = true, length = 150)
    private String email;
    
    // Relation OneToOne avec User
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    private User user;
    
    // Relation OneToMany avec Cours
    @OneToMany(mappedBy = "formateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cours> cours = new ArrayList<>();
    
    // Relation OneToMany avec Seance
    @OneToMany(mappedBy = "formateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seance> seances = new ArrayList<>();
    
    // Constructeur sans relations
    public Formateur(String id, String matricule, String nom, String specialite, String email) {
        this.id = id;
        this.matricule = matricule;
        this.nom = nom;
        this.specialite = specialite;
        this.email = email;
    }
}

