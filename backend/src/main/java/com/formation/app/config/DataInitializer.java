package com.formation.app.config;

import com.formation.app.entity.*;
import com.formation.app.repository.*;
import com.formation.app.service.UserService;
import com.formation.app.service.EtudiantService;
import com.formation.app.service.FormateurService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Initialiseur de données pour créer des utilisateurs et données de test
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final UserService userService;
    private final EtudiantService etudiantService;
    private final FormateurService formateurService;
    private final EtudiantRepository etudiantRepository;
    private final FormateurRepository formateurRepository;
    private final SessionRepository sessionRepository;
    private final CoursRepository coursRepository;
    private final GroupeRepository groupeRepository;
    private final InscriptionRepository inscriptionRepository;
    private final SeanceRepository seanceRepository;
    private final NoteRepository noteRepository;
    private final CoursGroupeRepository coursGroupeRepository;
    
    @Override
    public void run(String... args) throws Exception {
        log.info("=== Initialisation des données ===");
        
        // Créer l'utilisateur ADMIN
        createAdminUser();
        
        // Créer des données de test
        createTestData();
        
        log.info("=== Initialisation terminée ===");
    }
    
    private void createAdminUser() {
        // Vérifier si l'admin existe déjà
        if (userService.getUserByLogin("admin").isPresent()) {
            log.info("L'utilisateur admin existe déjà");
            return;
        }
        
        // Créer l'utilisateur admin
        userService.createUser("admin", "admin", Role.ADMIN);
        log.info("✅ Utilisateur ADMIN créé - Login: admin, Password: admin");
    }
    
    private void createTestData() {
        // Créer des sessions (si elles n'existent pas déjà)
        Session session1 = sessionRepository.findById("SESS001").orElse(new Session("SESS001", "S1", "2024-2025"));
        Session session2 = sessionRepository.findById("SESS002").orElse(new Session("SESS002", "S2", "2024-2025"));
        if (session1.getId() == null || !sessionRepository.existsById("SESS001")) {
            sessionRepository.save(session1);
        }
        if (session2.getId() == null || !sessionRepository.existsById("SESS002")) {
            sessionRepository.save(session2);
        }
        log.info("✅ Sessions vérifiées/créées");
        
        // Créer des formateurs (les Users seront créés automatiquement par FormateurService)
        // Chercher par email au lieu de l'ID car l'ID peut être un UUID
        Formateur formateur1 = formateurRepository.findByEmail("dupont@formation.com")
            .orElseGet(() -> {
                Formateur f = new Formateur(null, "Dupont", "Java", "dupont@formation.com");
                return formateurService.createFormateur(f);
            });
        Formateur formateur2 = formateurRepository.findByEmail("martin@formation.com")
            .orElseGet(() -> {
                Formateur f = new Formateur(null, "Martin", "Spring Boot", "martin@formation.com");
                return formateurService.createFormateur(f);
            });
        Formateur formateur3 = formateurRepository.findByEmail("bernard@formation.com")
            .orElseGet(() -> {
                Formateur f = new Formateur(null, "Bernard", "Base de données", "bernard@formation.com");
                return formateurService.createFormateur(f);
            });
        
        // S'assurer que les formateurs ont des Users
        if (formateur1.getUser() == null) {
            log.warn("⚠️ Formateur 1 (Dupont) n'a pas de User, création...");
            String login = formateur1.getEmail().split("@")[0];
            try {
                User user = userService.createUser(login, login, Role.FORMATEUR);
                formateur1.setUser(user);
                formateurRepository.save(formateur1);
                log.info("✅ User créé pour formateur 1: {}", login);
            } catch (Exception e) {
                log.error("❌ Erreur lors de la création du User pour formateur 1: {}", e.getMessage());
            }
        }
        if (formateur2.getUser() == null) {
            log.warn("⚠️ Formateur 2 (Martin) n'a pas de User, création...");
            String login = formateur2.getEmail().split("@")[0];
            try {
                User user = userService.createUser(login, login, Role.FORMATEUR);
                formateur2.setUser(user);
                formateurRepository.save(formateur2);
                log.info("✅ User créé pour formateur 2: {}", login);
            } catch (Exception e) {
                log.error("❌ Erreur lors de la création du User pour formateur 2: {}", e.getMessage());
            }
        }
        if (formateur3.getUser() == null) {
            log.warn("⚠️ Formateur 3 (Bernard) n'a pas de User, création...");
            String login = formateur3.getEmail().split("@")[0];
            try {
                User user = userService.createUser(login, login, Role.FORMATEUR);
                formateur3.setUser(user);
                formateurRepository.save(formateur3);
                log.info("✅ User créé pour formateur 3: {}", login);
            } catch (Exception e) {
                log.error("❌ Erreur lors de la création du User pour formateur 3: {}", e.getMessage());
            }
        }
        
        log.info("✅ Formateurs vérifiés/créés");
        
        // Afficher les credentials des formateurs
        log.info("📋 Credentials Formateurs:");
        if (formateur1.getUser() != null) {
            log.info("   - Formateur 1 (Dupont): Login: {}, Password: {}, Email: {}", 
                formateur1.getUser().getLogin(), formateur1.getUser().getLogin(), formateur1.getEmail());
        } else {
            log.warn("   - Formateur 1 (Dupont): PAS DE USER!");
        }
        if (formateur2.getUser() != null) {
            log.info("   - Formateur 2 (Martin): Login: {}, Password: {}, Email: {}", 
                formateur2.getUser().getLogin(), formateur2.getUser().getLogin(), formateur2.getEmail());
        } else {
            log.warn("   - Formateur 2 (Martin): PAS DE USER!");
        }
        if (formateur3.getUser() != null) {
            log.info("   - Formateur 3 (Bernard): Login: {}, Password: {}, Email: {}", 
                formateur3.getUser().getLogin(), formateur3.getUser().getLogin(), formateur3.getEmail());
        } else {
            log.warn("   - Formateur 3 (Bernard): PAS DE USER!");
        }
        
        // Créer des cours
        Cours cours1 = new Cours("JAVA001", "Java Fondamentaux", "Introduction à la programmation Java");
        cours1.setFormateur(formateur1);
        cours1.setSession(session1);
        
        Cours cours2 = new Cours("SPRING001", "Spring Boot Avancé", "Développement d'applications Spring Boot");
        cours2.setFormateur(formateur2);
        cours2.setSession(session1);
        
        Cours cours3 = new Cours("BDD001", "Bases de données", "MySQL, JPA, Hibernate");
        cours3.setFormateur(formateur3);
        cours3.setSession(session1);
        
        coursRepository.saveAll(List.of(cours1, cours2, cours3));
        log.info("✅ Cours créés");
        
        // Créer des groupes
        Groupe groupe1 = new Groupe("GRP001", "Groupe A");
        Groupe groupe2 = new Groupe("GRP002", "Groupe B");
        groupeRepository.saveAll(List.of(groupe1, groupe2));
        log.info("✅ Groupes créés");
        
        // Associer cours aux groupes
        CoursGroupe cg1 = new CoursGroupe("CG001", groupe1, cours1);
        CoursGroupe cg2 = new CoursGroupe("CG002", groupe1, cours2);
        CoursGroupe cg3 = new CoursGroupe("CG003", groupe2, cours3);
        coursGroupeRepository.saveAll(List.of(cg1, cg2, cg3));
        log.info("✅ Associations cours-groupes créées");
        
        // Créer des étudiants (les Users seront créés automatiquement par EtudiantService)
        // Chercher par matricule au lieu de l'ID car l'ID peut être un UUID
        Etudiant etudiant1 = etudiantRepository.findByMatricule("MAT001")
            .orElseGet(() -> {
                Etudiant e = new Etudiant(null, "MAT001", "Ben Ali", "Ahmed", "ahmed@email.com", LocalDate.now());
                return etudiantService.createEtudiant(e);
            });
        Etudiant etudiant2 = etudiantRepository.findByMatricule("MAT002")
            .orElseGet(() -> {
                Etudiant e = new Etudiant(null, "MAT002", "Trabelsi", "Fatma", "fatma@email.com", LocalDate.now());
                return etudiantService.createEtudiant(e);
            });
        Etudiant etudiant3 = etudiantRepository.findByMatricule("MAT003")
            .orElseGet(() -> {
                Etudiant e = new Etudiant(null, "MAT003", "Khelifi", "Mohamed", "mohamed@email.com", LocalDate.now());
                return etudiantService.createEtudiant(e);
            });
        Etudiant etudiant4 = etudiantRepository.findByMatricule("MAT004")
            .orElseGet(() -> {
                Etudiant e = new Etudiant(null, "MAT004", "Amri", "Sana", "sana@email.com", LocalDate.now());
                return etudiantService.createEtudiant(e);
            });
        
        // S'assurer que les étudiants ont des Users
        if (etudiant1.getUser() == null) {
            log.warn("⚠️ Étudiant 1 (MAT001) n'a pas de User, création...");
            String login = etudiant1.getMatricule().toLowerCase();
            try {
                User user = userService.createUser(login, login, Role.ETUDIANT);
                etudiant1.setUser(user);
                etudiantRepository.save(etudiant1);
                log.info("✅ User créé pour étudiant 1: {}", login);
            } catch (Exception e) {
                log.error("❌ Erreur lors de la création du User pour étudiant 1: {}", e.getMessage());
            }
        }
        if (etudiant2.getUser() == null) {
            log.warn("⚠️ Étudiant 2 (MAT002) n'a pas de User, création...");
            String login = etudiant2.getMatricule().toLowerCase();
            try {
                User user = userService.createUser(login, login, Role.ETUDIANT);
                etudiant2.setUser(user);
                etudiantRepository.save(etudiant2);
                log.info("✅ User créé pour étudiant 2: {}", login);
            } catch (Exception e) {
                log.error("❌ Erreur lors de la création du User pour étudiant 2: {}", e.getMessage());
            }
        }
        if (etudiant3.getUser() == null) {
            log.warn("⚠️ Étudiant 3 (MAT003) n'a pas de User, création...");
            String login = etudiant3.getMatricule().toLowerCase();
            try {
                User user = userService.createUser(login, login, Role.ETUDIANT);
                etudiant3.setUser(user);
                etudiantRepository.save(etudiant3);
                log.info("✅ User créé pour étudiant 3: {}", login);
            } catch (Exception e) {
                log.error("❌ Erreur lors de la création du User pour étudiant 3: {}", e.getMessage());
            }
        }
        if (etudiant4.getUser() == null) {
            log.warn("⚠️ Étudiant 4 (MAT004) n'a pas de User, création...");
            String login = etudiant4.getMatricule().toLowerCase();
            try {
                User user = userService.createUser(login, login, Role.ETUDIANT);
                etudiant4.setUser(user);
                etudiantRepository.save(etudiant4);
                log.info("✅ User créé pour étudiant 4: {}", login);
            } catch (Exception e) {
                log.error("❌ Erreur lors de la création du User pour étudiant 4: {}", e.getMessage());
            }
        }
        
        log.info("✅ Étudiants vérifiés/créés");
        
        // Afficher les credentials des étudiants
        log.info("📋 Credentials Étudiants:");
        if (etudiant1.getUser() != null) {
            log.info("   - Étudiant 1 (MAT001): Login: {}, Password: {}, Email: {}", 
                etudiant1.getUser().getLogin(), etudiant1.getUser().getLogin(), etudiant1.getEmail());
        } else {
            log.warn("   - Étudiant 1 (MAT001): PAS DE USER!");
        }
        if (etudiant2.getUser() != null) {
            log.info("   - Étudiant 2 (MAT002): Login: {}, Password: {}, Email: {}", 
                etudiant2.getUser().getLogin(), etudiant2.getUser().getLogin(), etudiant2.getEmail());
        } else {
            log.warn("   - Étudiant 2 (MAT002): PAS DE USER!");
        }
        if (etudiant3.getUser() != null) {
            log.info("   - Étudiant 3 (MAT003): Login: {}, Password: {}, Email: {}", 
                etudiant3.getUser().getLogin(), etudiant3.getUser().getLogin(), etudiant3.getEmail());
        } else {
            log.warn("   - Étudiant 3 (MAT003): PAS DE USER!");
        }
        if (etudiant4.getUser() != null) {
            log.info("   - Étudiant 4 (MAT004): Login: {}, Password: {}, Email: {}", 
                etudiant4.getUser().getLogin(), etudiant4.getUser().getLogin(), etudiant4.getEmail());
        } else {
            log.warn("   - Étudiant 4 (MAT004): PAS DE USER!");
        }
        
        // Créer des inscriptions
        Inscription ins1 = new Inscription("INS001", LocalDate.now(), "ACTIVE", etudiant1, cours1);
        Inscription ins2 = new Inscription("INS002", LocalDate.now(), "ACTIVE", etudiant1, cours2);
        Inscription ins3 = new Inscription("INS003", LocalDate.now(), "ACTIVE", etudiant2, cours1);
        Inscription ins4 = new Inscription("INS004", LocalDate.now(), "ACTIVE", etudiant3, cours3);
        Inscription ins5 = new Inscription("INS005", LocalDate.now(), "ACTIVE", etudiant4, cours2);
        inscriptionRepository.saveAll(List.of(ins1, ins2, ins3, ins4, ins5));
        log.info("✅ Inscriptions créées");
        
        // Créer des séances
        Seance seance1 = new Seance("SEA001", LocalDate.now().plusDays(1), LocalTime.of(9, 0), "Salle A", cours1, formateur1);
        Seance seance2 = new Seance("SEA002", LocalDate.now().plusDays(2), LocalTime.of(14, 0), "Salle B", cours2, formateur2);
        Seance seance3 = new Seance("SEA003", LocalDate.now().plusDays(3), LocalTime.of(10, 30), "Salle C", cours3, formateur3);
        // Ajouter plus de séances pour formateur1 (dupont)
        Seance seance4 = new Seance("SEA004", LocalDate.now().plusDays(4), LocalTime.of(11, 0), "Salle D", cours1, formateur1);
        Seance seance5 = new Seance("SEA005", LocalDate.now().plusDays(5), LocalTime.of(15, 30), "Salle E", cours1, formateur1);
        Seance seance6 = new Seance("SEA006", LocalDate.now().plusDays(6), LocalTime.of(8, 30), "Salle F", cours1, formateur1);
        seanceRepository.saveAll(List.of(seance1, seance2, seance3, seance4, seance5, seance6));
        log.info("✅ Séances créées (6 séances au total, dont 4 pour formateur1)");
        
        // Créer des notes
        Note note1 = new Note("NOTE001", 15.5f, LocalDate.now(), etudiant1, cours1);
        Note note2 = new Note("NOTE002", 18.0f, LocalDate.now(), etudiant1, cours2);
        Note note3 = new Note("NOTE003", 12.0f, LocalDate.now(), etudiant2, cours1);
        Note note4 = new Note("NOTE004", 16.5f, LocalDate.now(), etudiant3, cours3);
        Note note5 = new Note("NOTE005", 14.0f, LocalDate.now(), etudiant4, cours2);
        noteRepository.saveAll(List.of(note1, note2, note3, note4, note5));
        log.info("✅ Notes créées");
        
        log.info("=== Données de test créées avec succès ===");
    }
}

