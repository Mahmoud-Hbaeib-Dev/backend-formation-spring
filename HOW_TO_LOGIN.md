# 🔐 COMMENT SE CONNECTER EN TANT QU'ADMIN

## ✅ Utilisateur ADMIN créé automatiquement

Lors du premier démarrage de l'application, un utilisateur ADMIN est créé automatiquement.

### 🔑 Identifiants de connexion

- **Login** : `admin`
- **Password** : `admin`

## 📝 Étapes pour se connecter

### 1. Lancer l'application
```bash
cd backend
mvn spring-boot:run
```

### 2. Accéder à la page de connexion
Ouvrir dans votre navigateur :
```
http://localhost:8080/login
```

### 3. Entrer les identifiants
- **Nom d'utilisateur** : `admin`
- **Mot de passe** : `admin`

### 4. Cliquer sur "Se connecter"

Vous serez redirigé vers le dashboard admin : `http://localhost:8080/admin/dashboard`

## 🗄️ Données de test créées automatiquement

Lors du premier démarrage, des données de test sont également créées :

### ✅ Utilisateurs
- **admin** (ADMIN) - Login: `admin`, Password: `admin`

### ✅ Sessions
- S1 2024-2025
- S2 2024-2025

### ✅ Formateurs
- Dupont (Java)
- Martin (Spring Boot)
- Bernard (Base de données)

### ✅ Cours
- Java Fondamentaux (JAVA001)
- Spring Boot Avancé (SPRING001)
- Bases de données (BDD001)

### ✅ Groupes
- Groupe A
- Groupe B

### ✅ Étudiants
- Ahmed Ben Ali (MAT001)
- Fatma Trabelsi (MAT002)
- Mohamed Khelifi (MAT003)
- Sana Amri (MAT004)

### ✅ Inscriptions
- Plusieurs inscriptions actives

### ✅ Séances
- Séances de cours programmées

### ✅ Notes
- Notes d'évaluation attribuées

## 🔍 Vérifier les données dans H2 Console

1. Accéder à : `http://localhost:8080/h2-console`
2. JDBC URL : `jdbc:h2:file:./data/formationdb`
3. Username : `sa`
4. Password : (vide)
5. Exécuter : `SELECT * FROM USERS;` pour voir l'admin

## ⚠️ Important

- L'utilisateur admin est créé **automatiquement** au premier démarrage
- Si vous supprimez la base de données (`backend/data/formationdb.mv.db`), relancez l'application pour recréer les données
- Les données de test sont créées à chaque démarrage si elles n'existent pas déjà

## 🎯 Après connexion

Une fois connecté, vous aurez accès à :
- Dashboard avec statistiques
- Gestion des étudiants
- Gestion des formateurs
- Gestion des cours
- Gestion des inscriptions
- Gestion des séances
- Gestion des notes

