# 🔍 Comment Vérifier la Base de Données

## Option 1 : H2 Console (Recommandé pour le Développement)

Vous utilisez H2 en développement, qui est accessible via le navigateur !

### Étape 1 : Vérifier que le Backend est Démarré

Assurez-vous que votre backend Spring Boot est en cours d'exécution.

### Étape 2 : Accéder à H2 Console

Ouvrez votre navigateur et allez à :
```
http://localhost:8080/h2-console
```

### Étape 3 : Se Connecter à H2

Dans la page de connexion H2, entrez :

**JDBC URL :**
```
jdbc:h2:file:./data/formationdb
```

**User Name :**
```
sa
```

**Password :**
```
(laisser vide)
```

Puis cliquez sur **"Connect"**.

### Étape 4 : Vérifier les Données

Une fois connecté, vous pouvez exécuter ces requêtes SQL :

#### Vérifier les Users
```sql
SELECT * FROM USERS;
```

#### Vérifier les Étudiants avec leurs Users
```sql
SELECT 
    e.ID, 
    e.MATRICULE, 
    e.NOM, 
    e.PRENOM, 
    e.EMAIL, 
    u.LOGIN, 
    u.ROLES,
    e.USER_ID
FROM ETUDIANTS e 
LEFT JOIN USERS u ON e.USER_ID = u.ID;
```

#### Vérifier les Formateurs avec leurs Users
```sql
SELECT 
    f.ID, 
    f.NOM, 
    f.EMAIL, 
    u.LOGIN, 
    u.ROLES,
    f.USER_ID
FROM FORMATEURS f 
LEFT JOIN USERS u ON f.USER_ID = u.ID;
```

#### Compter les Étudiants sans User
```sql
SELECT COUNT(*) as ETUDIANTS_SANS_USER
FROM ETUDIANTS 
WHERE USER_ID IS NULL;
```

#### Compter les Formateurs sans User
```sql
SELECT COUNT(*) as FORMATEURS_SANS_USER
FROM FORMATEURS 
WHERE USER_ID IS NULL;
```

## Option 2 : Via l'Endpoint de Diagnostic (Quand il fonctionnera)

Une fois que l'endpoint de diagnostic fonctionne, vous pourrez accéder à :
```
http://localhost:8080/api/diagnostic/status
```

## Option 3 : MySQL (Si vous utilisez MySQL en Production)

Si vous utilisez MySQL, vous ne pouvez **PAS** l'ouvrir directement dans le navigateur. Vous devez utiliser :

### A. MySQL Workbench (Interface Graphique)
1. Téléchargez MySQL Workbench
2. Connectez-vous avec vos identifiants MySQL
3. Explorez la base de données `formation_db`

### B. Ligne de Commande MySQL
```bash
mysql -u root -p
```

Puis :
```sql
USE formation_db;
SELECT * FROM USERS;
SELECT * FROM ETUDIANTS;
SELECT * FROM FORMATEURS;
```

### C. phpMyAdmin (Si installé)
Si vous avez phpMyAdmin installé, vous pouvez y accéder via :
```
http://localhost/phpmyadmin
```

## 🔧 Résolution du Problème 403

Si vous obtenez toujours l'erreur 403 sur `/api/diagnostic/status`, voici les étapes :

### 1. Vérifier que le Backend est Redémarré

**Arrêtez** complètement le backend (Ctrl+C) et **redémarrez-le** :
```bash
cd backend
mvn spring-boot:run
```

### 2. Vérifier les Logs au Démarrage

Regardez les logs du backend au démarrage. Vous devriez voir :
```
=== Migration des données existantes ===
✅ User créé pour Ahmed Ben Ali - Login: mat001, Password: mat001
...
=== Initialisation des données ===
```

### 3. Vérifier H2 Console

Accédez à `http://localhost:8080/h2-console` et vérifiez :
- Si les tables existent
- Si les Users existent
- Si les Étudiants ont des `USER_ID` non NULL

### 4. Si les Étudiants n'ont pas de User

Si vous voyez dans H2 que les étudiants ont `USER_ID = NULL`, alors :

**Option A : Supprimer la base et redémarrer**
```bash
cd backend
rm -rf data/
mvn spring-boot:run
```

**Option B : Créer manuellement les Users via H2 Console**

Dans H2 Console, exécutez :
```sql
-- Pour chaque étudiant sans User, créer un User
-- Exemple pour l'étudiant avec matricule MAT001

-- 1. Trouver l'ID de l'étudiant
SELECT ID FROM ETUDIANTS WHERE MATRICULE = 'MAT001';

-- 2. Créer un User (remplacez l'ID de l'étudiant)
INSERT INTO USERS (ID, LOGIN, PASSWORD, ROLES)
VALUES (
    'user-mat001',
    'mat001',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- mot de passe: mat001 (encodé)
    'ETUDIANT'
);

-- 3. Lier l'étudiant au User (remplacez les IDs)
UPDATE ETUDIANTS 
SET USER_ID = 'user-mat001' 
WHERE MATRICULE = 'MAT001';
```

**Note :** Le mot de passe encodé ci-dessus est pour `mat001`. Pour générer un nouveau hash, vous pouvez utiliser un outil en ligne ou Spring Security.

## 📋 Checklist de Vérification

- [ ] Backend démarré sur le port 8080
- [ ] H2 Console accessible à `http://localhost:8080/h2-console`
- [ ] Tables `USERS`, `ETUDIANTS`, `FORMATEURS` existent
- [ ] Table `USERS` contient au moins l'admin (`login = 'admin'`)
- [ ] Tous les étudiants ont un `USER_ID` non NULL
- [ ] Tous les formateurs ont un `USER_ID` non NULL
- [ ] Les logins des Users correspondent aux matricules (pour étudiants) ou emails (pour formateurs)

