package com.formation.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test d'intégration pour vérifier que l'application démarre correctement
 */
@SpringBootTest
@ActiveProfiles("test")
class CentreFormationApplicationTests {
    
    @Test
    void contextLoads() {
        // Ce test vérifie que le contexte Spring se charge correctement
        // Si cette méthode passe, l'application est correctement configurée
    }
}

