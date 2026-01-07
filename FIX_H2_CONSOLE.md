# 🔧 Correction de l'Accès à H2 Console

## ❌ Problème

Erreur : `"No static resource h2-console"` ou `403 Forbidden`

## ✅ Solution

J'ai modifié la configuration de sécurité pour permettre l'accès à H2 Console.

## 🚀 Action Requise

**Redémarrer le backend** pour appliquer les changements :

```bash
cd backend
# Arrêter le backend (Ctrl+C si en cours d'exécution)
mvn spring-boot:run
```

## 📋 Après Redémarrage

### 1. Accéder à H2 Console

Ouvrez votre navigateur et allez à :
```
http://localhost:8080/h2-console
```

### 2. Se Connecter

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

### 3. Vérifier les Données

Une fois connecté, exécutez ces requêtes SQL :

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

#### Compter les Étudiants SANS User
```sql
SELECT COUNT(*) as ETUDIANTS_SANS_USER
FROM ETUDIANTS 
WHERE USER_ID IS NULL;
```

## 🔍 Si H2 Console ne fonctionne toujours pas

### Vérifier que H2 Console est activée

Dans `application-dev.properties`, assurez-vous d'avoir :
```properties
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

### Vérifier les logs du backend

Regardez les logs au démarrage. Vous devriez voir des messages indiquant que H2 Console est disponible.

### Alternative : Vérifier via les logs SQL

Si H2 Console ne fonctionne toujours pas, vous pouvez voir les requêtes SQL dans les logs du backend (car `spring.jpa.show-sql=true` est activé).

## 📝 Notes

- H2 Console est **uniquement disponible en mode développement** (profil `dev`)
- En production (profil `prod`), H2 Console est désactivée
- Les données H2 sont stockées dans `backend/data/formationdb.mv.db`

