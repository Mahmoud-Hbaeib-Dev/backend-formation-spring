# 🔄 Script de Redémarrage et Correction

## 🎯 Solution Simple en 3 Étapes

### Étape 1: Arrêter le Backend
Appuyez sur `Ctrl+C` dans le terminal où le backend tourne.

### Étape 2: Nettoyer et Redémarrer

**Option A: PowerShell (Recommandé)**

```powershell
# Aller dans le dossier backend
cd backend

# Supprimer la base de données H2 (pour repartir à zéro)
Remove-Item -Recurse -Force data\ -ErrorAction SilentlyContinue

# Nettoyer et redémarrer
mvn clean spring-boot:run
```

**Option B: Commandes séparées**

```powershell
cd backend
Remove-Item -Recurse -Force data\
mvn clean
mvn spring-boot:run
```

### Étape 3: Vérifier les Logs

Dans les logs du backend, vous devriez voir:

```
=== Migration des données existantes ===
🔄 Migration des étudiants...
✅ User créé pour Ahmed Ben Ali - Login: mat001, Password: mat001
✅ User créé pour Fatma Trabelsi - Login: mat002, Password: mat002
...
=== Initialisation des données ===
📋 Credentials Étudiants:
   - Étudiant 1 (MAT001): Login: mat001, Password: mat001, Email: ahmed@email.com
   - Étudiant 2 (MAT002): Login: mat002, Password: mat002, Email: fatma@email.com
...
```

## ✅ Test de Connexion

Une fois le backend redémarré:

1. **Ouvrez le frontend** (si pas déjà ouvert):
   ```powershell
   cd frontend
   npm run dev
   ```

2. **Allez sur** `http://localhost:3000/login`

3. **Connectez-vous avec**:
   - **Email**: `ahmed@email.com` OU **Login**: `mat001`
   - **Password**: `mat001`

## 🔍 Vérification Rapide

### Test 1: Backend fonctionne ?
```
http://localhost:8080/api/auth/test
```
Devrait retourner: `{"status":"OK","message":"Backend is running!"}`

### Test 2: Base de données OK ?
```
http://localhost:8080/api/diagnostic/status
```
Vérifiez que `etudiantsSansUser` et `formateursSansUser` sont à **0**.

### Test 3: Utilisateur existe ?
```
http://localhost:8080/api/diagnostic/test-user?email=ahmed@email.com
```
Devrait retourner les infos de l'utilisateur.

## ❌ Si ça ne marche toujours pas

1. **Vérifiez que le backend écoute sur le port 8080**
2. **Vérifiez que le frontend écoute sur le port 3000**
3. **Vérifiez les logs du backend** pour les erreurs
4. **Vérifiez la console du navigateur** (F12) pour les erreurs réseau

## 💡 Note

**Le problème n'est PAS le build du frontend !**

Le frontend fonctionne en mode développement avec `npm run dev` - pas besoin de build.

Le vrai problème est que les **Users n'existent pas dans la base de données**. Le redémarrage du backend avec suppression de la base de données va créer tous les Users automatiquement.

