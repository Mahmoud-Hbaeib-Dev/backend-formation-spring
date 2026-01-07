package com.formation.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un groupe d'étudiants
 */
@Entity
@Table(name = "groupes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Groupe {
    
    @Id
    @Column(length = 50)
    private String id;
    
    @Column(nullable = false, length = 100)
    private String nom;
    
    // Relation ManyToMany avec Cours (via CoursGroupe)
    @OneToMany(mappedBy = "groupe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CoursGroupe> coursGroupes = new ArrayList<>();
    
    // Constructeur sans relations
    public Groupe(String id, String nom) {
        this.id = id;
        this.nom = nom;
    }
}

