# ============================================================================
# Dockerfile pour Centre de Formation Application
# ============================================================================

# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copier les fichiers de configuration Maven
COPY pom.xml .

# Télécharger les dépendances (cache layer)
RUN mvn dependency:go-offline -B

# Copier le code source
COPY src ./src

# Build de l'application
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Créer le répertoire pour les logs avant de changer d'utilisateur
RUN mkdir -p /app/logs && chmod 777 /app/logs

# Créer un utilisateur non-root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copier le JAR depuis le stage de build
COPY --from=build /app/target/centre-formation-app-*.jar app.jar

# Exposer le port
EXPOSE 8080

# Variables d'environnement par défaut
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Point d'entrée
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]


