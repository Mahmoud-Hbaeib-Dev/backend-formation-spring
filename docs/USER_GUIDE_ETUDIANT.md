# 🎓 Guide d'utilisation - Étudiant (ETUDIANT)

Guide pour les étudiants du Centre de Formation.

## 📋 Table des matières

- [Connexion](#connexion)
- [API REST](#api-rest)
- [Mes informations](#mes-informations)
- [Mes cours](#mes-cours)
- [Mon emploi du temps](#mon-emploi-du-temps)
- [Mes notes](#mes-notes)
- [Mes inscriptions](#mes-inscriptions)

## 🔐 Connexion

Les étudiants utilisent l'**API REST** avec authentification JWT.

### 1. Obtenir un token JWT

**Endpoint** : `POST /api/auth/login`

**Request** :
```json
{
  "login": "etudiant1",
  "password": "password123"
}
```

**Response** :
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "etudiant1",
  "roles": ["ETUDIANT"]
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

## 👤 Mes informations

### Voir mes informations

**Endpoint** : `GET /api/auth/me`

**Headers** :
```
Authorization: Bearer <token>
```

**Response** :
```json
{
  "id": 1,
  "login": "etudiant1",
  "roles": ["ETUDIANT"],
  "etudiant": {
    "id": 1,
    "matricule": "ETU001",
    "nom": "Martin",
    "prenom": "Jean",
    "email": "jean.martin@example.com",
    "dateInscription": "2024-09-01"
  }
}
```

## 📚 Mes cours

### Voir mes cours inscrits

**Endpoint** : `GET /api/etudiants/{id}/cours`

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
  "formateur": {
    "id": 1,
    "nom": "Dupont",
    "specialite": "Java",
    "email": "dupont@example.com"
  },
  "session": {
    "id": 1,
    "semestre": "S1",
    "anneeScolaire": "2024-2025"
  }
}
```

## 📅 Mon emploi du temps

### Voir mon emploi du temps

**Endpoint** : `GET /api/seances/etudiant/{etudiantId}`

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
    },
    "formateur": {
      "id": 1,
      "nom": "Dupont"
    }
  },
  {
    "id": 2,
    "date": "2024-09-15",
    "heure": "14:00",
    "salle": "Salle 102",
    "cours": {
      "code": "SPRING101",
      "titre": "Spring Boot"
    },
    "formateur": {
      "id": 2,
      "nom": "Durand"
    }
  }
]
```

### Emploi du temps par date

**Endpoint** : `GET /api/seances/etudiant/{etudiantId}/date?date=2024-09-15`

## 📊 Mes notes

### Voir toutes mes notes

**Endpoint** : `GET /api/notes/etudiant/{etudiantId}`

**Response** :
```json
[
  {
    "id": 1,
    "valeur": 15.5,
    "dateSaisie": "2024-10-01",
    "cours": {
      "code": "JAVA101",
      "titre": "Programmation Java"
    }
  },
  {
    "id": 2,
    "valeur": 16.0,
    "dateSaisie": "2024-10-05",
    "cours": {
      "code": "SPRING101",
      "titre": "Spring Boot"
    }
  }
]
```

### Voir mes notes pour un cours spécifique

**Endpoint** : `GET /api/notes/etudiant/{etudiantId}/cours/{code}`

**Response** :
```json
[
  {
    "id": 1,
    "valeur": 15.5,
    "dateSaisie": "2024-10-01"
  }
]
```

### Calculer ma moyenne

**Endpoint** : `GET /api/etudiants/{id}/moyenne`

**Response** :
```json
{
  "etudiantId": 1,
  "moyenneGenerale": 15.75,
  "nombreNotes": 2,
  "notes": [
    {
      "cours": "JAVA101",
      "valeur": 15.5
    },
    {
      "cours": "SPRING101",
      "valeur": 16.0
    }
  ]
}
```

## 📝 Mes inscriptions

### Voir mes inscriptions

**Endpoint** : `GET /api/inscriptions/etudiant/{etudiantId}`

**Response** :
```json
[
  {
    "id": 1,
    "dateInscription": "2024-09-01",
    "status": "ACTIVE",
    "cours": {
      "code": "JAVA101",
      "titre": "Programmation Java"
    }
  }
]
```

### S'inscrire à un cours

**Endpoint** : `POST /api/inscriptions`

**Request** :
```json
{
  "etudiantId": 1,
  "coursCode": "JAVA101"
}
```

**Response** : `201 Created`

> ⚠️ Vous ne pouvez pas vous inscrire deux fois au même cours !

### Se désinscrire d'un cours

**Endpoint** : `DELETE /api/inscriptions/{id}`

**Response** : `200 OK`

## 🔒 Autorisations

En tant qu'**ÉTUDIANT**, vous avez accès à :

✅ **Lecture** :
- Vos informations personnelles
- Vos cours inscrits
- Votre emploi du temps
- Vos notes
- Vos inscriptions

✅ **Écriture** :
- S'inscrire à un cours
- Se désinscrire d'un cours

❌ **Interdit** :
- Voir les informations des autres étudiants
- Voir les notes des autres étudiants
- Créer/modifier/supprimer des cours
- Créer/modifier des séances
- Attribuer des notes

## 📖 Documentation complète

Consultez la documentation Swagger pour plus de détails :

**URL** : `http://localhost:8080/swagger-ui.html`

## 💡 Conseils

1. **Consultez régulièrement votre emploi du temps** pour ne pas manquer de séances
2. **Vérifiez vos notes** pour suivre votre progression
3. **Inscrivez-vous aux cours** en début de session
4. **Contactez votre formateur** en cas de question sur un cours

## ❓ Aide et support

Pour toute question :
1. Consultez la documentation API
2. Contactez votre formateur
3. Contactez l'administrateur

---

**Version** : 1.0.0  
**Dernière mise à jour** : 2025

