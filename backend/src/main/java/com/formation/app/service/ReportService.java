package com.formation.app.service;

import com.formation.app.entity.Cours;
import com.formation.app.entity.Etudiant;
import com.formation.app.entity.Note;
import com.formation.app.repository.CoursRepository;
import com.formation.app.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service pour la génération de rapports et statistiques en PDF
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReportService {
    
    private final NoteRepository noteRepository;
    private final CoursRepository coursRepository;
    private final NoteService noteService;
    
    /**
     * Génère un rapport de notes pour un étudiant en format PDF
     * @param etudiant l'étudiant pour lequel générer le rapport
     * @return byte array contenant le PDF
     */
    public byte[] genererRapportNotes(Etudiant etudiant) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            
            try {
                PDType1Font fontTitle = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                PDType1Font fontHeader = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                PDType1Font fontNormal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
                
                float margin = 50;
                float yPosition = 750;
                float lineHeight = 20;
                
                // Titre
                contentStream.beginText();
                contentStream.setFont(fontTitle, 18);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("RAPPORT DE NOTES");
                contentStream.endText();
                
                yPosition -= 40;
                
                // Informations étudiant
                contentStream.beginText();
                contentStream.setFont(fontHeader, 14);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Informations de l'étudiant");
                contentStream.endText();
                
                yPosition -= 25;
                
                DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                
                List<String> infosEtudiant = List.of(
                    "Matricule: " + etudiant.getMatricule(),
                    "Nom: " + etudiant.getNom() + " " + etudiant.getPrenom(),
                    "Email: " + etudiant.getEmail(),
                    "Date d'inscription: " + (etudiant.getDateInscription() != null ? 
                        etudiant.getDateInscription().format(dateFormat) : "N/A")
                );
                
                for (String info : infosEtudiant) {
                    contentStream.beginText();
                    contentStream.setFont(fontNormal, 12);
                    contentStream.newLineAtOffset(margin + 10, yPosition);
                    contentStream.showText(info);
                    contentStream.endText();
                    yPosition -= lineHeight;
                }
                
                yPosition -= 20;
                
                // Notes
                List<Note> notes = noteRepository.findByEtudiantId(etudiant.getId());
                
                contentStream.beginText();
                contentStream.setFont(fontHeader, 14);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Notes");
                contentStream.endText();
                
                yPosition -= 30;
                
                // En-tête du tableau
                float col1X = margin;
                float col3X = margin + 350;
                float col4X = margin + 450;
                
                contentStream.beginText();
                contentStream.setFont(fontHeader, 11);
                contentStream.newLineAtOffset(col1X, yPosition);
                contentStream.showText("Cours");
                contentStream.endText();
                
                contentStream.beginText();
                contentStream.setFont(fontHeader, 11);
                contentStream.newLineAtOffset(col3X, yPosition);
                contentStream.showText("Note");
                contentStream.endText();
                
                contentStream.beginText();
                contentStream.setFont(fontHeader, 11);
                contentStream.newLineAtOffset(col4X, yPosition);
                contentStream.showText("Date");
                contentStream.endText();
                
                // Ligne de séparation
                contentStream.moveTo(margin, yPosition - 5);
                contentStream.lineTo(550, yPosition - 5);
                contentStream.stroke();
                
                yPosition -= 25;
                
                // Données des notes
                for (Note note : notes) {
                    if (yPosition < 100) {
                        // Nouvelle page si nécessaire
                        contentStream.close();
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        yPosition = 750;
                        
                        // Réinitialiser les polices pour la nouvelle page
                        fontTitle = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                        fontHeader = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                        fontNormal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
                    }
                    
                    String coursTitre = note.getCours().getTitre();
                    if (coursTitre.length() > 30) {
                        coursTitre = coursTitre.substring(0, 27) + "...";
                    }
                    
                    contentStream.beginText();
                    contentStream.setFont(fontNormal, 10);
                    contentStream.newLineAtOffset(col1X, yPosition);
                    contentStream.showText(coursTitre);
                    contentStream.endText();
                    
                    contentStream.beginText();
                    contentStream.setFont(fontNormal, 10);
                    contentStream.newLineAtOffset(col3X, yPosition);
                    contentStream.showText(String.format("%.2f", note.getValeur()));
                    contentStream.endText();
                    
                    contentStream.beginText();
                    contentStream.setFont(fontNormal, 10);
                    contentStream.newLineAtOffset(col4X, yPosition);
                    contentStream.showText(note.getDateSaisie().format(dateFormat));
                    contentStream.endText();
                    
                    yPosition -= lineHeight;
                }
                
                yPosition -= 20;
                
                // Moyenne générale
                Double moyenneGenerale = noteService.calculerMoyenneGeneraleEtudiant(etudiant.getId());
                
                contentStream.beginText();
                contentStream.setFont(fontHeader, 12);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Moyenne générale: " + 
                    (moyenneGenerale != null ? String.format("%.2f", moyenneGenerale) : "N/A"));
                contentStream.endText();
                
                yPosition -= 20;
                
                contentStream.beginText();
                contentStream.setFont(fontNormal, 10);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Nombre total de notes: " + notes.size());
                contentStream.endText();
            } finally {
                if (contentStream != null) {
                    contentStream.close();
                }
            }
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            log.info("Rapport de notes PDF généré pour l'étudiant: {}", etudiant.getMatricule());
            return baos.toByteArray();
            
        } catch (IOException e) {
            log.error("Erreur lors de la génération du rapport PDF pour l'étudiant: {}", 
                etudiant.getMatricule(), e);
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }
    
    /**
     * Génère un rapport pour un cours en format PDF
     * @param cours le cours pour lequel générer le rapport
     * @return byte array contenant le PDF
     */
    public byte[] genererRapportCours(Cours cours) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            
            try {
                PDType1Font fontTitle = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                PDType1Font fontHeader = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                PDType1Font fontNormal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
                
                float margin = 50;
                float yPosition = 750;
                float lineHeight = 20;
                
                // Titre
                contentStream.beginText();
                contentStream.setFont(fontTitle, 18);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("RAPPORT DE COURS");
                contentStream.endText();
                
                yPosition -= 40;
                
                // Informations cours
                contentStream.beginText();
                contentStream.setFont(fontHeader, 14);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Informations du cours");
                contentStream.endText();
                
                yPosition -= 25;
                
                List<String> infosCours = List.of(
                    "Code: " + cours.getCode(),
                    "Titre: " + cours.getTitre(),
                    "Description: " + (cours.getDescription() != null && !cours.getDescription().isEmpty() ? 
                        cours.getDescription() : "Aucune description")
                );
                
                for (String info : infosCours) {
                    if (info.length() > 80) {
                        // Gérer les descriptions longues
                        String[] parts = info.split(" ", 2);
                        if (parts.length > 1) {
                            contentStream.beginText();
                            contentStream.setFont(fontNormal, 12);
                            contentStream.newLineAtOffset(margin + 10, yPosition);
                            contentStream.showText(parts[0]);
                            contentStream.endText();
                            yPosition -= lineHeight;
                            
                            // Description sur plusieurs lignes si nécessaire
                            String desc = parts[1];
                            while (desc.length() > 80 && yPosition > 100) {
                                int splitIndex = desc.substring(0, Math.min(80, desc.length())).lastIndexOf(' ');
                                if (splitIndex == -1) splitIndex = Math.min(80, desc.length());
                                
                                contentStream.beginText();
                                contentStream.setFont(fontNormal, 12);
                                contentStream.newLineAtOffset(margin + 10, yPosition);
                                contentStream.showText(desc.substring(0, splitIndex));
                                contentStream.endText();
                                yPosition -= lineHeight;
                                
                                desc = desc.substring(splitIndex).trim();
                            }
                            if (!desc.isEmpty() && yPosition > 100) {
                                contentStream.beginText();
                                contentStream.setFont(fontNormal, 12);
                                contentStream.newLineAtOffset(margin + 10, yPosition);
                                contentStream.showText(desc);
                                contentStream.endText();
                                yPosition -= lineHeight;
                            }
                        } else {
                            contentStream.beginText();
                            contentStream.setFont(fontNormal, 12);
                            contentStream.newLineAtOffset(margin + 10, yPosition);
                            contentStream.showText(info);
                            contentStream.endText();
                            yPosition -= lineHeight;
                        }
                    } else {
                        contentStream.beginText();
                        contentStream.setFont(fontNormal, 12);
                        contentStream.newLineAtOffset(margin + 10, yPosition);
                        contentStream.showText(info);
                        contentStream.endText();
                        yPosition -= lineHeight;
                    }
                }
                
                yPosition -= 20;
                
                // Statistiques
                Double moyenne = noteService.calculerMoyenneCours(cours.getCode());
                Double tauxReussite = noteService.calculerTauxReussite(cours.getCode());
                List<Note> notes = noteRepository.findByCoursCode(cours.getCode());
                
                contentStream.beginText();
                contentStream.setFont(fontHeader, 14);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Statistiques");
                contentStream.endText();
                
                yPosition -= 25;
                
                DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                
                List<String> stats = List.of(
                    "Moyenne du cours: " + (moyenne != null ? String.format("%.2f", moyenne) : "N/A"),
                    "Taux de réussite: " + (tauxReussite != null ? String.format("%.2f%%", tauxReussite) : "N/A"),
                    "Nombre d'étudiants notés: " + notes.size()
                );
                
                for (String stat : stats) {
                    contentStream.beginText();
                    contentStream.setFont(fontNormal, 12);
                    contentStream.newLineAtOffset(margin + 10, yPosition);
                    contentStream.showText(stat);
                    contentStream.endText();
                    yPosition -= lineHeight;
                }
                
                yPosition -= 20;
                
                // Notes des étudiants
                contentStream.beginText();
                contentStream.setFont(fontHeader, 14);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Notes des étudiants");
                contentStream.endText();
                
                yPosition -= 30;
                
                // En-tête du tableau
                float tableStartX = margin;
                float col1X = tableStartX;
                float col2X = tableStartX + 150;
                float col3X = tableStartX + 350;
                float col4X = tableStartX + 450;
                
                contentStream.beginText();
                contentStream.setFont(fontHeader, 11);
                contentStream.newLineAtOffset(col1X, yPosition);
                contentStream.showText("Matricule");
                contentStream.endText();
                
                contentStream.beginText();
                contentStream.setFont(fontHeader, 11);
                contentStream.newLineAtOffset(col2X, yPosition);
                contentStream.showText("Étudiant");
                contentStream.endText();
                
                contentStream.beginText();
                contentStream.setFont(fontHeader, 11);
                contentStream.newLineAtOffset(col3X, yPosition);
                contentStream.showText("Note");
                contentStream.endText();
                
                contentStream.beginText();
                contentStream.setFont(fontHeader, 11);
                contentStream.newLineAtOffset(col4X, yPosition);
                contentStream.showText("Date");
                contentStream.endText();
                
                // Ligne de séparation
                contentStream.moveTo(margin, yPosition - 5);
                contentStream.lineTo(550, yPosition - 5);
                contentStream.stroke();
                
                yPosition -= 25;
                
                // Données des notes
                for (Note note : notes) {
                    if (yPosition < 100) {
                        // Nouvelle page si nécessaire
                        contentStream.close();
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        yPosition = 750;
                        
                        // Réinitialiser les polices pour la nouvelle page
                        fontTitle = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                        fontHeader = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                        fontNormal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
                    }
                    
                    contentStream.beginText();
                    contentStream.setFont(fontNormal, 10);
                    contentStream.newLineAtOffset(col1X, yPosition);
                    contentStream.showText(note.getEtudiant().getMatricule());
                    contentStream.endText();
                    
                    String nomComplet = note.getEtudiant().getNomComplet();
                    if (nomComplet.length() > 20) {
                        nomComplet = nomComplet.substring(0, 17) + "...";
                    }
                    
                    contentStream.beginText();
                    contentStream.setFont(fontNormal, 10);
                    contentStream.newLineAtOffset(col2X, yPosition);
                    contentStream.showText(nomComplet);
                    contentStream.endText();
                    
                    contentStream.beginText();
                    contentStream.setFont(fontNormal, 10);
                    contentStream.newLineAtOffset(col3X, yPosition);
                    contentStream.showText(String.format("%.2f", note.getValeur()));
                    contentStream.endText();
                    
                    contentStream.beginText();
                    contentStream.setFont(fontNormal, 10);
                    contentStream.newLineAtOffset(col4X, yPosition);
                    contentStream.showText(note.getDateSaisie().format(dateFormat));
                    contentStream.endText();
                    
                    yPosition -= lineHeight;
                }
            } finally {
                if (contentStream != null) {
                    contentStream.close();
                }
            }
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            log.info("Rapport de cours PDF généré pour: {}", cours.getCode());
            return baos.toByteArray();
            
        } catch (IOException e) {
            log.error("Erreur lors de la génération du rapport PDF pour le cours: {}", 
                cours.getCode(), e);
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }
    
    /**
     * Obtient les statistiques générales des cours
     * @return Map contenant les statistiques
     */
    public Map<String, Object> getStatistiquesCours() {
        Map<String, Object> stats = new HashMap<>();
        
        List<Cours> tousLesCours = coursRepository.findAll();
        stats.put("nombreTotalCours", tousLesCours.size());
        
        // Cours les plus suivis (par nombre d'inscriptions)
        // Cette logique sera implémentée dans InscriptionService
        
        log.info("Statistiques des cours générées");
        
        return stats;
    }
    
    /**
     * Obtient la liste des cours les plus suivis
     * @return Liste des cours avec leur nombre d'inscriptions
     */
    public List<Map<String, Object>> getCoursPlusSuivis() {
        // Cette méthode nécessitera une requête plus complexe
        // Pour l'instant, on retourne une liste vide
        log.info("Liste des cours les plus suivis générée");
        return List.of();
    }
}

