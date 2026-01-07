# 🎓 Centre de Formation - Application de Gestion

Application Spring Boot complète pour la gestion d'un centre de formation, avec interface d'administration (SSR) et API REST pour applications clientes.

## 📋 Table des matières

- [Fonctionnalités](#fonctionnalités)
- [Architecture](#architecture)
- [Technologies](#technologies)
- [Prérequis](#prérequis)
- [Installation](#installation)
- [Configuration](#configuration)
- [Utilisation](#utilisation)
- [API REST](#api-rest)
- [Tests](#tests)
- [Déploiement](#déploiement)
- [Documentation](#documentation)

## ✨ Fonctionnalités

### Gestion des utilisateurs
- ✅ Authentification JWT pour API REST
- ✅ Authentification session pour interface admin
- ✅ Gestion des rôles (ADMIN, FORMATEUR, ETUDIANT)
- ✅ Hachage des mots de passe avec BCrypt

### Gestion pédagogique
- ✅ **Sessions** : Gestion des sessions pédagogiques (S1, S2)
- ✅ **Formateurs** : CRUD complet avec spécialités
- ✅ **Étudiants** : Gestion des étudiants avec matricules uniques
- ✅ **Cours** : Création et gestion des cours
- ✅ **Groupes** : Organisation des étudiants en groupes
- ✅ **Inscriptions** : Inscription des étudiants aux cours
- ✅ **Séances** : Planning et gestion des séances de cours
- ✅ **Notes** : Attribution et suivi des notes

### Interface d'administration
- ✅ Dashboard avec statistiques
- ✅ CRUD complet pour toutes les entités
- ✅ Planning et emploi du temps
- ✅ Statistiques et rapports
- ✅ Interface responsive avec Bootstrap

### API REST
- ✅ Endpoints RESTful complets
- ✅ Authentification JWT
- ✅ Autorisation par rôle
- ✅ Gestion d'erreurs centralisée

## 🏗️ Architecture

### Architecture Dual

L'application utilise une architecture dual pour répondre à différents besoins :

1. **API REST (`/api/**`)** : Pour applications clientes (React, Angular, etc.)
   - Authentification JWT (stateless)
   - Format JSON
   - CORS configuré

2. **Interface Admin (`/admin/**`)** : Pour l'administration
   - Authentification session (stateful)
   - Rendu serveur avec Thymeleaf
   - Interface complète et intuitive

### Structure des packages

```
com.formation.app/
├── entity/          # Entités JPA (User, Etudiant, Cours, etc.)
├── repository/      # Repositories Spring Data JPA
├── service/         # Logique métier
├── controller/
│   ├── api/         # REST Controllers
│   └── web/         # Thymeleaf Controllers
├── config/          # Configuration (Security, DataInitializer)
├── security/        # JWT, UserDetails
└── exception/       # Gestion des exceptions
```

## 🛠️ Technologies

- **Framework** : Spring Boot 3.2.0
- **Sécurité** : Spring Security 6 (JWT + Session)
- **Persistence** : Spring Data JPA / Hibernate
- **Base de données** : H2 (dev) / MySQL (prod)
- **Templates** : Thymeleaf
- **Build** : Maven
- **Java** : 17
- **Autres** : Lombok, Validation, Actuator

## 📦 Prérequis

- Java 17 ou supérieur
- Maven 3.6+
- MySQL 8+ (pour production)
- IDE (IntelliJ IDEA, Eclipse, VS Code)

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

### 3. Configuration

#### Mode développement (H2)

Aucune configuration nécessaire. L'application utilise H2 en mémoire par défaut.

#### Mode production (MySQL)

1. Créer la base de données MySQL :
```sql
CREATE DATABASE formationdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Configurer `src/main/resources/application-prod.properties` :
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/formationdb
spring.datasource.username=your_username
spring.datasource.password=your_password
```

## ⚙️ Configuration

### Profils Spring

- **`dev`** (par défaut) : H2 en fichier, console H2 activée
- **`prod`** : MySQL, pas de console H2
- **`test`** : H2 en mémoire pour les tests

### Variables d'environnement

Pour la production, vous pouvez utiliser des variables d'environnement :

```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/formationdb
export SPRING_DATASOURCE_USERNAME=admin
export SPRING_DATASOURCE_PASSWORD=secret
```

## 🎯 Utilisation

### Démarrer l'application

```bash
# Mode développement
mvn spring-boot:run

# Mode production
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

L'application sera accessible sur : **http://localhost:8080**

### Connexion Admin

1. Accéder à : http://localhost:8080/login
2. **Login** : `admin`
3. **Password** : `admin`

### Accès H2 Console (dev uniquement)

1. URL : http://localhost:8080/h2-console
2. **JDBC URL** : `jdbc:h2:file:./data/formationdb`
3. **Username** : `sa`
4. **Password** : (vide)

## 📡 API REST

### Authentification

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "login": "admin",
  "password": "admin"
}
```

**Réponse** :
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "admin",
  "roles": ["ADMIN"]
}
```

#### Utiliser le token
```http
GET /api/etudiants
Authorization: Bearer <token>
```

### Endpoints principaux

- `GET /api/etudiants` - Liste des étudiants
- `GET /api/etudiants/{id}` - Détails d'un étudiant
- `POST /api/etudiants` - Créer un étudiant
- `PUT /api/etudiants/{id}` - Modifier un étudiant
- `DELETE /api/etudiants/{id}` - Supprimer un étudiant

- `GET /api/cours` - Liste des cours
- `GET /api/cours/{code}` - Détails d'un cours
- `POST /api/cours` - Créer un cours

- `GET /api/inscriptions` - Liste des inscriptions
- `POST /api/inscriptions` - Inscrire un étudiant

- `GET /api/notes` - Liste des notes
- `POST /api/notes` - Attribuer une note

Voir la documentation complète dans `API_DOCUMENTATION.md`

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

- **Tests unitaires** : Services avec Mockito
- **Tests d'intégration** : Repositories avec H2
- **Tests de contrôleurs** : REST Controllers avec MockMvc

## 🚢 Déploiement

### Build JAR

```bash
mvn clean package
```

Le JAR sera généré dans `target/centre-formation-app-1.0.0.jar`

### Exécuter le JAR

```bash
java -jar target/centre-formation-app-1.0.0.jar --spring.profiles.active=prod
```

### Docker (optionnel)

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/centre-formation-app-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 📚 Documentation

### Documentation technique
- [Architecture de l'application](docs/ARCHITECTURE.md)
- [Guide de développement](docs/DEVELOPMENT_GUIDE.md)
- [Documentation API REST](API_DOCUMENTATION.md)
- [Documentation Swagger](http://localhost:8080/swagger-ui.html) (après démarrage)

### Guides utilisateur
- [Guide Administrateur](docs/USER_GUIDE_ADMIN.md)
- [Guide Formateur](docs/USER_GUIDE_FORMATEUR.md)
- [Guide Étudiant](docs/USER_GUIDE_ETUDIANT.md)

### Déploiement
- [Guide de déploiement](DEPLOYMENT.md)
- [Localisation de la base de données](DATABASE_LOCATION.md)
- [Comment se connecter](HOW_TO_LOGIN.md)

## 🔐 Sécurité

- ✅ Mots de passe hachés avec BCrypt
- ✅ JWT pour API REST
- ✅ Sessions sécurisées pour interface admin
- ✅ Protection CSRF
- ✅ Validation des entrées
- ✅ Gestion des erreurs sécurisée

## 📊 Données de test

L'application initialise automatiquement des données de test au démarrage :

- **Utilisateur admin** : `admin` / `admin`
- **Sessions** : S1 et S2 (2024-2025)
- **Formateurs** : 3 formateurs avec spécialités
- **Cours** : 3 cours (Java, Spring Boot, BDD)
- **Étudiants** : 4 étudiants
- **Inscriptions** : 5 inscriptions
- **Séances** : 3 séances programmées
- **Notes** : 5 notes attribuées

## 🐛 Dépannage

### Problème de connexion à la base de données

Vérifiez les paramètres dans `application-prod.properties` et que MySQL est démarré.

### Erreur "Port already in use"

Changez le port dans `application.properties` :
```properties
server.port=8081
```

### Problème d'authentification

Vérifiez que l'utilisateur admin a été créé. Consultez les logs au démarrage.

## 📝 Licence

Ce projet est un projet académique.

## 👥 Auteur

Développé dans le cadre du Mini-projet Spring Boot IIT S1 2025-2026

---

**Note** : Cette application est en développement. Pour toute question, consultez la documentation ou les logs de l'application.

