# 👨‍💻 Guide de développement

Guide complet pour les développeurs souhaitant contribuer au projet Centre de Formation.

## 📋 Table des matières

- [Prérequis](#prérequis)
- [Installation](#installation)
- [Structure du projet](#structure-du-projet)
- [Standards de code](#standards-de-code)
- [Architecture](#architecture)
- [Technologies utilisées](#technologies-utilisées)
- [Workflow de développement](#workflow-de-développement)
- [Tests](#tests)
- [Débogage](#débogage)
- [Contribuer](#contribuer)

## 📦 Prérequis

### Outils nécessaires

- **Java** : JDK 17 ou supérieur
- **Maven** : 3.6+ 
- **IDE** : IntelliJ IDEA (recommandé), Eclipse, VS Code
- **Git** : Pour le contrôle de version
- **MySQL** : 8.0+ (pour production)
- **H2** : Inclus dans les dépendances (pour développement)

### Plugins IDE recommandés

**IntelliJ IDEA** :
- Lombok Plugin
- Spring Boot Plugin
- Maven Helper

**VS Code** :
- Extension Pack for Java
- Spring Boot Extension Pack
- Lombok Annotations Support

## 🚀 Installation

### 1. Cloner le projet

```bash
git clone <repository-url>
cd SPRING/backend
```

### 2. Installer les dépendances

```bash
mvn clean install
```

### 3. Configurer l'IDE

#### IntelliJ IDEA

1. Ouvrir le projet : `File > Open > backend/pom.xml`
2. Maven : `File > Settings > Build > Build Tools > Maven`
   - Maven home directory : Auto-detect
   - User settings file : Auto-detect
3. Lombok : `File > Settings > Build > Compiler > Annotation Processors`
   - ✅ Enable annotation processing

#### Eclipse

1. Importer le projet Maven : `File > Import > Maven > Existing Maven Projects`
2. Installer Lombok : Télécharger depuis https://projectlombok.org/

### 4. Configurer la base de données

#### Mode développement (H2)

Aucune configuration nécessaire. L'application utilise H2 en fichier par défaut.

#### Mode production (MySQL)

1. Créer la base de données :
```sql
CREATE DATABASE formation_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Configurer `application-prod.properties` :
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/formation_db
spring.datasource.username=root
spring.datasource.password=your_password
```

### 5. Lancer l'application

```bash
mvn spring-boot:run
```

Ou depuis l'IDE : Exécuter `CentreFormationApplication.java`

## 📁 Structure du projet

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/formation/app/
│   │   │   ├── entity/          # Entités JPA
│   │   │   ├── repository/      # Repositories Spring Data
│   │   │   ├── service/         # Services métier
│   │   │   ├── controller/
│   │   │   │   ├── api/         # REST Controllers
│   │   │   │   └── web/         # Thymeleaf Controllers
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── config/          # Configuration classes
│   │   │   ├── security/        # Spring Security
│   │   │   ├── exception/       # Exception handlers
│   │   │   └── util/            # Utilitaires
│   │   └── resources/
│   │       ├── templates/       # Thymeleaf templates
│   │       ├── static/          # Static resources
│   │       └── application*.properties
│   └── test/                    # Tests
├── scripts/                     # Scripts de déploiement
├── pom.xml                      # Configuration Maven
└── README.md
```

## 📝 Standards de code

### Conventions de nommage

- **Classes** : PascalCase (`UserService`, `EtudiantController`)
- **Méthodes** : camelCase (`getUserById`, `createEtudiant`)
- **Variables** : camelCase (`userId`, `etudiantName`)
- **Constantes** : UPPER_SNAKE_CASE (`MAX_RETRY_COUNT`)
- **Packages** : lowercase (`com.formation.app.service`)

### Formatage

- **Indentation** : 4 espaces (pas de tabs)
- **Longueur de ligne** : Maximum 120 caractères
- **Imports** : Organisés et sans wildcards (`import java.util.List` pas `import java.util.*`)

### JavaDoc

Toutes les classes publiques doivent avoir une JavaDoc :

```java
/**
 * Service pour la gestion des étudiants
 * 
 * @author Formation Team
 * @version 1.0.0
 */
@Service
public class EtudiantService {
    
    /**
     * Récupère un étudiant par son ID
     * 
     * @param id L'ID de l'étudiant
     * @return L'étudiant trouvé
     * @throws ResourceNotFoundException Si l'étudiant n'existe pas
     */
    public Etudiant getEtudiantById(Long id) {
        // ...
    }
}
```

### Bonnes pratiques

1. **Séparation des responsabilités** :
   - Controller : Gestion des requêtes HTTP
   - Service : Logique métier
   - Repository : Accès aux données

2. **Gestion des exceptions** :
   - Utiliser les exceptions personnalisées
   - Ne jamais exposer les détails techniques aux clients

3. **Validation** :
   - Valider toutes les entrées utilisateur
   - Utiliser `@Valid` et `@NotNull`

4. **Logging** :
   - Utiliser SLF4J avec Logback
   - Niveaux : DEBUG (dev), INFO (prod), ERROR (erreurs)

5. **Tests** :
   - Au moins 70% de couverture de code
   - Tests unitaires pour les services
   - Tests d'intégration pour les controllers

## 🏗️ Architecture

### Architecture en couches

```
┌─────────────────────────────────────┐
│         Controllers (API/Web)        │
├─────────────────────────────────────┤
│            Services                  │
├─────────────────────────────────────┤
│          Repositories                │
├─────────────────────────────────────┤
│         Database (JPA/Hibernate)     │
└─────────────────────────────────────┘
```

### Flux de données

1. **Requête HTTP** → Controller
2. **Controller** → Service (validation, logique métier)
3. **Service** → Repository (accès données)
4. **Repository** → Database
5. **Réponse** ← Controller ← Service ← Repository

### Sécurité

- **API REST** (`/api/**`) : JWT Authentication
- **Interface Admin** (`/admin/**`) : Session Authentication
- **Mots de passe** : Hashés avec BCrypt
- **Validation** : Sur toutes les entrées

## 🛠️ Technologies utilisées

### Backend

- **Spring Boot** : 3.2.0
- **Spring Security** : 6.x
- **Spring Data JPA** : Accès aux données
- **Hibernate** : ORM
- **Thymeleaf** : Templates SSR
- **JWT** : Authentification API
- **Lombok** : Réduction du code boilerplate
- **Validation** : Bean Validation
- **Actuator** : Monitoring

### Base de données

- **H2** : Développement (fichier)
- **MySQL** : Production

### Build

- **Maven** : Gestion des dépendances
- **Java** : 17

## 🔄 Workflow de développement

### 1. Créer une branche

```bash
git checkout -b feature/nouvelle-fonctionnalite
```

### 2. Développer

- Écrire le code
- Ajouter des tests
- Vérifier les standards

### 3. Tester

```bash
mvn test
```

### 4. Commit

```bash
git add .
git commit -m "feat: ajout de la fonctionnalité X"
```

**Conventions de commit** :
- `feat:` : Nouvelle fonctionnalité
- `fix:` : Correction de bug
- `docs:` : Documentation
- `refactor:` : Refactoring
- `test:` : Tests

### 5. Push et Pull Request

```bash
git push origin feature/nouvelle-fonctionnalite
```

Créer une Pull Request sur GitHub/GitLab.

## 🧪 Tests

### Exécuter tous les tests

```bash
mvn test
```

### Exécuter un test spécifique

```bash
mvn test -Dtest=UserServiceTest
```

### Types de tests

1. **Tests unitaires** :
   - Services avec Mockito
   - Mocker les repositories

2. **Tests d'intégration** :
   - Repositories avec H2
   - Controllers avec MockMvc

3. **Tests end-to-end** :
   - Scénarios complets

### Exemple de test unitaire

```java
@ExtendWith(MockitoExtension.class)
class EtudiantServiceTest {
    
    @Mock
    private EtudiantRepository etudiantRepository;
    
    @InjectMocks
    private EtudiantService etudiantService;
    
    @Test
    void testGetEtudiantById() {
        // Given
        Long id = 1L;
        Etudiant etudiant = new Etudiant();
        when(etudiantRepository.findById(id)).thenReturn(Optional.of(etudiant));
        
        // When
        Etudiant result = etudiantService.getEtudiantById(id);
        
        // Then
        assertNotNull(result);
        verify(etudiantRepository).findById(id);
    }
}
```

## 🐛 Débogage

### Logs

Les logs sont configurés dans `logback-spring.xml` :

- **Console** : Tous les niveaux
- **Fichier** : INFO et ERROR dans `logs/`

### Actuator

Monitoring disponible sur `/actuator/health`

### H2 Console

En développement : `http://localhost:8080/h2-console`

### Breakpoints

Utiliser les breakpoints dans l'IDE pour déboguer.

## 🤝 Contribuer

### Processus de contribution

1. **Fork** le projet
2. **Créer** une branche (`git checkout -b feature/AmazingFeature`)
3. **Commit** vos changements (`git commit -m 'feat: Add AmazingFeature'`)
4. **Push** vers la branche (`git push origin feature/AmazingFeature`)
5. **Ouvrir** une Pull Request

### Checklist avant PR

- [ ] Code conforme aux standards
- [ ] Tests ajoutés et passent
- [ ] Documentation mise à jour
- [ ] Pas d'erreurs de compilation
- [ ] Pas d'erreurs de linter
- [ ] JavaDoc ajoutée

### Code Review

Toutes les PR sont revues avant merge :
- Qualité du code
- Respect des standards
- Tests suffisants
- Documentation complète

## 📚 Ressources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [JPA Documentation](https://spring.io/projects/spring-data-jpa)
- [Thymeleaf Documentation](https://www.thymeleaf.org/)

## ❓ Support

Pour toute question :
1. Consultez la documentation
2. Vérifiez les issues existantes
3. Créez une nouvelle issue si nécessaire

---

**Version** : 1.0.0  
**Dernière mise à jour** : 2025

