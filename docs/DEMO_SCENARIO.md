# 🎬 Scénario de démonstration

Scénario complet pour la démonstration de l'application Centre de Formation.

## 📋 Table des matières

- [Préparation](#préparation)
- [Scénario 1 : Interface Admin](#scénario-1--interface-admin)
- [Scénario 2 : API REST](#scénario-2--api-rest)
- [Scénario 3 : Fonctionnalités avancées](#scénario-3--fonctionnalités-avancées)
- [Points clés à mettre en avant](#points-clés-à-mettre-en-avant)

## 🚀 Préparation

### 1. Démarrer l'application

```bash
cd backend
mvn spring-boot:run
```

### 2. Vérifier le démarrage

- ✅ Application accessible sur `http://localhost:8080`
- ✅ Base de données initialisée avec des données de test
- ✅ Utilisateur admin créé : `admin` / `admin`

### 3. Données de test disponibles

- **1 utilisateur admin** : `admin` / `admin`
- **2 sessions** : S1 et S2 (2024-2025)
- **3 formateurs** : Dupont (Java), Martin (Spring Boot), Durand (BDD)
- **3 cours** : JAVA101, SPRING101, BDD101
- **4 étudiants** : ETU001 à ETU004
- **5 inscriptions** : Étudiants inscrits aux cours
- **3 séances** : Séances programmées
- **5 notes** : Notes attribuées

## 🎯 Scénario 1 : Interface Admin

### 1.1 Connexion Admin

1. **Accéder à la page de login**
   - URL : `http://localhost:8080/login`
   - Afficher le formulaire de connexion

2. **Se connecter**
   - Login : `admin`
   - Password : `admin`
   - Cliquer sur "Se connecter"
   - ✅ Redirection vers `/admin/dashboard`

### 1.2 Dashboard

**Afficher le dashboard** :
- Statistiques globales :
  - Nombre d'étudiants : 4
  - Nombre de formateurs : 3
  - Nombre de cours : 3
  - Inscriptions actives : 5
- Statistiques par cours :
  - Liste des cours avec nombre d'inscriptions

**Points à mentionner** :
- ✅ Interface responsive
- ✅ Statistiques en temps réel
- ✅ Navigation intuitive

### 1.3 Gestion des étudiants

1. **Voir la liste des étudiants**
   - Menu : "Étudiants" → "Liste"
   - Afficher les 4 étudiants

2. **Créer un nouvel étudiant**
   - Cliquer sur "Nouvel étudiant"
   - Remplir le formulaire :
     - Matricule : `ETU005`
     - Nom : `Bernard`
     - Prénom : `Sophie`
     - Email : `sophie.bernard@example.com`
     - Date d'inscription : Aujourd'hui
   - Cliquer sur "Enregistrer"
   - ✅ Étudiant créé et affiché dans la liste

3. **Voir les détails d'un étudiant**
   - Cliquer sur "Détails" pour ETU001
   - Afficher :
     - Informations personnelles
     - Liste des inscriptions
     - Notes obtenues

### 1.4 Gestion des cours

1. **Voir la liste des cours**
   - Menu : "Cours" → "Liste"
   - Afficher les 3 cours

2. **Créer un nouveau cours**
   - Cliquer sur "Nouveau cours"
   - Remplir le formulaire :
     - Code : `ANGULAR101`
     - Titre : `Angular Framework`
     - Description : `Introduction au framework Angular`
     - Formateur : Sélectionner "Martin"
     - Session : Sélectionner "S1 2024-2025"
     - Groupes : Sélectionner "Groupe A"
   - Cliquer sur "Enregistrer"
   - ✅ Cours créé

### 1.5 Gestion des inscriptions

1. **Inscrire un étudiant à un cours**
   - Menu : "Inscriptions" → "Nouvelle inscription"
   - Sélectionner :
     - Étudiant : ETU005 (Sophie Bernard)
     - Cours : ANGULAR101
   - Cliquer sur "Enregistrer"
   - ✅ Inscription créée

### 1.6 Gestion des séances

1. **Créer une séance**
   - Menu : "Séances" → "Nouvelle séance"
   - Remplir :
     - Cours : ANGULAR101
     - Formateur : Martin
     - Date : Demain
     - Heure : `14:00`
     - Salle : `Salle 201`
   - Cliquer sur "Enregistrer"
   - ✅ Séance créée

2. **Vérifier les conflits**
   - Essayer de créer une séance avec le même formateur à la même heure
   - ✅ Le système détecte le conflit et affiche une erreur

### 1.7 Gestion des notes

1. **Attribuer une note**
   - Menu : "Notes" → "Nouvelle note"
   - Sélectionner :
     - Étudiant : ETU005
     - Cours : ANGULAR101
     - Valeur : `16.5`
   - Cliquer sur "Enregistrer"
   - ✅ Note attribuée

### 1.8 Planning

1. **Voir le planning global**
   - Menu : "Planning" → "Vue globale"
   - Afficher toutes les séances programmées

2. **Planning par étudiant**
   - Menu : "Planning" → "Par étudiant"
   - Sélectionner ETU001
   - ✅ Afficher l'emploi du temps de l'étudiant

3. **Planning par formateur**
   - Menu : "Planning" → "Par formateur"
   - Sélectionner "Dupont"
   - ✅ Afficher l'emploi du temps du formateur

### 1.9 Statistiques

1. **Dashboard statistiques**
   - Menu : "Statistiques" → "Dashboard"
   - Afficher :
     - Statistiques globales
     - Répartition des notes
     - Taux d'inscription par cours

2. **Statistiques par cours**
   - Menu : "Statistiques" → "Par cours"
   - Sélectionner JAVA101
   - ✅ Afficher les statistiques détaillées

## 📡 Scénario 2 : API REST

### 2.1 Accéder à Swagger

1. **Ouvrir Swagger UI**
   - URL : `http://localhost:8080/swagger-ui.html`
   - ✅ Afficher la documentation interactive

2. **Explorer les endpoints**
   - Parcourir les différents groupes d'endpoints
   - Expliquer la structure de l'API

### 2.2 Authentification JWT

1. **Obtenir un token**
   - Endpoint : `POST /api/auth/login`
   - Body :
     ```json
     {
       "login": "admin",
       "password": "admin"
     }
     ```
   - ✅ Récupérer le token JWT

2. **Utiliser le token**
   - Cliquer sur "Authorize" en haut à droite
   - Entrer : `Bearer <token>`
   - ✅ Token configuré pour toutes les requêtes

### 2.3 Consulter les étudiants

1. **Liste des étudiants**
   - Endpoint : `GET /api/etudiants`
   - ✅ Afficher la liste JSON

2. **Détails d'un étudiant**
   - Endpoint : `GET /api/etudiants/{id}`
   - ✅ Afficher les détails complets

3. **Rechercher un étudiant**
   - Endpoint : `GET /api/etudiants/search?nom=Martin`
   - ✅ Afficher les résultats de recherche

### 2.4 Gérer les cours

1. **Liste des cours**
   - Endpoint : `GET /api/cours`
   - ✅ Afficher tous les cours

2. **Créer un cours**
   - Endpoint : `POST /api/cours`
   - Body :
     ```json
     {
       "code": "REACT101",
       "titre": "React Framework",
       "description": "Introduction à React",
       "formateurId": 1,
       "sessionId": 1
     }
     ```
   - ✅ Cours créé

### 2.5 Gérer les inscriptions

1. **Inscrire un étudiant**
   - Endpoint : `POST /api/inscriptions`
   - Body :
     ```json
     {
       "etudiantId": 1,
       "coursCode": "REACT101"
     }
     ```
   - ✅ Inscription créée

### 2.6 Gérer les notes

1. **Attribuer une note**
   - Endpoint : `POST /api/notes`
   - Body :
     ```json
     {
       "etudiantId": 1,
       "coursCode": "REACT101",
       "valeur": 15.5
     }
     ```
   - ✅ Note attribuée

2. **Voir les notes d'un étudiant**
   - Endpoint : `GET /api/notes/etudiant/{id}`
   - ✅ Afficher toutes les notes

## 🎨 Scénario 3 : Fonctionnalités avancées

### 3.1 Sécurité

1. **Tester l'authentification**
   - Essayer d'accéder à `/api/etudiants` sans token
   - ✅ Erreur 401 Unauthorized

2. **Tester l'autorisation**
   - Se connecter en tant qu'étudiant
   - Essayer d'accéder à `/admin/dashboard`
   - ✅ Redirection vers login (accès refusé)

### 3.2 Validation

1. **Tester la validation**
   - Créer un étudiant sans matricule
   - ✅ Erreur de validation

2. **Tester les contraintes**
   - Créer un étudiant avec un matricule existant
   - ✅ Erreur de conflit

### 3.3 Gestion des erreurs

1. **Ressource non trouvée**
   - Requête : `GET /api/etudiants/999`
   - ✅ Erreur 404 avec message clair

2. **Requête invalide**
   - Créer une note avec valeur > 20
   - ✅ Erreur 400 avec message de validation

### 3.4 Performance

1. **Cache**
   - Consulter plusieurs fois la liste des cours
   - ✅ Les requêtes suivantes sont plus rapides (cache)

2. **Optimisation des requêtes**
   - Voir les logs SQL
   - ✅ Pas de requêtes N+1

## ✨ Points clés à mettre en avant

### Architecture

- ✅ **Architecture dual** : API REST + Interface Admin
- ✅ **Séparation des responsabilités** : Controller → Service → Repository
- ✅ **Sécurité** : JWT pour API, Session pour Admin

### Fonctionnalités

- ✅ **CRUD complet** pour toutes les entités
- ✅ **Validation** des données
- ✅ **Gestion des erreurs** centralisée
- ✅ **Recherche et filtres**
- ✅ **Statistiques** et rapports

### Qualité du code

- ✅ **Tests** unitaires et d'intégration
- ✅ **Documentation** complète (Swagger, guides)
- ✅ **Standards** de code respectés
- ✅ **Performance** optimisée (cache, requêtes)

### Technologies

- ✅ **Spring Boot 3.2.0** : Framework moderne
- ✅ **Spring Security** : Sécurité robuste
- ✅ **JPA/Hibernate** : ORM performant
- ✅ **Thymeleaf** : Templates serveur
- ✅ **Swagger/OpenAPI** : Documentation API

## 📝 Checklist avant la démo

- [ ] Application démarrée et accessible
- [ ] Données de test chargées
- [ ] Swagger accessible
- [ ] Tous les scénarios testés
- [ ] Aucune erreur dans les logs
- [ ] Documentation à portée de main
- [ ] Captures d'écran préparées (si nécessaire)

## 🎤 Script de présentation

1. **Introduction** (2 min)
   - Présenter le projet
   - Expliquer le contexte

2. **Architecture** (3 min)
   - Architecture dual
   - Technologies utilisées
   - Structure du projet

3. **Démonstration Interface Admin** (5 min)
   - Connexion
   - Dashboard
   - CRUD étudiants
   - CRUD cours
   - Planning et statistiques

4. **Démonstration API REST** (5 min)
   - Swagger UI
   - Authentification JWT
   - Endpoints principaux
   - Tests en direct

5. **Fonctionnalités avancées** (3 min)
   - Sécurité
   - Validation
   - Gestion des erreurs
   - Performance

6. **Conclusion** (2 min)
   - Résumé des fonctionnalités
   - Points forts
   - Questions

**Durée totale** : ~20 minutes

---

**Version** : 1.0.0  
**Dernière mise à jour** : 2025

