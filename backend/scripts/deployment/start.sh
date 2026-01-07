#!/bin/bash
# ============================================================================
# Script de démarrage de l'application Centre de Formation
# ============================================================================

# Configuration
APP_NAME="centre-formation-app"
APP_JAR="centre-formation-app-1.0.0.jar"
APP_DIR="/opt/formation"
LOG_DIR="$APP_DIR/logs"
PID_FILE="$APP_DIR/$APP_NAME.pid"

# Couleurs pour les messages
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Fonction pour afficher les messages
info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# Vérifier si l'application est déjà en cours d'exécution
if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    if ps -p $PID > /dev/null 2>&1; then
        error "L'application est déjà en cours d'exécution (PID: $PID)"
        exit 1
    else
        warning "Fichier PID trouvé mais processus inexistant. Suppression du fichier PID."
        rm -f "$PID_FILE"
    fi
fi

# Vérifier que le JAR existe
if [ ! -f "$APP_DIR/$APP_JAR" ]; then
    error "Le fichier JAR n'existe pas : $APP_DIR/$APP_JAR"
    exit 1
fi

# Créer le répertoire de logs s'il n'existe pas
mkdir -p "$LOG_DIR"

# Démarrer l'application
info "Démarrage de l'application $APP_NAME..."
cd "$APP_DIR"

# Démarrer en arrière-plan et sauvegarder le PID
nohup java -jar \
    -Dspring.profiles.active=prod \
    -Xms512m \
    -Xmx1024m \
    "$APP_JAR" > "$LOG_DIR/startup.log" 2>&1 &

# Sauvegarder le PID
echo $! > "$PID_FILE"

# Attendre un peu pour vérifier que l'application démarre
sleep 3

# Vérifier si l'application est toujours en cours d'exécution
if ps -p $(cat "$PID_FILE") > /dev/null 2>&1; then
    info "Application démarrée avec succès (PID: $(cat $PID_FILE))"
    info "Logs disponibles dans : $LOG_DIR"
    info "Pour arrêter l'application : ./stop.sh"
else
    error "L'application n'a pas pu démarrer. Vérifiez les logs : $LOG_DIR/startup.log"
    rm -f "$PID_FILE"
    exit 1
fi


