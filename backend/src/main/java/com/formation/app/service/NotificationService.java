package com.formation.app.service;

import com.formation.app.entity.Cours;
import com.formation.app.entity.Etudiant;
import com.formation.app.entity.Formateur;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service pour l'envoi de notifications (emails)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final EmailService emailService;
    
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
        
        // Envoyer l'email
        emailService.sendEmail(etudiant.getEmail(), subject, message);
        log.info("✅ Email d'inscription envoyé à: {}", etudiant.getEmail());
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
        
        // Envoyer l'email
        emailService.sendEmail(etudiant.getEmail(), subject, message);
        log.info("✅ Email de désinscription envoyé à: {}", etudiant.getEmail());
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
        
        // Envoyer l'email
        emailService.sendEmail(formateur.getEmail(), subject, message);
        log.info("✅ Notification envoyée au formateur: {}", formateur.getEmail());
    }
    
    /**
     * Envoie les identifiants de connexion à un nouvel étudiant
     */
    public void sendEtudiantCredentials(Etudiant etudiant) {
        emailService.sendCredentialsEmail(
            etudiant.getEmail(),
            etudiant.getNom(),
            etudiant.getPrenom(),
            etudiant.getMatricule(),
            "ETUDIANT"
        );
        log.info("✅ Email de bienvenue avec identifiants envoyé à l'étudiant: {}", etudiant.getEmail());
    }
    
    /**
     * Envoie les identifiants de connexion à un nouveau formateur
     */
    public void sendFormateurCredentials(Formateur formateur) {
        emailService.sendCredentialsEmail(
            formateur.getEmail(),
            formateur.getNom(),
            "", // Les formateurs n'ont pas de prénom, EmailService gérera cela
            formateur.getMatricule(),
            "FORMATEUR"
        );
        log.info("✅ Email de bienvenue avec identifiants envoyé au formateur: {}", formateur.getEmail());
    }
}

