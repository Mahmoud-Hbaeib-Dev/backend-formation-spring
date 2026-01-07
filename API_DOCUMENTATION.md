# 📡 Documentation API REST

Documentation complète de l'API REST du Centre de Formation.

## 🔐 Authentification

L'API utilise JWT (JSON Web Tokens) pour l'authentification.

### Login

**Endpoint** : `POST /api/auth/login`

**Request** :
```json
{
  "login": "admin",
  "password": "admin"
}
```

**Response** (200 OK) :
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "admin",
  "roles": ["ADMIN"]
}
```

**Response** (401 Unauthorized) :
```json
{
  "error": "Invalid credentials"
}
```

### Utilisation du token

Inclure le token dans le header `Authorization` :

```
Authorization: Bearer <token>
```

## 👥 Étudiants

### Liste des étudiants

**Endpoint** : `GET /api/etudiants`

**Headers** :
```
Authorization: Bearer <token>
```

**Response** (200 OK) :
```json
[
  {
    "id": "ETU001",
    "matricule": "MAT001",
    "nom": "Ben Ali",
    "prenom": "Ahmed",
    "email": "ahmed@email.com",
    "dateInscription": "2024-01-15"
  }
]
```

**Autorisation** : ADMIN, FORMATEUR

### Détails d'un étudiant

**Endpoint** : `GET /api/etudiants/{id}`

**Response** (200 OK) :
```json
{
  "id": "ETU001",
  "matricule": "MAT001",
  "nom": "Ben Ali",
  "prenom": "Ahmed",
  "email": "ahmed@email.com",
  "dateInscription": "2024-01-15"
}
```

**Autorisation** : ADMIN, FORMATEUR, ETUDIANT (son propre profil)

### Créer un étudiant

**Endpoint** : `POST /api/etudiants`

**Request** :
```json
{
  "matricule": "MAT005",
  "nom": "Nouveau",
  "prenom": "Étudiant",
  "email": "nouveau@email.com"
}
```

**Response** (201 Created) :
```json
{
  "id": "ETU005",
  "matricule": "MAT005",
  "nom": "Nouveau",
  "prenom": "Étudiant",
  "email": "nouveau@email.com",
  "dateInscription": "2024-01-20"
}
```

**Autorisation** : ADMIN

### Modifier un étudiant

**Endpoint** : `PUT /api/etudiants/{id}`

**Request** :
```json
{
  "nom": "Modifié",
  "email": "modifie@email.com"
}
```

**Autorisation** : ADMIN

### Supprimer un étudiant

**Endpoint** : `DELETE /api/etudiants/{id}`

**Response** (204 No Content)

**Autorisation** : ADMIN

## 📚 Cours

### Liste des cours

**Endpoint** : `GET /api/cours`

**Response** (200 OK) :
```json
[
  {
    "code": "JAVA001",
    "titre": "Java Fondamentaux",
    "description": "Introduction à la programmation Java",
    "formateur": {
      "id": "FORM001",
      "nom": "Dupont"
    },
    "session": {
      "id": "SESS001",
      "semestre": "S1"
    }
  }
]
```

**Autorisation** : Tous les rôles authentifiés

### Détails d'un cours

**Endpoint** : `GET /api/cours/{code}`

**Autorisation** : Tous les rôles authentifiés

### Créer un cours

**Endpoint** : `POST /api/cours`

**Request** :
```json
{
  "code": "NEW001",
  "titre": "Nouveau Cours",
  "description": "Description du cours",
  "formateurId": "FORM001",
  "sessionId": "SESS001"
}
```

**Autorisation** : ADMIN, FORMATEUR

## 📝 Inscriptions

### Liste des inscriptions

**Endpoint** : `GET /api/inscriptions`

**Autorisation** : ADMIN, FORMATEUR

### Inscrire un étudiant

**Endpoint** : `POST /api/inscriptions`

**Request** :
```json
{
  "etudiantId": "ETU001",
  "coursCode": "JAVA001"
}
```

**Response** (201 Created) :
```json
{
  "id": "INS006",
  "dateInscription": "2024-01-20",
  "status": "ACTIVE",
  "etudiant": {
    "id": "ETU001",
    "nom": "Ben Ali"
  },
  "cours": {
    "code": "JAVA001",
    "titre": "Java Fondamentaux"
  }
}
```

**Autorisation** : ADMIN, FORMATEUR

### Désinscrire un étudiant

**Endpoint** : `DELETE /api/inscriptions/{id}`

**Autorisation** : ADMIN, FORMATEUR

## 📊 Notes

### Liste des notes

**Endpoint** : `GET /api/notes`

**Autorisation** : ADMIN, FORMATEUR

### Attribuer une note

**Endpoint** : `POST /api/notes`

**Request** :
```json
{
  "etudiantId": "ETU001",
  "coursCode": "JAVA001",
  "valeur": 15.5
}
```

**Autorisation** : ADMIN, FORMATEUR

### Notes d'un étudiant

**Endpoint** : `GET /api/notes/etudiant/{etudiantId}`

**Autorisation** : ADMIN, FORMATEUR, ETUDIANT (ses propres notes)

### Notes d'un cours

**Endpoint** : `GET /api/notes/cours/{coursCode}`

**Autorisation** : ADMIN, FORMATEUR

## 📅 Séances

### Liste des séances

**Endpoint** : `GET /api/seances`

**Autorisation** : Tous les rôles authentifiés

### Créer une séance

**Endpoint** : `POST /api/seances`

**Request** :
```json
{
  "date": "2024-01-25",
  "heure": "09:00",
  "salle": "Salle A",
  "coursCode": "JAVA001",
  "formateurId": "FORM001"
}
```

**Autorisation** : ADMIN, FORMATEUR

### Emploi du temps étudiant

**Endpoint** : `GET /api/seances/etudiant/{etudiantId}`

**Autorisation** : ADMIN, FORMATEUR, ETUDIANT (son propre emploi du temps)

## 📈 Statistiques

### Dashboard statistiques

**Endpoint** : `GET /api/statistiques/dashboard`

**Response** :
```json
{
  "nombreTotalCours": 10,
  "nombreTotalEtudiants": 50,
  "nombreTotalFormateurs": 5
}
```

**Autorisation** : ADMIN

### Statistiques d'un cours

**Endpoint** : `GET /api/statistiques/cours/{coursCode}`

**Response** :
```json
{
  "coursCode": "JAVA001",
  "moyenne": 14.5,
  "tauxReussite": 75.0,
  "nombreInscriptions": 20,
  "nombreEtudiantsNotes": 15
}
```

**Autorisation** : ADMIN, FORMATEUR

## ⚠️ Codes d'erreur

- **200 OK** : Succès
- **201 Created** : Ressource créée
- **204 No Content** : Succès sans contenu
- **400 Bad Request** : Requête invalide
- **401 Unauthorized** : Non authentifié
- **403 Forbidden** : Non autorisé
- **404 Not Found** : Ressource non trouvée
- **409 Conflict** : Conflit (ex: doublon)
- **500 Internal Server Error** : Erreur serveur

## 📝 Exemples avec cURL

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"admin","password":"admin"}'
```

### Liste des étudiants

```bash
curl -X GET http://localhost:8080/api/etudiants \
  -H "Authorization: Bearer <token>"
```

### Créer un étudiant

```bash
curl -X POST http://localhost:8080/api/etudiants \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "matricule": "MAT005",
    "nom": "Nouveau",
    "prenom": "Étudiant",
    "email": "nouveau@email.com"
  }'
```

## 🔒 Autorisations par rôle

| Endpoint | ADMIN | FORMATEUR | ETUDIANT |
|----------|-------|-----------|----------|
| GET /api/etudiants | ✅ | ✅ | ❌ |
| POST /api/etudiants | ✅ | ❌ | ❌ |
| GET /api/etudiants/{id} | ✅ | ✅ | ✅* |
| GET /api/cours | ✅ | ✅ | ✅ |
| POST /api/cours | ✅ | ✅ | ❌ |
| POST /api/inscriptions | ✅ | ✅ | ❌ |
| POST /api/notes | ✅ | ✅ | ❌ |
| GET /api/notes/etudiant/{id} | ✅ | ✅ | ✅* |

*Seulement pour ses propres données

---

**Note** : Cette documentation est générée pour la version actuelle de l'API. Certains endpoints peuvent varier selon l'implémentation.

