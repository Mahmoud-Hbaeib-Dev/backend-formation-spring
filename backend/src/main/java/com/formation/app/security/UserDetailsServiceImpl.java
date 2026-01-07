package com.formation.app.security;

import com.formation.app.entity.Etudiant;
import com.formation.app.entity.Formateur;
import com.formation.app.entity.User;
import com.formation.app.repository.EtudiantRepository;
import com.formation.app.repository.FormateurRepository;
import com.formation.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service pour charger les UserDetails depuis la base de données
 * Supporte la connexion avec login, email ou nom
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final EtudiantRepository etudiantRepository;
    private final FormateurRepository formateurRepository;
    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("🔍 [USER DETAILS] Recherche de l'utilisateur avec: {}", username);
        
        // 1. Essayer de trouver par login (username)
        log.info("🔍 [USER DETAILS] Tentative 1: Recherche par login...");
        User user = userRepository.findByLogin(username).orElse(null);
        
        if (user != null) {
            log.info("✅ [USER DETAILS] Utilisateur trouvé par login: {}", user.getLogin());
            return new UserDetailsImpl(user);
        }
        log.info("❌ [USER DETAILS] Aucun utilisateur trouvé par login");
        
        // 2. Si pas trouvé, essayer de trouver par email (via Etudiant)
        log.info("🔍 [USER DETAILS] Tentative 2: Recherche par email (Etudiant)...");
        Etudiant etudiant = etudiantRepository.findByEmail(username).orElse(null);
        if (etudiant != null) {
            log.info("✅ [USER DETAILS] Étudiant trouvé par email: {} {}", etudiant.getPrenom(), etudiant.getNom());
            if (etudiant.getUser() != null) {
                log.info("✅ [USER DETAILS] User associé trouvé: {}", etudiant.getUser().getLogin());
                return new UserDetailsImpl(etudiant.getUser());
            } else {
                log.error("❌ [USER DETAILS] Étudiant trouvé mais PAS de User associé! ID: {}, Email: {}", 
                    etudiant.getId(), etudiant.getEmail());
            }
        } else {
            log.info("❌ [USER DETAILS] Aucun étudiant trouvé par email");
        }
        
        // 3. Si pas trouvé, essayer de trouver par email (via Formateur)
        log.info("🔍 [USER DETAILS] Tentative 3: Recherche par email (Formateur)...");
        Formateur formateur = formateurRepository.findByEmail(username).orElse(null);
        if (formateur != null) {
            log.info("✅ [USER DETAILS] Formateur trouvé par email: {}", formateur.getNom());
            if (formateur.getUser() != null) {
                log.info("✅ [USER DETAILS] User associé trouvé: {}", formateur.getUser().getLogin());
                return new UserDetailsImpl(formateur.getUser());
            } else {
                log.error("❌ [USER DETAILS] Formateur trouvé mais PAS de User associé! ID: {}, Email: {}", 
                    formateur.getId(), formateur.getEmail());
            }
        } else {
            log.info("❌ [USER DETAILS] Aucun formateur trouvé par email");
        }
        
        // 4. Si toujours pas trouvé, lever une exception
        log.error("❌ [USER DETAILS] Utilisateur non trouvé avec: {} (login, email ou nom)", username);
        throw new UsernameNotFoundException("Utilisateur non trouvé avec: " + username + " (login, email ou nom)");
    }
}

