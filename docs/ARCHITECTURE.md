# 🏗️ Architecture de l'application

Documentation technique de l'architecture du Centre de Formation.

## 📋 Table des matières

- [Vue d'ensemble](#vue-densemble)
- [Architecture dual](#architecture-dual)
- [Structure des packages](#structure-des-packages)
- [Flux de données](#flux-de-données)
- [Sécurité](#sécurité)
- [Base de données](#base-de-données)
- [API REST](#api-rest)
- [Interface Admin](#interface-admin)

## 🎯 Vue d'ensemble

L'application Centre de Formation est une application Spring Boot qui gère un centre de formation avec deux interfaces distinctes :

1. **API REST** : Pour les applications clientes (React, Angular, etc.)
2. **Interface Admin** : Interface web complète pour l'administration

### Diagramme d'architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Clients                                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │   React App  │  │ Angular App │  │  Mobile App  │     │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘     │
└─────────┼─────────────────┼─────────────────┼─────────────┘
          │                 │                 │
          └─────────────────┼─────────────────┘
                            │
          ┌─────────────────▼─────────────────┐
          │      Spring Boot Application       │
          │  ┌─────────────────────────────┐  │
          │  │   Security Layer (JWT/Session)│ │
          │  └─────────────────────────────┘  │
          │  ┌─────────────────────────────┐  │
          │  │   Controllers (API/Web)      │  │
          │  └──────────────┬──────────────┘  │
          │  ┌──────────────▼──────────────┐  │
          │  │      Services (Business)     │  │
          │  └──────────────┬──────────────┘  │
          │  ┌──────────────▼──────────────┐  │
          │  │    Repositories (JPA)       │  │
          │  └──────────────┬──────────────┘  │
          └──────────────────┼──────────────────┘
                            │
          ┌─────────────────▼─────────────────┐
          │         Database (H2/MySQL)         │
          └─────────────────────────────────────┘
```

## 🔀 Architecture dual

L'application utilise une architecture dual pour répondre à différents besoins :

### 1. API REST (`/api/**`)

**Objectif** : Fournir une API RESTful pour les applications clientes

**Caractéristiques** :
- Authentification JWT (stateless)
- Format JSON
- CORS configuré
- Documentation Swagger/OpenAPI

**Endpoints** :
- `/api/auth/**` : Authentification
- `/api/etudiants/**` : Gestion des étudiants
- `/api/formateurs/**` : Gestion des formateurs
- `/api/cours/**` : Gestion des cours
- `/api/inscriptions/**` : Gestion des inscriptions
- `/api/seances/**` : Gestion des séances
- `/api/notes/**` : Gestion des notes
- `/api/statistiques/**` : Statistiques

### 2. Interface Admin (`/admin/**`)

**Objectif** : Interface web complète pour l'administration

**Caractéristiques** :
- Authentification session (stateful)
- Rendu serveur avec Thymeleaf
- Interface responsive avec Bootstrap
- CRUD complet pour toutes les entités

**Pages principales** :
- `/admin/dashboard` : Tableau de bord
- `/admin/etudiants/**` : Gestion des étudiants
- `/admin/formateurs/**` : Gestion des formateurs
- `/admin/cours/**` : Gestion des cours
- `/admin/planning/**` : Planning et emploi du temps
- `/admin/statistiques/**` : Statistiques

## 📦 Structure des packages

```
com.formation.app/
├── entity/              # Entités JPA
│   ├── User.java
│   ├── Etudiant.java
│   ├── Formateur.java
│   ├── Cours.java
│   ├── Session.java
│   ├── Groupe.java
│   ├── Inscription.java
│   ├── Seance.java
│   └── Note.java
│
├── repository/          # Repositories Spring Data JPA
│   ├── UserRepository.java
│   ├── EtudiantRepository.java
│   ├── FormateurRepository.java
│   ├── CoursRepository.java
│   └── ...
│
├── service/             # Services métier
│   ├── UserService.java
│   ├── EtudiantService.java
│   ├── FormateurService.java
│   ├── CoursService.java
│   ├── InscriptionService.java
│   ├── SeanceService.java
│   ├── NoteService.java
│   ├── NotificationService.java
│   └── ReportService.java
│
├── controller/
│   ├── api/             # REST Controllers
│   │   ├── AuthRestController.java
│   │   ├── EtudiantRestController.java
│   │   ├── FormateurRestController.java
│   │   ├── CoursRestController.java
│   │   └── ...
│   │
│   └── web/             # Thymeleaf Controllers
│       ├── AuthWebController.java
│       ├── EtudiantWebController.java
│       ├── FormateurWebController.java
│       ├── CoursWebController.java
│       └── ...
│
├── config/              # Configuration
│   ├── SecurityConfig.java
│   ├── CorsConfig.java
│   ├── CacheConfig.java
│   ├── OpenApiConfig.java
│   └── DataInitializer.java
│
├── security/            # Spring Security
│   ├── JwtTokenService.java
│   ├── JwtAuthenticationFilter.java
│   ├── UserDetailsImpl.java
│   └── UserDetailsServiceImpl.java
│
├── exception/           # Gestion des exceptions
│   ├── ResourceNotFoundException.java
│   ├── BadRequestException.java
│   ├── ConflictException.java
│   └── GlobalExceptionHandler.java
│
└── util/                # Utilitaires
```

## 🔄 Flux de données

### Flux API REST

```
Client (React/Angular)
    ↓ HTTP Request (JSON)
Controller (REST)
    ↓ Validation
Service (Business Logic)
    ↓ Transaction
Repository (JPA)
    ↓ SQL
Database
    ↓
Repository → Service → Controller → Client (JSON Response)
```

### Flux Interface Admin

```
Browser
    ↓ HTTP Request
Controller (Web/Thymeleaf)
    ↓ Validation
Service (Business Logic)
    ↓ Transaction
Repository (JPA)
    ↓ SQL
Database
    ↓
Repository → Service → Controller → Thymeleaf Template → HTML Response
```

## 🔒 Sécurité

### Authentification

#### API REST (JWT)

1. **Login** : `POST /api/auth/login`
   - Client envoie `login` et `password`
   - Serveur valide et retourne un JWT token

2. **Requêtes authentifiées** :
   - Client inclut `Authorization: Bearer <token>`
   - `JwtAuthenticationFilter` valide le token
   - Si valide, requête autorisée

#### Interface Admin (Session)

1. **Login** : `POST /login`
   - Utilisateur envoie credentials via formulaire
   - Serveur crée une session HTTP
   - Cookie `JSESSIONID` envoyé au client

2. **Requêtes authentifiées** :
   - Client inclut le cookie de session
   - Spring Security valide la session
   - Si valide, requête autorisée

### Autorisation

**Rôles** :
- `ADMIN` : Accès complet
- `FORMATEUR` : Gestion de ses cours, séances, notes
- `ETUDIANT` : Consultation de ses informations, cours, notes

**Configuration** : `SecurityConfig.java`

## 🗄️ Base de données

### Modèle de données

**Entités principales** :
- `User` : Utilisateurs (login, password, roles)
- `Etudiant` : Étudiants (matricule, nom, prénom, email)
- `Formateur` : Formateurs (nom, spécialité, email)
- `Session` : Sessions pédagogiques (semestre, année)
- `Cours` : Cours (code, titre, description)
- `Groupe` : Groupes d'étudiants
- `Inscription` : Inscriptions étudiants-cours
- `Seance` : Séances de cours (date, heure, salle)
- `Note` : Notes des étudiants

### Relations

- `User` ↔ `Etudiant` : OneToOne
- `Formateur` ↔ `Cours` : OneToMany
- `Session` ↔ `Cours` : OneToMany
- `Cours` ↔ `Groupe` : ManyToMany (via `CoursGroupe`)
- `Etudiant` ↔ `Cours` : ManyToMany (via `Inscription`)
- `Cours` ↔ `Seance` : OneToMany
- `Formateur` ↔ `Seance` : OneToMany
- `Etudiant` ↔ `Note` : OneToMany
- `Cours` ↔ `Note` : OneToMany

### Profils de base de données

- **Dev** : H2 (fichier) - `application-dev.properties`
- **Prod** : MySQL - `application-prod.properties`
- **Test** : H2 (mémoire) - `application-test.properties`

## 📡 API REST

### Structure des réponses

**Succès** :
```json
{
  "id": 1,
  "nom": "Martin",
  "prenom": "Jean"
}
```

**Erreur** :
```json
{
  "error": "Resource not found",
  "message": "Étudiant avec ID 999 non trouvé",
  "timestamp": "2024-01-20T10:30:00"
}
```

### Codes HTTP

- `200 OK` : Succès
- `201 Created` : Ressource créée
- `400 Bad Request` : Requête invalide
- `401 Unauthorized` : Non authentifié
- `403 Forbidden` : Non autorisé
- `404 Not Found` : Ressource non trouvée
- `409 Conflict` : Conflit (doublon, etc.)
- `500 Internal Server Error` : Erreur serveur

## 🖥️ Interface Admin

### Technologies

- **Thymeleaf** : Templates serveur
- **Bootstrap** : Framework CSS
- **JavaScript** : Interactivité

### Structure des templates

```
templates/
├── fragments/
│   ├── header.html
│   ├── footer.html
│   └── layout.html
├── login.html
└── admin/
    ├── dashboard.html
    ├── etudiants/
    │   ├── list.html
    │   ├── form.html
    │   └── details.html
    ├── formateurs/
    ├── cours/
    ├── inscriptions/
    ├── seances/
    ├── notes/
    ├── sessions/
    ├── groupes/
    ├── planning/
    └── statistiques/
```

## 🚀 Performance

### Optimisations

1. **Cache** : Spring Cache avec Caffeine
   - Cache des cours fréquemment consultés
   - TTL : 10 minutes

2. **Requêtes JPA** :
   - Utilisation de `@EntityGraph` pour éviter N+1
   - Pagination pour les grandes listes

3. **Batch Processing** :
   - HikariCP pour le pool de connexions
   - JPA batch inserts/updates

## 📊 Monitoring

### Actuator

Endpoints disponibles :
- `/actuator/health` : Santé de l'application
- `/actuator/info` : Informations sur l'application
- `/actuator/metrics` : Métriques

### Logging

- **Logback** : Configuration dans `logback-spring.xml`
- **Niveaux** : DEBUG (dev), INFO (prod)
- **Fichiers** : Rotation automatique

---

**Version** : 1.0.0  
**Dernière mise à jour** : 2025

