# 🚀 Guide de Déploiement

Guide complet pour déployer l'application Centre de Formation en production.

## 📋 Table des matières

- [Prérequis](#prérequis)
- [Configuration MySQL](#configuration-mysql)
- [Build de l'application](#build-de-lapplication)
- [Déploiement](#déploiement)
- [Configuration de production](#configuration-de-production)
- [Monitoring](#monitoring)
- [Sauvegarde](#sauvegarde)

## 🔧 Prérequis

### Serveur

- **OS** : Linux (Ubuntu 20.04+ recommandé) ou Windows Server
- **Java** : OpenJDK 17 ou Oracle JDK 17
- **MySQL** : Version 8.0 ou supérieure
- **Mémoire** : Minimum 2GB RAM
- **Disque** : Minimum 10GB d'espace libre

### Vérification

```bash
# Vérifier Java
java -version

# Vérifier MySQL
mysql --version

# Vérifier Maven (pour build)
mvn -version
```

## 🗄️ Configuration MySQL

### 1. Créer la base de données

```sql
CREATE DATABASE formationdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Créer un utilisateur dédié

```sql
CREATE USER 'formation_user'@'localhost' IDENTIFIED BY 'strong_password_here';
GRANT ALL PRIVILEGES ON formationdb.* TO 'formation_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Tester la connexion

```bash
mysql -u formation_user -p formationdb
```

## 🔨 Build de l'application

### 1. Cloner et compiler

```bash
cd backend
mvn clean package -DskipTests
```

Le JAR sera généré dans `target/centre-formation-app-1.0.0.jar`

### 2. Vérifier le JAR

```bash
ls -lh target/*.jar
```

## 🚀 Déploiement

### Option 1 : Exécution directe

#### 1. Copier le JAR

```bash
scp target/centre-formation-app-1.0.0.jar user@server:/opt/formation/
```

#### 2. Créer le fichier de configuration

Sur le serveur, créer `/opt/formation/application-prod.properties` :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/formationdb
spring.datasource.username=formation_user
spring.datasource.password=strong_password_here
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

server.port=8080
logging.level.com.formation.app=INFO
```

#### 3. Lancer l'application

```bash
cd /opt/formation
java -jar -Dspring.profiles.active=prod centre-formation-app-1.0.0.jar
```

### Option 2 : Service systemd (Linux)

#### 1. Créer le service

Créer `/etc/systemd/system/formation-app.service` :

```ini
[Unit]
Description=Centre de Formation Application
After=network.target mysql.service

[Service]
Type=simple
User=formation
WorkingDirectory=/opt/formation
ExecStart=/usr/bin/java -jar -Dspring.profiles.active=prod /opt/formation/centre-formation-app-1.0.0.jar
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
```

#### 2. Activer et démarrer

```bash
sudo systemctl daemon-reload
sudo systemctl enable formation-app
sudo systemctl start formation-app
sudo systemctl status formation-app
```

### Option 3 : Docker

#### 1. Créer Dockerfile

```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/centre-formation-app-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
```

#### 2. Build et run

```bash
docker build -t formation-app .
docker run -d -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql-host:3306/formationdb \
  -e SPRING_DATASOURCE_USERNAME=formation_user \
  -e SPRING_DATASOURCE_PASSWORD=password \
  formation-app
```

## ⚙️ Configuration de production

### Variables d'environnement recommandées

```bash
export SPRING_PROFILES_ACTIVE=prod
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/formationdb
export SPRING_DATASOURCE_USERNAME=formation_user
export SPRING_DATASOURCE_PASSWORD=secure_password
export SERVER_PORT=8080
export JWT_SECRET=your-secret-key-min-256-bits
```

### Sécurité

1. **Changer le mot de passe admin par défaut** après le premier démarrage
2. **Désactiver H2 Console** en production
3. **Configurer HTTPS** avec un reverse proxy (Nginx)
4. **Limiter l'accès** aux endpoints Actuator

### Nginx Reverse Proxy (optionnel)

Configuration exemple `/etc/nginx/sites-available/formation` :

```nginx
server {
    listen 80;
    server_name formation.example.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

## 📊 Monitoring

### Health Check

L'application expose des endpoints Actuator :

- **Health** : `http://localhost:8080/actuator/health`
- **Info** : `http://localhost:8080/actuator/info`
- **Metrics** : `http://localhost:8080/actuator/metrics`

### Logs

Les logs sont écrits dans :
- **Console** : Sortie standard
- **Fichier** : Configurer dans `logback-spring.xml` (optionnel)

### Monitoring avec Prometheus (optionnel)

Ajouter la dépendance dans `pom.xml` :

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

## 💾 Sauvegarde

### Base de données

#### Script de sauvegarde automatique

Créer `/opt/formation/backup.sh` :

```bash
#!/bin/bash
BACKUP_DIR="/opt/formation/backups"
DATE=$(date +%Y%m%d_%H%M%S)
mkdir -p $BACKUP_DIR

mysqldump -u formation_user -p'password' formationdb > $BACKUP_DIR/formationdb_$DATE.sql

# Garder seulement les 7 derniers backups
find $BACKUP_DIR -name "formationdb_*.sql" -mtime +7 -delete
```

#### Cron job (sauvegarde quotidienne)

```bash
0 2 * * * /opt/formation/backup.sh
```

### Restauration

```bash
mysql -u formation_user -p formationdb < backup_file.sql
```

## 🔄 Mise à jour

### Processus de mise à jour

1. **Sauvegarder la base de données**
2. **Arrêter l'application**
3. **Remplacer le JAR**
4. **Redémarrer l'application**
5. **Vérifier les logs**

```bash
# Arrêter
sudo systemctl stop formation-app

# Backup DB
mysqldump -u formation_user -p formationdb > backup_$(date +%Y%m%d).sql

# Remplacer JAR
cp new-version.jar /opt/formation/centre-formation-app-1.0.0.jar

# Redémarrer
sudo systemctl start formation-app

# Vérifier
sudo systemctl status formation-app
tail -f /var/log/formation-app.log
```

## 🐛 Dépannage

### L'application ne démarre pas

1. Vérifier les logs : `journalctl -u formation-app -n 50`
2. Vérifier la connexion MySQL
3. Vérifier les permissions sur le JAR
4. Vérifier les ports disponibles : `netstat -tulpn | grep 8080`

### Erreurs de connexion MySQL

1. Vérifier que MySQL est démarré : `sudo systemctl status mysql`
2. Vérifier les credentials dans la configuration
3. Vérifier les permissions utilisateur MySQL

### Performance

1. Vérifier la mémoire : `free -h`
2. Vérifier les logs pour les requêtes lentes
3. Optimiser les requêtes JPA si nécessaire

## 📞 Support

Pour toute question ou problème :
1. Consulter les logs de l'application
2. Vérifier la documentation
3. Consulter les issues GitHub (si applicable)

---

**Note** : Ce guide est une base. Adaptez-le selon votre environnement spécifique.

