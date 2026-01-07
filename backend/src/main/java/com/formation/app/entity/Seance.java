package com.formation.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entité représentant une séance de cours
 */
@Entity
@Table(name = "seances")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seance {
    
    @Id
    @Column(length = 50)
    private String id;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(nullable = false)
    private LocalTime heure;
    
    @Column(nullable = false, length = 50)
    private String salle;
    
    // Relation ManyToOne avec Cours
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cours_code", nullable = false)
    private Cours cours;
    
    // Relation ManyToOne avec Formateur
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formateur_id", nullable = false)
    private Formateur formateur;
}

