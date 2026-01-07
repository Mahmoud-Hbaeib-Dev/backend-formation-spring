# 📊 EMPLACEMENT DE LA BASE DE DONNÉES

## 🔍 Où se trouve votre base de données ?

### Mode Développement (H2) - ACTUEL

#### Option 1 : En mémoire (par défaut)
- **Emplacement** : RAM (mémoire)
- **Fichier** : Aucun
- **Persistance** : ❌ Données perdues au redémarrage
- **Configuration** : `jdbc:h2:mem:formationdb`

#### Option 2 : Fichier (recommandé pour dev)
- **Emplacement** : `backend/data/formationdb.mv.db`
- **Fichier** : `formationdb.mv.db` dans le dossier `backend/data/`
- **Persistance** : ✅ Données conservées
- **Configuration** : `jdbc:h2:file:./data/formationdb`

### Mode Production (MySQL)
- **Emplacement** : Serveur MySQL
- **Fichier** : Géré par MySQL
- **Base de données** : `formation_db` (à créer)
- **Configuration** : `jdbc:mysql://localhost:3306/formation_db`

## 🛠️ Comment accéder à la base de données ?

### Console H2 (Développement)
1. Lancer l'application : `mvn spring-boot:run`
2. Ouvrir : `http://localhost:8080/h2-console`
3. Remplir :
   - **JDBC URL** : `jdbc:h2:file:./data/formationdb` (ou `jdbc:h2:mem:formationdb` pour mémoire)
   - **Username** : `sa`
   - **Password** : (vide)
4. Cliquer sur "Connect"

### MySQL (Production)
1. Installer MySQL
2. Créer la base : `CREATE DATABASE formation_db;`
3. Configurer `application-prod.properties`
4. Lancer avec profil prod : `mvn spring-boot:run -Dspring-boot.run.profiles=prod`

## 📁 Structure des fichiers

```
backend/
├── data/                    ← Base H2 (si fichier)
│   └── formationdb.mv.db   ← Fichier de base de données H2
├── src/main/resources/
│   ├── application.properties
│   ├── application-dev.properties  ← Configuration H2
│   └── application-prod.properties ← Configuration MySQL
```

## ⚙️ Changer le mode de stockage H2

### Pour utiliser un fichier (recommandé)
Dans `application-dev.properties` :
```properties
spring.datasource.url=jdbc:h2:file:./data/formationdb
```

### Pour utiliser la mémoire
Dans `application-dev.properties` :
```properties
spring.datasource.url=jdbc:h2:mem:formationdb
```

## 🔄 Migration vers MySQL

1. Installer MySQL
2. Créer la base de données :
   ```sql
   CREATE DATABASE formation_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
3. Configurer `application-prod.properties`
4. Lancer avec le profil prod

## 📝 Notes importantes

- **H2 en mémoire** : Parfait pour les tests, données perdues au redémarrage
- **H2 fichier** : Bon pour le développement, données persistantes
- **MySQL** : Nécessaire pour la production, plus robuste

