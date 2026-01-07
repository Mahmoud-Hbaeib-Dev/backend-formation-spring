# Centre de Formation - Backend Application

Application Spring Boot pour la gestion d'un centre de formation.

## Technologies utilisées

- Spring Boot 3.2.0
- Spring Security (JWT + Session-based)
- Spring Data JPA
- Thymeleaf (SSR)
- MySQL / H2 Database
- Maven

## Structure du projet

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/formation/app/
│   │   │   ├── entity/          # Entités JPA
│   │   │   ├── repository/      # Repositories Spring Data
│   │   │   ├── service/         # Services métier
│   │   │   ├── controller/      # Controllers
│   │   │   │   ├── api/         # REST Controllers
│   │   │   │   └── web/         # Thymeleaf Controllers
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── config/          # Configuration classes
│   │   │   ├── security/        # Spring Security config
│   │   │   ├── exception/       # Exception handlers
│   │   │   └── util/            # Utilitaires
│   │   └── resources/
│   │       ├── templates/        # Thymeleaf templates
│   │       └── static/          # Static resources (CSS, JS)
│   └── test/                    # Tests
└── pom.xml
```

## Prérequis

- Java 17 ou supérieur
- Maven 3.6+
- MySQL 8+ (pour production)
- IDE (IntelliJ IDEA, Eclipse, VS Code)

## Installation

1. Cloner le projet
2. Installer les dépendances Maven :
   ```bash
   mvn clean install
   ```

## Exécution

### Mode développement (H2)
```bash
mvn spring-boot:run
```

L'application sera accessible sur : http://localhost:8080

### Mode production (MySQL)
1. Créer la base de données MySQL
2. Configurer `application-prod.properties`
3. Lancer avec le profil prod :
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=prod
   ```

## Accès H2 Console (dev)

URL : http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:formationdb`
- Username: `sa`
- Password: (vide)

## Profils disponibles

- `dev` : H2 en mémoire (par défaut)
- `prod` : MySQL

## Documentation API

L'API REST sera disponible sur `/api/**`
L'interface admin (Thymeleaf) sera disponible sur `/admin/**`

