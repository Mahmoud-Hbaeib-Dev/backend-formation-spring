-- ============================================================================
-- Script de création de la base de données MySQL
-- Centre de Formation Application
-- ============================================================================

-- Créer la base de données
CREATE DATABASE IF NOT EXISTS formation_db 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- Utiliser la base de données
USE formation_db;

-- Note: Les tables seront créées automatiquement par Hibernate
-- avec spring.jpa.hibernate.ddl-auto=update

-- ============================================================================
-- Script de réinitialisation (optionnel)
-- ============================================================================

-- DROP DATABASE IF EXISTS formation_db;
-- CREATE DATABASE formation_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

