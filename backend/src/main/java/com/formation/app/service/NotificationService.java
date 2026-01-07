package com.formation.app.service;

import com.formation.app.entity.Cours;
import com.formation.app.entity.Etudiant;
import com.formation.app.entity.Formateur;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service pour l'envoi de notifications (emails)
 * Pour le développement, on utilise un MockMailService
 * En production, on peut intégrer un service d'email réel (SMTP, SendGrid, etc.)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    /**
     * Envoie un email de confirmation d'inscription à un étudiant
     */
    public void sendInscriptionEmail(Etudiant etudiant, Cours cours) {
        String subject = "Confirmation d'inscription - " + cours.getTitre();
        String message = String.format(
            "Bonjour %s %s,\n\n" +
            "Votre inscription au cours '%s' a été confirmée.\n" +
            "Code du cours: %s\n\n" +
            "Cordialement,\n" +
            "L'équipe du centre de formation",
            etudiant.getPrenom(),
            etudiant.getNom(),
            cours.getTitre(),
            cours.getCode()
        );
        
        // En développement, on log juste le message
        log.info("=== EMAIL D'INSCRIPTION ===");
        log.info("À: {}", etudiant.getEmail());
        log.info("Sujet: {}", subject);
        log.info("Message:\n{}", message);
        log.info("===========================");
        
        // TODO: En production, utiliser un service d'email réel
        // emailService.send(etudiant.getEmail(), subject, message);
    }
    
    /**
     * Envoie un email de désinscription à un étudiant
     */
    public void sendDesinscriptionEmail(Etudiant etudiant, Cours cours) {
        String subject = "Désinscription - " + cours.getTitre();
        String message = String.format(
            "Bonjour %s %s,\n\n" +
            "Votre désinscription du cours '%s' a été enregistrée.\n" +
            "Code du cours: %s\n\n" +
            "Cordialement,\n" +
            "L'équipe du centre de formation",
            etudiant.getPrenom(),
            etudiant.getNom(),
            cours.getTitre(),
            cours.getCode()
        );
        
        log.info("=== EMAIL DE DÉSINSCRIPTION ===");
        log.info("À: {}", etudiant.getEmail());
        log.info("Sujet: {}", subject);
        log.info("Message:\n{}", message);
        log.info("===============================");
    }
    
    /**
     * Notifie un formateur d'une nouvelle inscription à son cours
     */
    public void notifyFormateurInscription(Formateur formateur, Etudiant etudiant, Cours cours) {
        String subject = "Nouvelle inscription - " + cours.getTitre();
        String message = String.format(
            "Bonjour %s,\n\n" +
            "Un nouvel étudiant s'est inscrit à votre cours:\n" +
            "- Étudiant: %s %s (%s)\n" +
            "- Cours: %s (%s)\n\n" +
            "Cordialement,\n" +
            "L'équipe du centre de formation",
            formateur.getNom(),
            etudiant.getPrenom(),
            etudiant.getNom(),
            etudiant.getMatricule(),
            cours.getTitre(),
            cours.getCode()
        );
        
        log.info("=== NOTIFICATION FORMATEUR ===");
        log.info("À: {}", formateur.getEmail());
        log.info("Sujet: {}", subject);
        log.info("Message:\n{}", message);
        log.info("==============================");
    }
}

