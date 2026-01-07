package com.formation.app.config;

import com.formation.app.entity.Etudiant;
import com.formation.app.entity.Formateur;
import com.formation.app.entity.Role;
import com.formation.app.entity.User;
import com.formation.app.repository.EtudiantRepository;
import com.formation.app.repository.FormateurRepository;
import com.formation.app.repository.UserRepository;
import com.formation.app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Migration des données existantes pour créer les Users manquants
 * S'exécute AVANT DataInitializer
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Order(1) // S'exécute avant DataInitializer (Order 2 par défaut)
public class DataMigration implements CommandLineRunner {
    
    private final EtudiantRepository etudiantRepository;
    private final FormateurRepository formateurRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("=== Migration des données existantes ===");
        
        // Migrer les étudiants sans User
        migrateEtudiants();
        
        // Migrer les formateurs sans User
        migrateFormateurs();
        
        log.info("=== Migration terminée ===");
    }
    
    private void migrateEtudiants() {
        log.info("🔄 Migration des étudiants...");
        List<Etudiant> etudiants = etudiantRepository.findAll();
        int count = 0;
        
        for (Etudiant etudiant : etudiants) {
            if (etudiant.getUser() == null) {
                log.info("📝 Création du User pour l'étudiant: {} {} ({})", 
                    etudiant.getPrenom(), etudiant.getNom(), etudiant.getMatricule());
                
                try {
                    // Générer un login basé sur le matricule
                    String login = etudiant.getMatricule().toLowerCase();
                    String password = etudiant.getMatricule().toLowerCase();
                    
                    // Vérifier si le login existe déjà
                    if (userRepository.existsByLogin(login)) {
                        login = etudiant.getEmail().split("@")[0];
                        password = login;
                    }
                    
                    User user = userService.createUser(login, password, Role.ETUDIANT);
                    etudiant.setUser(user);
                    etudiantRepository.save(etudiant);
                    
                    log.info("✅ User créé pour {} {} - Login: {}, Password: {}", 
                        etudiant.getPrenom(), etudiant.getNom(), login, password);
                    count++;
                } catch (Exception e) {
                    log.error("❌ Erreur lors de la création du User pour {} {}: {}", 
                        etudiant.getPrenom(), etudiant.getNom(), e.getMessage());
                }
            }
        }
        
        log.info("✅ {} étudiants migrés", count);
    }
    
    private void migrateFormateurs() {
        log.info("🔄 Migration des formateurs...");
        List<Formateur> formateurs = formateurRepository.findAll();
        int count = 0;
        
        for (Formateur formateur : formateurs) {
            if (formateur.getUser() == null) {
                log.info("📝 Création du User pour le formateur: {} ({})", 
                    formateur.getNom(), formateur.getEmail());
                
                try {
                    // Générer un login basé sur l'email
                    String login = formateur.getEmail().split("@")[0];
                    String password = login;
                    
                    // Vérifier si le login existe déjà
                    int suffix = 1;
                    String originalLogin = login;
                    while (userRepository.existsByLogin(login)) {
                        login = originalLogin + suffix;
                        suffix++;
                    }
                    
                    User user = userService.createUser(login, password, Role.FORMATEUR);
                    formateur.setUser(user);
                    formateurRepository.save(formateur);
                    
                    log.info("✅ User créé pour {} - Login: {}, Password: {}", 
                        formateur.getNom(), login, password);
                    count++;
                } catch (Exception e) {
                    log.error("❌ Erreur lors de la création du User pour {}: {}", 
                        formateur.getNom(), e.getMessage());
                }
            }
        }
        
        log.info("✅ {} formateurs migrés", count);
    }
}

