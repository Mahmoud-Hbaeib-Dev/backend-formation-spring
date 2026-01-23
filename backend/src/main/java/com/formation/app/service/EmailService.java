package com.formation.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service pour l'envoi d'emails
 * Si JavaMailSender n'est pas configuré, les emails seront seulement loggés
 */
@Service
@Slf4j
public class EmailService {
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username:}")
    private String fromEmail;
    
    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;
    
    /**
     * Envoie un email simple
     */
    public void sendEmail(String to, String subject, String text) {
        try {
            if (mailSender != null) {
                // Envoyer l'email via SMTP si configuré
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromEmail.isEmpty() ? "noreply@formation.com" : fromEmail);
                message.setTo(to);
                message.setSubject(subject);
                message.setText(text);
                
                mailSender.send(message);
                log.info("✅ Email envoyé avec succès à: {}", to);
            } else {
                // Si le serveur SMTP n'est pas configuré, logger seulement
                log.info("=== EMAIL (SMTP non configuré - log seulement) ===");
                log.info("À: {}", to);
                log.info("Sujet: {}", subject);
                log.info("Message:\n{}", text);
                log.info("==================================================");
                log.warn("⚠️ Pour envoyer de vrais emails, configurez spring.mail.* dans application.properties");
            }
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi de l'email à {}: {}", to, e.getMessage());
            // Ne pas faire échouer la création de l'utilisateur si l'email échoue
            // On log juste l'erreur
        }
    }
    
    /**
     * Envoie les identifiants de connexion à un nouvel utilisateur
     */
    public void sendCredentialsEmail(String email, String nom, String prenom, String matricule, String role) {
        String roleText = role.equals("ETUDIANT") ? "étudiant" : "formateur";
        String loginUrl = frontendUrl + "/login";
        
        // Construire le nom complet
        String nomComplet = prenom != null && !prenom.isEmpty() && !prenom.equals(nom) 
            ? prenom + " " + nom 
            : nom;
        
        String subject = "Bienvenue au Centre de Formation - Vos identifiants de connexion";
        String text = String.format(
            "Bonjour %s,\n\n" +
            "Votre compte %s a été créé avec succès au Centre de Formation.\n\n" +
            "Vos identifiants de connexion :\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
            "Matricule (Login) : %s\n" +
            "Mot de passe      : %s\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
            "⚠️ IMPORTANT : Pour votre sécurité, veuillez changer votre mot de passe après votre première connexion.\n\n" +
            "Pour vous connecter, rendez-vous sur :\n" +
            "%s\n\n" +
            "Cordialement,\n" +
            "L'équipe du Centre de Formation",
            nomComplet,
            roleText,
            matricule.toLowerCase(),
            matricule.toLowerCase(),
            loginUrl
        );
        
        sendEmail(email, subject, text);
    }
}
