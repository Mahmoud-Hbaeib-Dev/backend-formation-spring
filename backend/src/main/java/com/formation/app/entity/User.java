package com.formation.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Entité représentant un utilisateur du système
 */
@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "login")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @Column(length = 50)
    private String id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String login;
    
    @Column(nullable = false, length = 255)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role roles;
    
    // Relation OneToOne avec Etudiant (optionnelle)
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Etudiant etudiant;
    
    // Constructeur pour faciliter la création
    public User(String id, String login, String password, Role roles) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.roles = roles;
    }
}

