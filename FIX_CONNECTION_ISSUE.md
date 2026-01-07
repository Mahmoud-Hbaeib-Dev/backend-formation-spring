# 🔧 Résolution du Problème de Connexion

## ❌ Problème Actuel

**Erreur** : `401 Unauthorized - Bad credentials`

**Cause** : Les étudiants/formateurs existants dans la base de données n'ont pas de User associé, donc ils ne peuvent pas se connecter.

## ✅ Solution

J'ai créé deux mécanismes pour résoudre ce problème :

### 1. **DataMigration** (Nouveau)
- S'exécute **avant** DataInitializer
- Vérifie tous les étudiants/formateurs existants
- Crée automatiquement les Users manquants
- Affiche les credentials dans les logs

### 2. **DataInitializer amélioré**
- Vérifie si les données existent déjà
- Crée les Users manquants si nécessaire
- Évite les doublons

## 🚀 Étapes pour Résoudre

### Option 1 : Redémarrer le Backend (Recommandé)

1. **Arrêter le backend** (Ctrl+C)

2. **Supprimer la base de données H2** (si vous utilisez H2) :
   ```bash
   # Dans le dossier backend
   rm -rf data/
   # Ou sous Windows
   rmdir /s data
   ```

3. **Redémarrer le backend** :
   ```bash
   cd backend
   mvn spring-boot:run
   ```

4. **Vérifier les logs** - Vous devriez voir :
   ```
   === Migration des données existantes ===
   🔄 Migration des étudiants...
   ✅ User créé pour Ahmed Ben Ali - Login: mat001, Password: mat001
   ...
   === Initialisation des données ===
   ✅ Étudiants créés avec leurs comptes User
   📋 Credentials Étudiants:
      - Étudiant 1 (MAT001): Login: mat001, Password: mat001, Email: ahmed@email.com
   ```

5. **Essayer de se connecter** avec :
   - Email: `ahmed@email.com` OU Login: `mat001`
   - Password: `mat001`

### Option 2 : Garder les Données Existantes

Si vous voulez garder vos données existantes :

1. **Redémarrer le backend** (sans supprimer la base)
   ```bash
   cd backend
   mvn spring-boot:run
   ```

2. **Vérifier les logs** - DataMigration devrait créer les Users manquants :
   ```
   === Migration des données existantes ===
   🔄 Migration des étudiants...
   ✅ User créé pour Ahmed Ben Ali - Login: mat001, Password: mat001
   ```

3. **Essayer de se connecter**

## 🔍 Vérification

### Vérifier dans les Logs du Backend

Cherchez ces messages dans les logs :

```
🔍 [USER DETAILS] Recherche de l'utilisateur avec: ahmed@email.com
🔍 [USER DETAILS] Tentative 1: Recherche par login...
❌ [USER DETAILS] Aucun utilisateur trouvé par login
🔍 [USER DETAILS] Tentative 2: Recherche par email (Etudiant)...
✅ [USER DETAILS] Étudiant trouvé par email: Ahmed Ben Ali
✅ [USER DETAILS] User associé trouvé: mat001
```

Si vous voyez :
```
❌ [USER DETAILS] Étudiant trouvé mais PAS de User associé!
```

Cela signifie que la migration n'a pas fonctionné. Dans ce cas, supprimez la base et redémarrez.

### Vérifier dans H2 Console

1. Accéder à : `http://localhost:8080/h2-console`
2. Connecter avec :
   - JDBC URL: `jdbc:h2:file:./data/formationdb`
   - Username: `sa`
   - Password: (vide)
3. Exécuter ces requêtes :

```sql
-- Vérifier les Users
SELECT * FROM USERS;

-- Vérifier les Étudiants avec leurs Users
SELECT e.id, e.matricule, e.nom, e.prenom, e.email, u.login, u.roles 
FROM ETUDIANTS e 
LEFT JOIN USERS u ON e.user_id = u.id;

-- Vérifier les Formateurs avec leurs Users
SELECT f.id, f.nom, f.email, u.login, u.roles 
FROM FORMATEURS f 
LEFT JOIN USERS u ON f.user_id = u.id;
```

**Tous les étudiants/formateurs doivent avoir un `user_id` non NULL.**

## 🧪 Test de Connexion

### Test 1 : Avec Email
- Email: `ahmed@email.com`
- Password: `mat001`

### Test 2 : Avec Login
- Login: `mat001`
- Password: `mat001`

### Test 3 : Formateur
- Email: `dupont@formation.com` OU Login: `dupont`
- Password: `dupont`

## 📋 Logs à Surveiller

### Backend (Terminal)
```
🔐 [AUTH API] Tentative de connexion reçue
📧 Login/Email: ahmed@email.com
🔄 [AUTH API] Authentification en cours...
🔍 [USER DETAILS] Recherche de l'utilisateur avec: ahmed@email.com
✅ [USER DETAILS] Étudiant trouvé par email: Ahmed Ben Ali
✅ [USER DETAILS] User associé trouvé: mat001
✅ [AUTH API] Authentification réussie
✅ [AUTH API] Connexion réussie pour: mat001 (Rôle: ETUDIANT)
```

### Frontend (Console)
```
✅ [API] Réponse reçue: 200 /auth/login
📦 [API] Données de réponse: {token: "...", username: "mat001", roles: ["ETUDIANT"]}
✅ [AUTH SERVICE] Connexion réussie!
```

## ⚠️ Si Ça Ne Fonctionne Toujours Pas

1. **Vérifier que le backend est bien redémarré** après les modifications
2. **Vérifier les logs du backend** pour voir si DataMigration s'est exécuté
3. **Vérifier dans H2 console** que les Users existent
4. **Supprimer complètement la base** et redémarrer :
   ```bash
   # Supprimer le dossier data
   rm -rf backend/data/
   # Redémarrer
   mvn spring-boot:run
   ```

## 📝 Notes

- Les mots de passe par défaut sont simples (matricule pour étudiants, login pour formateurs)
- En production, il faudrait forcer le changement de mot de passe au premier login
- Les credentials exacts sont affichés dans les logs du backend au démarrage

