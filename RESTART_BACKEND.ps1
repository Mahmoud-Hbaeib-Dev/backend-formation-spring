# Script PowerShell pour arrêter et redémarrer le backend

Write-Host "🛑 Arrêt des processus sur le port 8080..." -ForegroundColor Yellow

# Arrêter tous les processus Java qui utilisent le port 8080
$processes = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess -Unique
if ($processes) {
    foreach ($pid in $processes) {
        try {
            $proc = Get-Process -Id $pid -ErrorAction SilentlyContinue
            if ($proc -and $proc.ProcessName -like "*java*") {
                Write-Host "   Arrêt du processus Java (PID: $pid)..." -ForegroundColor Yellow
                Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue
            }
        } catch {
            # Ignorer les erreurs
        }
    }
}

# Attendre que le port soit libéré
Write-Host "⏳ Attente de la libération du port 8080..." -ForegroundColor Yellow
Start-Sleep -Seconds 3

# Vérifier que le port est libre
$stillInUse = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue
if ($stillInUse) {
    Write-Host "⚠️  Le port 8080 est toujours utilisé. Arrêt forcé..." -ForegroundColor Red
    Get-Process -Name "java" -ErrorAction SilentlyContinue | Where-Object { $_.MainWindowTitle -like "*spring*" -or $_.CommandLine -like "*spring-boot*" } | Stop-Process -Force -ErrorAction SilentlyContinue
    Start-Sleep -Seconds 2
}

Write-Host "✅ Port 8080 libéré" -ForegroundColor Green
Write-Host ""
Write-Host "🚀 Démarrage du backend..." -ForegroundColor Cyan
Write-Host ""

# Aller dans le dossier backend et démarrer
Set-Location backend
mvn clean spring-boot:run

