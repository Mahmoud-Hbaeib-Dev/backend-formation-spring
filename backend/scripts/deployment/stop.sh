#!/bin/bash
# ============================================================================
# Script d'arrêt de l'application Centre de Formation
# ============================================================================

# Configuration
APP_NAME="centre-formation-app"
APP_DIR="/opt/formation"
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

# Vérifier si le fichier PID existe
if [ ! -f "$PID_FILE" ]; then
    error "Fichier PID introuvable. L'application n'est peut-être pas en cours d'exécution."
    exit 1
fi

# Lire le PID
PID=$(cat "$PID_FILE")

# Vérifier si le processus existe
if ! ps -p $PID > /dev/null 2>&1; then
    warning "Le processus avec PID $PID n'existe pas. Suppression du fichier PID."
    rm -f "$PID_FILE"
    exit 0
fi

# Arrêter l'application gracieusement
info "Arrêt de l'application (PID: $PID)..."
kill $PID

# Attendre jusqu'à 30 secondes pour l'arrêt gracieux
TIMEOUT=30
ELAPSED=0

while [ $ELAPSED -lt $TIMEOUT ]; do
    if ! ps -p $PID > /dev/null 2>&1; then
        info "Application arrêtée avec succès."
        rm -f "$PID_FILE"
        exit 0
    fi
    sleep 1
    ELAPSED=$((ELAPSED + 1))
done

# Si l'application n'est pas arrêtée, forcer l'arrêt
if ps -p $PID > /dev/null 2>&1; then
    warning "L'application n'a pas répondu à l'arrêt gracieux. Arrêt forcé..."
    kill -9 $PID
    sleep 2
    
    if ! ps -p $PID > /dev/null 2>&1; then
        info "Application arrêtée de force."
        rm -f "$PID_FILE"
        exit 0
    else
        error "Impossible d'arrêter l'application."
        exit 1
    fi
fi


