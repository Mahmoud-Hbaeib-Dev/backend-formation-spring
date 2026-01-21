# Script PowerShell pour demarrer le projet automatiquement
# Usage: .\START_PROJECT.ps1

Write-Host "Demarrage automatique du projet..." -ForegroundColor Cyan
Write-Host ""

# Etape 1 : Arreter TOUS les processus utilisant le port 8080
Write-Host "1. Liberation du port 8080..." -ForegroundColor Yellow
$maxAttempts = 5
$attempt = 0
$portFreed = $false

while ($attempt -lt $maxAttempts -and -not $portFreed) {
    $attempt++
    $connections = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
    if ($connections) {
        $processIds = $connections | Select-Object -ExpandProperty OwningProcess -Unique
        Write-Host "   Tentative $attempt/$maxAttempts - Processus trouves: $($processIds.Count)" -ForegroundColor Gray
        
        foreach ($processId in $processIds) {
            try {
                $proc = Get-Process -Id $processId -ErrorAction SilentlyContinue
                if ($proc) {
                    Write-Host "   Arret du processus $($proc.ProcessName) (PID: $processId)..." -ForegroundColor Gray
                    Stop-Process -Id $processId -Force -ErrorAction SilentlyContinue
                }
            } catch {
                # Ignorer les erreurs
            }
        }
        
        # Attendre que le processus se termine
        Start-Sleep -Seconds 3
        
        # Verifier si le port est libre
        $stillInUse = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue
        if (-not $stillInUse) {
            $portFreed = $true
            Write-Host "   Port 8080 libere" -ForegroundColor Green
        }
    } else {
        $portFreed = $true
        Write-Host "   Port 8080 deja libre" -ForegroundColor Green
    }
}

if (-not $portFreed) {
    Write-Host ""
    Write-Host "   ERREUR: Impossible de liberer le port 8080 automatiquement !" -ForegroundColor Red
    Write-Host ""
    Write-Host "   Solution rapide:" -ForegroundColor Yellow
    Write-Host "   Executez: .\KILL_PORT_8080.ps1" -ForegroundColor Cyan
    Write-Host "   Puis relancez: .\START_PROJECT.ps1" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "   Ou fermez manuellement toutes les fenetres PowerShell qui executent le backend" -ForegroundColor Yellow
    Write-Host ""
    exit 1
}

Write-Host ""

# Etape 2 : Verifier que le dossier backend existe
if (-not (Test-Path "backend")) {
    Write-Host "ERREUR: Le dossier 'backend' n'existe pas !" -ForegroundColor Red
    Write-Host "   Assurez-vous d'executer ce script depuis la racine du projet." -ForegroundColor Yellow
    exit 1
}

# Etape 3 : Aller dans le dossier backend et demarrer
Write-Host "2. Demarrage du backend..." -ForegroundColor Yellow
Write-Host "   (Le backend demarre dans une nouvelle fenetre PowerShell)" -ForegroundColor Gray
Write-Host "   (Attendez le message 'Started Application...' avant d'utiliser l'application)" -ForegroundColor Gray
Write-Host ""

Set-Location backend

# Demarrer le backend dans une nouvelle fenetre
$command = "Write-Host 'Backend en cours de demarrage...' -ForegroundColor Cyan; Write-Host ''; mvn clean spring-boot:run"
Start-Process powershell -ArgumentList "-NoExit", "-Command", $command

Set-Location ..

Write-Host "   Backend en cours de demarrage..." -ForegroundColor Green
Write-Host ""

# Etape 3 : Attendre que le backend soit pret
Write-Host "3. Attente du demarrage complet du backend (peut prendre 30-60 secondes)..." -ForegroundColor Yellow
Write-Host "   (Verification toutes les 2 secondes...)" -ForegroundColor Gray

$maxAttempts = 60
$attempt = 0
$backendReady = $false

while ($attempt -lt $maxAttempts -and -not $backendReady) {
    Start-Sleep -Seconds 2
    $attempt++
    
    try {
        $testResponse = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -TimeoutSec 2 -UseBasicParsing -ErrorAction Stop
        if ($testResponse.StatusCode -eq 200) {
            $backendReady = $true
        }
    } catch {
        # Backend pas encore pret
    }
    
    if ($attempt % 5 -eq 0) {
        Write-Host "   Tentative $attempt/$maxAttempts..." -ForegroundColor Gray
    }
}

Write-Host ""

if ($backendReady) {
    Write-Host "Backend demarre avec succes !" -ForegroundColor Green
    Write-Host ""
    
    # Verifier H2 Console
    Write-Host "4. Verification de H2 Console..." -ForegroundColor Yellow
    try {
        $h2Response = Invoke-WebRequest -Uri "http://localhost:8080/h2-console" -TimeoutSec 3 -UseBasicParsing -ErrorAction Stop
        Write-Host "   H2 Console est accessible" -ForegroundColor Green
        Write-Host "   URL: http://localhost:8080/h2-console" -ForegroundColor Cyan
    } catch {
        Write-Host "   H2 Console n'est pas encore accessible (c'est normal, attendez quelques secondes)" -ForegroundColor Yellow
    }
    
    Write-Host ""
    Write-Host "Projet demarre avec succes !" -ForegroundColor Green
    Write-Host ""
    Write-Host "URLs importantes:" -ForegroundColor Cyan
    Write-Host "   - Backend API: http://localhost:8080" -ForegroundColor White
    Write-Host "   - H2 Console: http://localhost:8080/h2-console" -ForegroundColor White
    Write-Host "   - Frontend: http://localhost:3000" -ForegroundColor White
    Write-Host ""
    Write-Host "Identifiants par defaut:" -ForegroundColor Cyan
    Write-Host "   - Formateur: dupont / dupont" -ForegroundColor White
    Write-Host "   - Etudiant: mat001 / mat001" -ForegroundColor White
    Write-Host "   - Admin: admin / admin" -ForegroundColor White
    Write-Host ""
    Write-Host "Le backend continue de tourner dans une fenetre PowerShell separee" -ForegroundColor Gray
    Write-Host "Pour l'arreter, fermez cette fenetre ou appuyez sur Ctrl+C" -ForegroundColor Gray
    
} else {
    Write-Host "Le backend n'a pas demarre dans les temps impartis." -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Verifiez:" -ForegroundColor Cyan
    Write-Host "   1. La fenetre PowerShell du backend pour voir les erreurs" -ForegroundColor White
    Write-Host "   2. Que Java et Maven sont installes" -ForegroundColor White
    Write-Host "   3. Que le port 8080 n'est pas utilise par un autre processus" -ForegroundColor White
    Write-Host ""
    Write-Host "   Vous pouvez toujours acceder a:" -ForegroundColor Cyan
    Write-Host "   - Backend: http://localhost:8080" -ForegroundColor White
    Write-Host "   - H2 Console: http://localhost:8080/h2-console" -ForegroundColor White
}
