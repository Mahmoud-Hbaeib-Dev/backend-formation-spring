# ⚡ Solution Rapide - Port 8080 Occupé

## ❌ Problème
```
Web server failed to start. Port 8080 was already in use.
```

## ✅ Solution en 2 Étapes

### Étape 1: Arrêter l'ancien backend

**Option A: PowerShell (Recommandé)**

```powershell
# Arrêter tous les processus Java
Get-Process -Name "java" -ErrorAction SilentlyContinue | Stop-Process -Force

# OU utiliser le script automatique
.\RESTART_BACKEND.ps1
```

**Option B: Manuellement**

1. Ouvrez le **Gestionnaire des tâches** (Ctrl+Shift+Esc)
2. Cherchez les processus **Java**
3. Cliquez droit → **Arrêter la tâche**

**Option C: Trouver le processus sur le port 8080**

```powershell
# Trouver le PID du processus
Get-NetTCPConnection -LocalPort 8080 | Select-Object OwningProcess

# Arrêter le processus (remplacez PID par le numéro trouvé)
Stop-Process -Id PID -Force
```

### Étape 2: Redémarrer le backend

```powershell
cd backend
mvn clean spring-boot:run
```

## 🔍 Vérification

Une fois le backend redémarré, testez:

1. **Test simple**: `http://localhost:8080/test`
2. **Test auth**: `http://localhost:8080/api/auth/test`
3. **Diagnostic**: `http://localhost:8080/api/diagnostic/status`

## 💡 Astuce

Si le problème persiste, utilisez un autre port temporairement:

```powershell
cd backend
$env:SERVER_PORT=8081
mvn spring-boot:run
```

Puis testez avec: `http://localhost:8081/test`

