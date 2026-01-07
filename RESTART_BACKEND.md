# 🔄 Comment Redémarrer le Backend Correctement

## ❌ Le Problème

L'erreur "No static resource" et "Bad credentials" indiquent que le backend qui tourne n'a **PAS** les dernières modifications.

## ✅ Solution : Redémarrer Complètement

### Étape 1 : Arrêter TOUS les processus Java

**Dans PowerShell :**
```powershell
Get-Process -Name java -ErrorAction SilentlyContinue | Stop-Process -Force
```

**Ou manuellement :**
- Trouvez la fenêtre de terminal où le backend tourne
- Appuyez sur `Ctrl+C` plusieurs fois
- Attendez que le processus s'arrête complètement

### Étape 2 : Nettoyer et Recompiler

```powershell
cd backend
mvn clean
mvn compile
```

### Étape 3 : Redémarrer le Backend

```powershell
mvn spring-boot:run
```

**Attendez 30-60 secondes** pour que le backend démarre complètement.

### Étape 4 : Vérifier les Logs

Regardez les logs au démarrage. Vous devriez voir :

```
=== Migration des données existantes ===
✅ User créé pour Ahmed Ben Ali - Login: mat001, Password: mat001
...
=== Initialisation des données ===
📋 Credentials Étudiants:
   - Étudiant 1 (MAT001): Login: mat001, Password: mat001, Email: ahmed@email.com
```

## 🧪 Test Après Redémarrage

### 1. Test de l'endpoint de test
```
http://localhost:8080/api/auth/test
```

### 2. Test de connexion (PRIORITAIRE)

Dans votre frontend React, essayez :
- Email : `ahmed@email.com` OU Login : `mat001`
- Password : `mat001`

### 3. Vérifier les Logs du Backend

Quand vous essayez de vous connecter, regardez les logs. Vous devriez voir :

```
🔐 [AUTH API] Tentative de connexion reçue
📧 Login/Email: ahmed@email.com
🔍 [USER DETAILS] Recherche de l'utilisateur avec: ahmed@email.com
✅ [USER DETAILS] Étudiant trouvé par email: Ahmed Ben Ali
✅ [USER DETAILS] User associé trouvé: mat001
✅ [AUTH API] Authentification réussie
```

## ⚠️ Si Ça Ne Fonctionne Toujours Pas

### Option 1 : Supprimer la Base de Données et Redémarrer

```powershell
cd backend
Remove-Item -Recurse -Force data\
mvn clean spring-boot:run
```

Cela supprimera toutes les données et les recréera avec les Users correctement liés.

### Option 2 : Vérifier les Logs d'Erreur

Regardez les logs du backend pour voir exactement ce qui se passe lors de la tentative de connexion. Les logs avec les emojis (🔍, ✅, ❌) vous diront exactement où ça bloque.

## 📝 Note Importante

**L'erreur "No static resource" n'est PAS le vrai problème.** Le vrai problème est que l'authentification ne fonctionne pas. Une fois que la connexion fonctionne, vous pouvez ignorer cette erreur.

