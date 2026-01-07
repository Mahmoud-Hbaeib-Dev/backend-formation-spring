@echo off
REM ============================================================================
REM Script de démarrage de l'application Centre de Formation (Windows)
REM ============================================================================

setlocal

REM Configuration
set APP_NAME=centre-formation-app
set APP_JAR=centre-formation-app-1.0.0.jar
set APP_DIR=C:\opt\formation
set LOG_DIR=%APP_DIR%\logs
set PID_FILE=%APP_DIR%\%APP_NAME%.pid

REM Vérifier que le JAR existe
if not exist "%APP_DIR%\%APP_JAR%" (
    echo [ERROR] Le fichier JAR n'existe pas : %APP_DIR%\%APP_JAR%
    exit /b 1
)

REM Créer le répertoire de logs s'il n'existe pas
if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"

REM Démarrer l'application
echo [INFO] Démarrage de l'application %APP_NAME%...
cd /d "%APP_DIR%"

start /B java -jar -Dspring.profiles.active=prod -Xms512m -Xmx1024m "%APP_JAR%" > "%LOG_DIR%\startup.log" 2>&1

echo [INFO] Application démarrée.
echo [INFO] Logs disponibles dans : %LOG_DIR%
echo [INFO] Pour arrêter l'application, fermez la fenêtre ou utilisez stop.bat

endlocal


