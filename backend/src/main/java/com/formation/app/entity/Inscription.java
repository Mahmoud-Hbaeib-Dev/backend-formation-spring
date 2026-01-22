package com.formation.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonIgnore
    private Etudiant etudiant;
    
    // Relation ManyToOne avec Cours
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cours_code", nullable = false)
    @JsonIgnore
    private Cours cours;
    
    // Getters personnalisés pour la sérialisation JSON (simplifiés)
    @JsonProperty("cours")
    public CoursInfo getCoursInfo() {
        if (cours == null) return null;
        return new CoursInfo(cours.getCode(), cours.getTitre(), cours.getDescription());
    }
    
    @JsonProperty("etudiant")
    public EtudiantInfo getEtudiantInfo() {
        if (etudiant == null) return null;
        return new EtudiantInfo(etudiant.getId(), etudiant.getMatricule(), etudiant.getNom(), etudiant.getPrenom());
    }
    
    // Classes internes pour la sérialisation
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class CoursInfo {
        private String code;
        private String titre;
        private String description;
    }
    
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class EtudiantInfo {
        private String id;
        private String matricule;
        private String nom;
        private String prenom;
    }
}

