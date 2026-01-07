package com.formation.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Table de liaison entre Cours et Groupe (ManyToMany)
 */
@Entity
@Table(name = "cours_groupe", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"cours_code", "groupe_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoursGroupe {
    
    @Id
    @Column(length = 50)
    private String id;
    
    // Relation ManyToOne avec Groupe
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupe_id", nullable = false)
    private Groupe groupe;
    
    // Relation ManyToOne avec Cours
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cours_code", nullable = false)
    private Cours cours;
}

