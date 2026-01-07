#!/bin/bash
# ============================================================================
# Script de restauration de la base de données MySQL
# Centre de Formation Application
# ============================================================================

# Configuration
DB_NAME="formation_db"
DB_USER="${DB_USERNAME:-formation_user}"
DB_PASSWORD="${DB_PASSWORD:-}"
BACKUP_DIR="/opt/formation/backups"

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

# Vérifier les arguments
if [ $# -eq 0 ]; then
    error "Usage: $0 <fichier_backup.sql[.gz]>"
    echo ""
    echo "Sauvegardes disponibles :"
    ls -lh "$BACKUP_DIR"/formation_db_*.sql* 2>/dev/null | awk '{print "  - " $9 " (" $5 ")"}'
    exit 1
fi

BACKUP_FILE="$1"

# Vérifier si le fichier existe
if [ ! -f "$BACKUP_FILE" ]; then
    error "Le fichier de sauvegarde n'existe pas : $BACKUP_FILE"
    exit 1
fi

# Confirmation
warning "ATTENTION : Cette opération va écraser la base de données actuelle !"
read -p "Êtes-vous sûr de vouloir continuer ? (oui/non) : " CONFIRM

if [ "$CONFIRM" != "oui" ]; then
    info "Restauration annulée."
    exit 0
fi

# Détecter si le fichier est compressé
if [[ "$BACKUP_FILE" == *.gz ]]; then
    info "Décompression de la sauvegarde..."
    gunzip -c "$BACKUP_FILE" | mysql -u "$DB_USER" ${DB_PASSWORD:+-p"$DB_PASSWORD"} "$DB_NAME"
else
    info "Restauration de la base de données..."
    mysql -u "$DB_USER" ${DB_PASSWORD:+-p"$DB_PASSWORD"} "$DB_NAME" < "$BACKUP_FILE"
fi

# Vérifier si la restauration a réussi
if [ $? -eq 0 ]; then
    info "Restauration réussie !"
    exit 0
else
    error "Échec de la restauration."
    exit 1
fi


