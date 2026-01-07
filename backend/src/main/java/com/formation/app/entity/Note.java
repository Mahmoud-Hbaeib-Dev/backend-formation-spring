package com.formation.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entité représentant une note d'évaluation
 */
@Entity
@Table(name = "notes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"etudiant_id", "cours_code"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Note {
    
    @Id
    @Column(length = 50)
    private String id;
    
    @Column(nullable = false)
    private Float valeur; // Note sur 20 par exemple
    
    @Column(name = "date_saisie", nullable = false)
    private LocalDate dateSaisie;
    
    // Relation ManyToOne avec Etudiant
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;
    
    // Relation ManyToOne avec Cours
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cours_code", nullable = false)
    private Cours cours;
}

