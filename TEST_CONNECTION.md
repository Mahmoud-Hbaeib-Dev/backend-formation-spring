# 🔍 Guide de Test de Connexion Frontend-Backend

## ✅ Vérification de la Connexion

### 1. Vérifier que le Backend est Démarré

Ouvrez votre navigateur et allez à :
```
http://localhost:8080/actuator/health
```

Vous devriez voir :
```json
{"status":"UP"}
```

### 2. Vérifier l'État de la Base de Données

Ouvrez votre navigateur et allez à :
```
http://localhost:8080/api/diagnostic/status
```

Cela vous montrera :
- Le nombre total d'utilisateurs, étudiants, formateurs
- Les étudiants/formateurs sans User associé
- La liste de tous les Users avec leurs logins et rôles
- La liste des étudiants avec leurs Users associés

**Exemple de réponse attendue :**
```json
{
  "totalUsers": 5,
  "totalEtudiants": 4,
  "totalFormateurs": 3,
  "etudiantsSansUser": 0,
  "formateursSansUser": 0,
  "users": [
    {"id": "...", "login": "admin", "role": "ADMIN"},
    {"id": "...", "login": "mat001", "role": "ETUDIANT"},
    ...
  ],
  "etudiantsAvecUser": [
    {
      "id": "...",
      "matricule": "MAT001",
      "nom": "Ben Ali",
      "prenom": "Ahmed",
      "email": "ahmed@email.com",
      "hasUser": true,
      "userLogin": "mat001",
      "userRole": "ETUDIANT"
    },
    ...
  ]
}
```

### 3. Tester la Recherche d'Utilisateur par Email

Ouvrez votre navigateur et allez à :
```
http://localhost:8080/api/diagnostic/test-user?email=ahmed@email.com
```

Cela vous montrera :
- Si l'utilisateur est trouvé par login
- Si l'étudiant est trouvé par email
- Si l'étudiant a un User associé
- Les détails du User associé

**Exemple de réponse attendue :**
```json
{
  "email": "ahmed@email.com",
  "foundByLogin": false,
  "foundEtudiant": true,
  "etudiant": {
    "id": "...",
    "matricule": "MAT001",
    "nom": "Ben Ali",
    "prenom": "Ahmed",
    "email": "ahmed@email.com",
    "hasUser": true
  },
  "etudiantUser": {
    "id": "...",
    "login": "mat001",
    "role": "ETUDIANT"
  }
}
```

### 4. Vérifier les Logs du Backend

Quand vous essayez de vous connecter, regardez les logs du backend. Vous devriez voir :

```
🔐 [AUTH API] Tentative de connexion reçue
📧 Login/Email: ahmed@email.com
🔑 Password: ***
🔄 [AUTH API] Authentification en cours...
🔍 [USER DETAILS] Recherche de l'utilisateur avec: ahmed@email.com
🔍 [USER DETAILS] Tentative 1: Recherche par login...
❌ [USER DETAILS] Aucun utilisateur trouvé par login
🔍 [USER DETAILS] Tentative 2: Recherche par email (Etudiant)...
✅ [USER DETAILS] Étudiant trouvé par email: Ahmed Ben Ali
✅ [USER DETAILS] User associé trouvé: mat001
✅ [AUTH API] Authentification réussie
```

## ❌ Problèmes Possibles et Solutions

### Problème 1 : `etudiantsSansUser > 0`

**Symptôme** : L'endpoint `/api/diagnostic/status` montre des étudiants sans User.

**Solution** :
1. Redémarrer le backend pour que `DataMigration` s'exécute
2. Ou supprimer la base de données et redémarrer :
   ```bash
   cd backend
   rm -rf data/
   mvn spring-boot:run
   ```

### Problème 2 : `hasUser: false` pour un étudiant

**Symptôme** : L'endpoint `/api/diagnostic/test-user?email=ahmed@email.com` montre `hasUser: false`.

**Solution** :
1. Vérifier que `DataMigration` s'est exécuté (regarder les logs au démarrage)
2. Si pas, redémarrer le backend
3. Si oui, il y a peut-être un problème avec la création du User

### Problème 3 : Aucun log dans le Backend lors de la Connexion

**Symptôme** : Vous essayez de vous connecter mais aucun log n'apparaît dans le backend.

**Causes possibles** :
- Le backend n'est pas démarré
- Le frontend envoie la requête à la mauvaise URL
- Problème de CORS

**Solution** :
1. Vérifier que le backend est bien démarré sur le port 8080
2. Vérifier dans la console du navigateur (Network tab) que la requête est bien envoyée à `http://localhost:8080/api/auth/login`
3. Vérifier les logs du backend

### Problème 4 : "Bad credentials" mais l'utilisateur existe

**Symptôme** : L'utilisateur existe dans la base (visible via `/api/diagnostic/status`) mais la connexion échoue.

**Causes possibles** :
- Le mot de passe ne correspond pas
- Le User n'est pas correctement lié à l'Etudiant/Formateur
- Problème avec le PasswordEncoder

**Solution** :
1. Vérifier dans `/api/diagnostic/test-user?email=ahmed@email.com` que `hasUser: true`
2. Vérifier que le login du User correspond (devrait être `mat001` pour l'étudiant avec matricule `MAT001`)
3. Essayer de se connecter avec le login au lieu de l'email :
   - Login: `mat001`
   - Password: `mat001`

## 🧪 Test Complet

### Étape 1 : Vérifier l'État Initial
```bash
# Ouvrir dans le navigateur
http://localhost:8080/api/diagnostic/status
```

### Étape 2 : Tester la Recherche
```bash
# Ouvrir dans le navigateur
http://localhost:8080/api/diagnostic/test-user?email=ahmed@email.com
```

### Étape 3 : Essayer de Se Connecter
Dans le frontend, essayez de vous connecter avec :
- Email: `ahmed@email.com`
- Password: `mat001`

### Étape 4 : Vérifier les Logs
Regardez les logs du backend pour voir exactement ce qui se passe.

## 📋 Checklist

- [ ] Backend démarré sur le port 8080
- [ ] `/actuator/health` retourne `{"status":"UP"}`
- [ ] `/api/diagnostic/status` accessible
- [ ] `etudiantsSansUser: 0` dans la réponse
- [ ] `hasUser: true` pour l'étudiant testé
- [ ] Logs du backend montrent la recherche d'utilisateur
- [ ] Frontend envoie la requête à `http://localhost:8080/api/auth/login`

## 🔧 Commandes Utiles

### Redémarrer le Backend
```bash
cd backend
mvn spring-boot:run
```

### Supprimer la Base de Données et Redémarrer
```bash
cd backend
rm -rf data/
mvn spring-boot:run
```

### Vérifier les Logs du Backend
Regardez la console où vous avez lancé `mvn spring-boot:run`. Vous devriez voir tous les logs avec les emojis (🔍, ✅, ❌, etc.).

