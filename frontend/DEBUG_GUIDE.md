# 🐛 Guide de Débogage - Problèmes de Connexion

Ce guide vous aide à diagnostiquer les problèmes de connexion.

## 📋 Checklist de Vérification

### 1. Vérifier que le Backend est démarré

```bash
# Dans le terminal backend
cd backend
mvn spring-boot:run
```

**Vérifier** :
- Le serveur démarre sur `http://localhost:8080`
- Pas d'erreurs dans les logs
- Le message "Started CentreFormationApplication" apparaît

### 2. Vérifier que le Frontend est démarré

```bash
# Dans le terminal frontend
cd frontend
npm run dev
```

**Vérifier** :
- Le serveur démarre sur `http://localhost:3000`
- Pas d'erreurs dans la console

### 3. Vérifier la Configuration API

Ouvrez la console du navigateur (F12) et vérifiez :

```javascript
// Dans la console
console.log('API URL:', import.meta.env.VITE_API_URL || 'http://localhost:8080/api');
```

**Doit afficher** : `http://localhost:8080/api`

### 4. Vérifier les Credentials

#### Pour un Étudiant :
- **Email** : `ahmed@email.com` OU **Login** : `mat001`
- **Password** : `mat001`

#### Pour un Formateur :
- **Email** : `dupont@formation.com` OU **Login** : `dupont`
- **Password** : `dupont`

## 🔍 Logs Console à Surveiller

Lorsque vous essayez de vous connecter, vous devriez voir dans la console :

### ✅ Succès
```
🚀 [LOGIN PAGE] Soumission du formulaire
📝 Données: { login: "ahmed@email.com", password: "***" }
🔄 [LOGIN PAGE] Appel de authLogin...
🔐 [AUTH CONTEXT] Début de la connexion
🔄 [AUTH CONTEXT] Appel de authService.login...
🔐 [AUTH SERVICE] Tentative de connexion...
📧 Login/Email: ahmed@email.com
🔑 Password: ***
🌐 URL API: http://localhost:8080/api
📤 [API] Requête: POST /auth/login
📦 [API] Données: { login: "ahmed@email.com", password: "mat001" }
✅ [API] Réponse reçue: 200 /auth/login
📦 [API] Données de réponse: { token: "...", username: "...", roles: [...] }
✅ [AUTH SERVICE] Connexion réussie!
✅ [AUTH CONTEXT] Réponse reçue: { ... }
💾 [AUTH CONTEXT] Sauvegarde du token et des données utilisateur
✅ [AUTH CONTEXT] Connexion réussie et données sauvegardées
✅ [LOGIN PAGE] Connexion réussie!
👤 Rôle détecté: ETUDIANT
➡️ Redirection vers /etudiant/dashboard
```

### ❌ Erreurs Communes

#### Erreur 1 : Backend non démarré
```
❌ [API] Erreur de réponse:
🌐 Erreur réseau - Le serveur ne répond pas
💡 Vérifiez que le backend est démarré sur http://localhost:8080
```

**Solution** : Démarrer le backend

#### Erreur 2 : CORS
```
❌ [API] Erreur de réponse:
📊 Status: undefined
Access to XMLHttpRequest at 'http://localhost:8080/api/auth/login' from origin 'http://localhost:3000' has been blocked by CORS policy
```

**Solution** : Vérifier que `CorsConfig.java` est bien configuré dans le backend

#### Erreur 3 : Credentials incorrects
```
❌ [API] Erreur de réponse:
📊 Status: 401
💬 Message: Invalid credentials
```

**Solution** : Vérifier les credentials (email/login et password)

#### Erreur 4 : Utilisateur non trouvé
```
❌ [API] Erreur de réponse:
📊 Status: 401
💬 Message: Utilisateur non trouvé avec: ahmed@email.com
```

**Solution** : 
1. Vérifier que l'étudiant/formateur existe dans la base de données
2. Vérifier qu'un User est associé à l'étudiant/formateur
3. Vérifier que l'email/login est correct

#### Erreur 5 : URL API incorrecte
```
❌ [API] Erreur de réponse:
🔗 URL: /auth/login
🌐 Base URL: undefined
```

**Solution** : Créer un fichier `.env` dans `frontend/` avec :
```
VITE_API_URL=http://localhost:8080/api
```

## 🧪 Tests Manuels

### Test 1 : Vérifier que le Backend répond

Ouvrez dans le navigateur :
```
http://localhost:8080/actuator/health
```

**Doit afficher** : `{"status":"UP"}`

### Test 2 : Tester l'API directement

Dans la console du navigateur :
```javascript
fetch('http://localhost:8080/api/auth/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    login: 'mat001',
    password: 'mat001'
  })
})
.then(r => r.json())
.then(console.log)
.catch(console.error);
```

**Doit retourner** : Un objet avec `token`, `username`, `roles`

### Test 3 : Vérifier les données dans la base

Si vous utilisez H2, accédez à :
```
http://localhost:8080/h2-console
```

Vérifiez les tables :
- `USERS` : Doit contenir les utilisateurs avec leurs logins
- `ETUDIANTS` : Doit contenir les étudiants avec leurs emails
- `FORMATEURS` : Doit contenir les formateurs avec leurs emails

## 🔧 Solutions aux Problèmes Courants

### Problème : "Utilisateur non trouvé"

**Causes possibles** :
1. L'étudiant/formateur n'a pas de User associé
2. L'email/login est incorrect
3. La base de données n'a pas été initialisée

**Solution** :
1. Redémarrer le backend (les données de test seront créées)
2. Vérifier dans H2 console que les Users existent
3. Vérifier que les emails correspondent

### Problème : "Invalid credentials"

**Causes possibles** :
1. Le mot de passe est incorrect
2. Le mot de passe n'a pas été hashé correctement

**Solution** :
1. Utiliser le mot de passe par défaut (matricule pour étudiants, login pour formateurs)
2. Vérifier dans les logs du backend que le User a été créé

### Problème : CORS Error

**Solution** :
Vérifier que `CorsConfig.java` autorise `http://localhost:3000`

## 📞 Informations à Fournir en Cas de Problème

Si vous avez toujours un problème, fournissez :

1. **Logs de la console** (copier-coller complet)
2. **Logs du backend** (dernières lignes)
3. **Status du backend** : `http://localhost:8080/actuator/health`
4. **Credentials utilisés** (email/login et password)
5. **Version de Node.js** : `node --version`
6. **Version de Java** : `java --version`

