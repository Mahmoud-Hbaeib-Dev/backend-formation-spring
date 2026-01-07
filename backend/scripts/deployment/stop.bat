@echo off
REM ============================================================================
REM Script d'arrêt de l'application Centre de Formation (Windows)
REM ============================================================================

setlocal

set APP_NAME=centre-formation-app
set APP_JAR=centre-formation-app-1.0.0.jar

echo [INFO] Recherche des processus Java pour %APP_NAME%...

REM Trouver et arrêter les processus Java correspondant au JAR
for /f "tokens=2" %%a in ('jps -l ^| findstr "%APP_JAR%"') do (
    echo [INFO] Arrêt du processus PID: %%a
    taskkill /PID %%a /F
)

echo [INFO] Application arrêtée.

endlocal


