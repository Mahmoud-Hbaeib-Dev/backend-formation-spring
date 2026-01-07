# 🚀 Guide de Démarrage Rapide - Frontend

## ✅ Le Frontend n'a PAS besoin d'être "buildé" en développement !

En développement, vous utilisez **Vite** qui compile à la volée. Pas besoin de build !

## 📋 Vérifications Rapides

### 1. Vérifier que le Frontend est lancé

Dans un terminal, allez dans le dossier `frontend` et lancez:

```powershell
cd frontend
npm run dev
```

Vous devriez voir:
```
  VITE v5.0.8  ready in XXX ms

  ➜  Local:   http://localhost:3000/
  ➜  Network: use --host to expose
```

### 2. Vérifier que le Backend est lancé

Dans un autre terminal, allez dans le dossier `backend` et lancez:

```powershell
cd backend
mvn spring-boot:run
```

Vous devriez voir:
```
Started CentreFormationApplication in X.XXX seconds
```

### 3. Vérifier la Configuration

Le frontend est configuré pour se connecter à:
- **Backend URL**: `http://localhost:8080/api`
- **Frontend URL**: `http://localhost:3000`

## 🔍 Diagnostic du Problème de Connexion

### Le problème "Bad credentials" n'est PAS lié au build !

Le problème vient probablement de la **base de données** :

1. **Les étudiants/formateurs n'ont pas de User dans la base de données**
2. **Le backend n'a pas été redémarré** pour exécuter `DataMigration`

### Solution Simple

1. **Arrêter le backend** (Ctrl+C)

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
   ✅ User créé pour Ahmed Ben Ali - Login: mat001, Password: mat001
   ```

5. **Essayer de se connecter** avec:
   - Email: `ahmed@email.com` OU Login: `mat001`
   - Password: `mat001`

## 🐛 Si ça ne marche toujours pas

### Vérifier que le Backend répond

Ouvrez dans votre navigateur:
```
http://localhost:8080/api/auth/test
```

Vous devriez voir:
```json
{
  "status": "OK",
  "message": "Backend is running!",
  "timestamp": ...
}
```

### Vérifier l'état de la base de données

```
http://localhost:8080/api/diagnostic/status
```

Vérifiez que:
- `etudiantsSansUser` = 0
- `formateursSansUser` = 0

### Vérifier les logs du backend

Cherchez dans les logs:
- `✅ User créé pour...` = OK
- `❌ Erreur lors de la création du User...` = Problème

## 💡 Note Importante

- **En développement**: Utilisez `npm run dev` (pas besoin de build)
- **En production**: Utilisez `npm run build` pour créer les fichiers optimisés

Mais pour le moment, **le problème n'est PAS le build**, c'est la base de données qui n'a pas les Users créés !

