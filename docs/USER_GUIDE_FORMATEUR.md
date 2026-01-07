# 👨‍🏫 Guide d'utilisation - Formateur (FORMATEUR)

Guide pour les formateurs du Centre de Formation.

## 📋 Table des matières

- [Connexion](#connexion)
- [API REST](#api-rest)
- [Endpoints disponibles](#endpoints-disponibles)
- [Gestion des cours](#gestion-des-cours)
- [Gestion des séances](#gestion-des-séances)
- [Gestion des notes](#gestion-des-notes)
- [Consultation des étudiants](#consultation-des-étudiants)
- [Statistiques](#statistiques)

## 🔐 Connexion

Les formateurs utilisent l'**API REST** avec authentification JWT.

### 1. Obtenir un token JWT

**Endpoint** : `POST /api/auth/login`

**Request** :
```json
{
  "login": "formateur1",
  "password": "password123"
}
```

**Response** :
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "formateur1",
  "roles": ["FORMATEUR"]
}
```

### 2. Utiliser le token

Inclure le token dans le header `Authorization` :

```
Authorization: Bearer <token>
```

## 📡 API REST

### Base URL

```
http://localhost:8080/api
```

### Format des réponses

Toutes les réponses sont au format JSON.

### Codes de réponse

- `200 OK` : Succès
- `201 Created` : Ressource créée
- `400 Bad Request` : Requête invalide
- `401 Unauthorized` : Non authentifié
- `403 Forbidden` : Non autorisé
- `404 Not Found` : Ressource non trouvée
- `500 Internal Server Error` : Erreur serveur

## 📚 Gestion des cours

### Voir mes cours

**Endpoint** : `GET /api/cours/formateur/{formateurId}`

**Headers** :
```
Authorization: Bearer <token>
```

**Response** :
```json
[
  {
    "code": "JAVA101",
    "titre": "Programmation Java",
    "description": "Introduction à Java",
    "formateur": {
      "id": 1,
      "nom": "Dupont",
      "specialite": "Java"
    },
    "session": {
      "id": 1,
      "semestre": "S1",
      "anneeScolaire": "2024-2025"
    }
  }
]
```

### Voir les détails d'un cours

**Endpoint** : `GET /api/cours/{code}`

**Response** :
```json
{
  "code": "JAVA101",
  "titre": "Programmation Java",
  "description": "Introduction à Java",
  "formateur": {...},
  "session": {...},
  "inscriptions": [...],
  "groupes": [...]
}
```

### Voir les étudiants inscrits à un cours

**Endpoint** : `GET /api/cours/{code}/inscriptions`

**Response** :
```json
[
  {
    "id": 1,
    "etudiant": {
      "id": 1,
      "matricule": "ETU001",
      "nom": "Martin",
      "prenom": "Jean"
    },
    "dateInscription": "2024-09-01",
    "status": "ACTIVE"
  }
]
```

## 📅 Gestion des séances

### Voir mes séances

**Endpoint** : `GET /api/seances/formateur/{formateurId}`

**Response** :
```json
[
  {
    "id": 1,
    "date": "2024-09-15",
    "heure": "09:00",
    "salle": "Salle 101",
    "cours": {
      "code": "JAVA101",
      "titre": "Programmation Java"
    }
  }
]
```

### Créer une séance

**Endpoint** : `POST /api/seances`

**Request** :
```json
{
  "coursCode": "JAVA101",
  "formateurId": 1,
  "date": "2024-09-20",
  "heure": "14:00",
  "salle": "Salle 102"
}
```

**Response** : `201 Created`

> ⚠️ Le système vérifie automatiquement les conflits d'horaires !

### Modifier une séance

**Endpoint** : `PUT /api/seances/{id}`

**Request** :
```json
{
  "date": "2024-09-21",
  "heure": "15:00",
  "salle": "Salle 103"
}
```

### Supprimer une séance

**Endpoint** : `DELETE /api/seances/{id}`

## 📊 Gestion des notes

### Voir les notes de mes cours

**Endpoint** : `GET /api/notes/cours/{code}`

**Response** :
```json
[
  {
    "id": 1,
    "valeur": 15.5,
    "dateSaisie": "2024-10-01",
    "etudiant": {
      "id": 1,
      "matricule": "ETU001",
      "nom": "Martin",
      "prenom": "Jean"
    },
    "cours": {
      "code": "JAVA101",
      "titre": "Programmation Java"
    }
  }
]
```

### Attribuer une note

**Endpoint** : `POST /api/notes`

**Request** :
```json
{
  "etudiantId": 1,
  "coursCode": "JAVA101",
  "valeur": 16.5
}
```

**Response** : `201 Created`

> ⚠️ La note doit être entre 0 et 20 !

### Modifier une note

**Endpoint** : `PUT /api/notes/{id}`

**Request** :
```json
{
  "valeur": 17.0
}
```

## 👥 Consultation des étudiants

### Voir la liste des étudiants

**Endpoint** : `GET /api/etudiants`

**Response** :
```json
[
  {
    "id": 1,
    "matricule": "ETU001",
    "nom": "Martin",
    "prenom": "Jean",
    "email": "jean.martin@example.com",
    "dateInscription": "2024-09-01"
  }
]
```

### Voir les détails d'un étudiant

**Endpoint** : `GET /api/etudiants/{id}`

### Rechercher un étudiant

**Endpoint** : `GET /api/etudiants/search?nom=Martin`

## 📈 Statistiques

### Statistiques de mes cours

**Endpoint** : `GET /api/statistiques/cours/{code}`

**Response** :
```json
{
  "cours": {
    "code": "JAVA101",
    "titre": "Programmation Java"
  },
  "nombreInscriptions": 25,
  "moyenneNotes": 14.5,
  "repartitionNotes": {
    "0-10": 2,
    "10-15": 10,
    "15-20": 13
  }
}
```

## 🔒 Autorisations

En tant que **FORMATEUR**, vous avez accès à :

✅ **Lecture** :
- Liste des étudiants
- Détails des étudiants
- Vos cours assignés
- Vos séances
- Notes de vos cours

✅ **Écriture** :
- Créer des séances pour vos cours
- Modifier vos séances
- Attribuer des notes à vos étudiants
- Modifier les notes de vos cours

❌ **Interdit** :
- Créer/modifier/supprimer des cours
- Créer/modifier/supprimer des étudiants
- Créer/modifier/supprimer des formateurs
- Accéder aux statistiques globales

## 📖 Documentation complète

Consultez la documentation Swagger pour plus de détails :

**URL** : `http://localhost:8080/swagger-ui.html`

## ❓ Aide et support

Pour toute question :
1. Consultez la documentation API
2. Vérifiez les exemples de requêtes
3. Contactez l'administrateur

---

**Version** : 1.0.0  
**Dernière mise à jour** : 2025

