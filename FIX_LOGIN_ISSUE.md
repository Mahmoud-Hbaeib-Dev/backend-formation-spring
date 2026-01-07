# 🔧 Résolution du Problème de Connexion Étudiant/Formateur

## ❌ Problème
- **Admin** peut se connecter ✅
- **Étudiant/Formateur** ne peuvent PAS se connecter ❌
- Erreur: `401 Bad credentials`

## 🔍 Diagnostic Rapide

### Étape 1: Vérifier l'état de la base de données

Ouvrez dans votre navigateur:
```
http://localhost:8080/api/diagnostic/status
```

Vous devriez voir:
- `totalUsers`: nombre total d'utilisateurs
- `etudiantsSansUser`: nombre d'étudiants sans User (devrait être 0)
- `formateursSansUser`: nombre de formateurs sans User (devrait être 0)
- `users`: liste de tous les Users avec leurs logins

### Étape 2: Tester la recherche d'utilisateur

Testez avec l'email de l'étudiant:
```
http://localhost:8080/api/diagnostic/test-user?email=ahmed@email.com
```

## ✅ Solution Simple

### Option 1: Redémarrer le Backend (Recommandé)

1. **Arrêter le backend** (Ctrl+C dans le terminal)

2. **Supprimer la base de données H2** (pour repartir à zéro):
   ```powershell
   cd backend
   Remove-Item -Recurse -Force data\ -ErrorAction SilentlyContinue
   ```

3. **Redémarrer le backend**:
   ```powershell
   mvn clean spring-boot:run
   ```

4. **Vérifier les logs** - Vous devriez voir:
   ```
   === Migration des données existantes ===
   🔄 Migration des étudiants...
   ✅ User créé pour Ahmed Ben Ali - Login: mat001, Password: mat001
   ...
   === Initialisation des données ===
   📋 Credentials Étudiants:
      - Étudiant 1 (MAT001): Login: mat001, Password: mat001, Email: ahmed@email.com
   ```

5. **Essayer de se connecter** avec:
   - **Email**: `ahmed@email.com` OU **Login**: `mat001`
   - **Password**: `mat001`

### Option 2: Garder les Données Existantes

Si vous voulez garder vos données:

1. **Redémarrer le backend** (sans supprimer la base):
   ```powershell
   cd backend
   mvn spring-boot:run
   ```

2. **Vérifier les logs** - `DataMigration` devrait créer les Users manquants automatiquement

3. **Vérifier avec le diagnostic**:
   ```
   http://localhost:8080/api/diagnostic/status
   ```
   - `etudiantsSansUser` devrait être 0
   - `formateursSansUser` devrait être 0

## 📋 Credentials par Défaut

### Admin
- **Login**: `admin`
- **Password**: `admin`

### Étudiants
- **Étudiant 1 (Ahmed)**:
  - Email: `ahmed@email.com`
  - Login: `mat001`
  - Password: `mat001`

- **Étudiant 2 (Fatma)**:
  - Email: `fatma@email.com`
  - Login: `mat002`
  - Password: `mat002`

- **Étudiant 3 (Mohamed)**:
  - Email: `mohamed@email.com`
  - Login: `mat003`
  - Password: `mat003`

- **Étudiant 4 (Sana)**:
  - Email: `sana@email.com`
  - Login: `mat004`
  - Password: `mat004`

### Formateurs
- **Formateur 1 (Dupont)**:
  - Email: `dupont@formation.com`
  - Login: `dupont`
  - Password: `dupont`

- **Formateur 2 (Martin)**:
  - Email: `martin@formation.com`
  - Login: `martin`
  - Password: `martin`

- **Formateur 3 (Bernard)**:
  - Email: `bernard@formation.com`
  - Login: `bernard`
  - Password: `bernard`

## 🐛 Si ça ne marche toujours pas

1. **Vérifier les logs du backend** - Cherchez les messages:
   - `✅ User créé pour...`
   - `❌ Erreur lors de la création du User...`

2. **Vérifier le diagnostic**:
   ```
   http://localhost:8080/api/diagnostic/status
   ```

3. **Tester la recherche d'utilisateur**:
   ```
   http://localhost:8080/api/diagnostic/test-user?email=ahmed@email.com
   ```

4. **Vérifier que le backend écoute sur le bon port**:
   - Backend: `http://localhost:8080`
   - Frontend: `http://localhost:3000`

## 💡 Note Importante

- Vous pouvez vous connecter avec **soit l'email, soit le login**
- Le mot de passe est toujours le **login** (en minuscules)
- Exemple: Pour Ahmed, vous pouvez utiliser:
  - Email: `ahmed@email.com` + Password: `mat001`
  - OU Login: `mat001` + Password: `mat001`

