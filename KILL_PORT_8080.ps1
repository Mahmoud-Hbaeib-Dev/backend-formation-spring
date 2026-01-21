# Script pour liberer le port 8080 manuellement
# Usage: .\KILL_PORT_8080.ps1

Write-Host "Recherche des processus utilisant le port 8080..." -ForegroundColor Cyan
Write-Host ""

$connections = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue

if (-not $connections) {
    Write-Host "Aucun processus n'utilise le port 8080" -ForegroundColor Green
    exit 0
}

$processIds = $connections | Select-Object -ExpandProperty OwningProcess -Unique

Write-Host "Processus trouves utilisant le port 8080:" -ForegroundColor Yellow
Write-Host ""

foreach ($processId in $processIds) {
    try {
        $proc = Get-Process -Id $processId -ErrorAction SilentlyContinue
        if ($proc) {
            Write-Host "  PID: $processId" -ForegroundColor White
            Write-Host "  Nom: $($proc.ProcessName)" -ForegroundColor White
            Write-Host "  Chemin: $($proc.Path)" -ForegroundColor Gray
            Write-Host ""
        }
    } catch {
        Write-Host "  PID: $processId (impossible d'obtenir les details)" -ForegroundColor Yellow
    }
}

Write-Host "Arret de ces processus..." -ForegroundColor Yellow
Write-Host ""

foreach ($processId in $processIds) {
    try {
        $proc = Get-Process -Id $processId -ErrorAction SilentlyContinue
        if ($proc) {
            Write-Host "Arret du processus $($proc.ProcessName) (PID: $processId)..." -ForegroundColor Gray
            Stop-Process -Id $processId -Force -ErrorAction SilentlyContinue
        }
    } catch {
        Write-Host "Erreur lors de l'arret du processus $processId: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Start-Sleep -Seconds 2

$stillInUse = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue

if ($stillInUse) {
    Write-Host ""
    Write-Host "ERREUR: Le port 8080 est toujours utilise !" -ForegroundColor Red
    Write-Host ""
    Write-Host "Essayez d'executer ce script en tant qu'administrateur:" -ForegroundColor Yellow
    Write-Host "  1. Clic droit sur PowerShell" -ForegroundColor White
    Write-Host "  2. Selectionnez 'Executer en tant qu'administrateur'" -ForegroundColor White
    Write-Host "  3. Naviguez vers le dossier du projet" -ForegroundColor White
    Write-Host "  4. Executez: .\KILL_PORT_8080.ps1" -ForegroundColor White
    Write-Host ""
    Write-Host "Ou fermez manuellement toutes les fenetres PowerShell qui executent le backend" -ForegroundColor Yellow
    exit 1
} else {
    Write-Host ""
    Write-Host "Port 8080 libere avec succes !" -ForegroundColor Green
    Write-Host ""
    Write-Host "Vous pouvez maintenant executer: .\START_PROJECT.ps1" -ForegroundColor Cyan
}

