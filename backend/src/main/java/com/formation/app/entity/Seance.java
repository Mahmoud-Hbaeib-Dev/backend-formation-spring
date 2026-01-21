package com.formation.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonIgnore
    private Cours cours;
    
    // Relation ManyToOne avec Formateur
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formateur_id", nullable = false)
    @JsonIgnore
    private Formateur formateur;
    
    // Getters personnalisés pour la sérialisation JSON (simplifiés)
    @JsonProperty("cours")
    public CoursInfo getCoursInfo() {
        if (cours == null) return null;
        return new CoursInfo(cours.getCode(), cours.getTitre());
    }
    
    @JsonProperty("formateur")
    public FormateurInfo getFormateurInfo() {
        if (formateur == null) return null;
        return new FormateurInfo(formateur.getId(), formateur.getNom());
    }
    
    // Classes internes pour la sérialisation
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class CoursInfo {
        private String code;
        private String titre;
    }
    
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class FormateurInfo {
        private String id;
        private String nom;
    }
}

