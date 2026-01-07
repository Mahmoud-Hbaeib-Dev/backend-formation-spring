package com.formation.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entité représentant une inscription d'un étudiant à un cours
 */
@Entity
@Table(name = "inscriptions", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"etudiant_id", "cours_code"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inscription {
    
    @Id
    @Column(length = 50)
    private String id;
    
    @Column(name = "date_inscription", nullable = false)
    private LocalDate dateInscription;
    
    @Column(nullable = false, length = 50)
    private String status; // Ex: "ACTIVE", "CANCELLED", "COMPLETED"
    
    // Relation ManyToOne avec Etudiant
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;
    
    // Relation ManyToOne avec Cours
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cours_code", nullable = false)
    private Cours cours;
}

