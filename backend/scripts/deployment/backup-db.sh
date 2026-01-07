#!/bin/bash
# ============================================================================
# Script de sauvegarde de la base de données MySQL
# Centre de Formation Application
# ============================================================================

# Configuration
DB_NAME="formation_db"
DB_USER="${DB_USERNAME:-formation_user}"
DB_PASSWORD="${DB_PASSWORD:-}"
BACKUP_DIR="/opt/formation/backups"
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="$BACKUP_DIR/formation_db_$DATE.sql"
RETENTION_DAYS=7

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

# Créer le répertoire de sauvegarde s'il n'existe pas
mkdir -p "$BACKUP_DIR"

# Vérifier que mysqldump est disponible
if ! command -v mysqldump &> /dev/null; then
    error "mysqldump n'est pas installé ou n'est pas dans le PATH."
    exit 1
fi

# Effectuer la sauvegarde
info "Début de la sauvegarde de la base de données $DB_NAME..."

if [ -z "$DB_PASSWORD" ]; then
    mysqldump -u "$DB_USER" "$DB_NAME" > "$BACKUP_FILE"
else
    mysqldump -u "$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" > "$BACKUP_FILE"
fi

# Vérifier si la sauvegarde a réussi
if [ $? -eq 0 ]; then
    # Compresser la sauvegarde
    gzip "$BACKUP_FILE"
    BACKUP_FILE="${BACKUP_FILE}.gz"
    
    # Obtenir la taille du fichier
    SIZE=$(du -h "$BACKUP_FILE" | cut -f1)
    
    info "Sauvegarde réussie : $BACKUP_FILE ($SIZE)"
    
    # Supprimer les anciennes sauvegardes (plus de RETENTION_DAYS jours)
    info "Suppression des sauvegardes de plus de $RETENTION_DAYS jours..."
    find "$BACKUP_DIR" -name "formation_db_*.sql.gz" -mtime +$RETENTION_DAYS -delete
    
    # Afficher le nombre de sauvegardes restantes
    BACKUP_COUNT=$(ls -1 "$BACKUP_DIR"/formation_db_*.sql.gz 2>/dev/null | wc -l)
    info "Nombre de sauvegardes conservées : $BACKUP_COUNT"
    
    exit 0
else
    error "Échec de la sauvegarde."
    rm -f "$BACKUP_FILE"
    exit 1
fi


