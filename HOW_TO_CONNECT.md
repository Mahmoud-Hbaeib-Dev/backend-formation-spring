# 🔐 Comment se connecter - Guide d'authentification

Ce document explique comment les différents utilisateurs (Admin, Formateur, Étudiant) peuvent se connecter à l'application.

## 📋 Vue d'ensemble

L'application utilise un système d'authentification avec **JWT** pour le frontend React et **Session** pour l'interface admin Thymeleaf.

## 👤 Types d'utilisateurs

### 1. **ADMIN**
- Accès à l'interface admin Thymeleaf (`/admin/**`)
- Peut créer des étudiants et formateurs
- Peut gérer tous les aspects du système

### 2. **FORMATEUR**
- Accès au frontend React (`/formateur/**`)
- Peut gérer ses cours, séances et notes
- Peut consulter les statistiques

### 3. **ETUDIANT**
- Accès au frontend React (`/etudiant/**`)
- Peut consulter ses cours, notes et planning
- Peut s'inscrire aux cours disponibles

## 🔑 Création des comptes

### Processus automatique

Lorsqu'un **ADMIN** crée un étudiant ou un formateur via l'interface admin, un compte User est **automatiquement créé** avec :

#### Pour les Étudiants :
- **Login** : Le matricule en minuscules (ex: `mat001`)
- **Password** : Le matricule en minuscules (ex: `mat001`)
- **Rôle** : `ETUDIANT`
- **Connexion possible avec** : Login (`mat001`) OU Email (`ahmed@email.com`)

#### Pour les Formateurs :
- **Login** : La partie avant `@` de l'email (ex: `dupont` pour `dupont@formation.com`)
- **Password** : Identique au login (ex: `dupont`)
- **Rôle** : `FORMATEUR`
- **Connexion possible avec** : Login (`dupont`) OU Email (`dupont@formation.com`)

### Exemple de création

1. **Admin se connecte** à `/admin/dashboard`
2. **Admin crée un étudiant** :
   - Matricule: `MAT001`
   - Nom: `Ben Ali`
   - Prénom: `Ahmed`
   - Email: `ahmed@email.com`
3. **Système crée automatiquement** :
   - User avec login: `mat001` et password: `mat001`
   - L'étudiant peut maintenant se connecter au frontend React

## 🚀 Connexion

### Frontend React (Formateurs et Étudiants)

1. **Accéder à** : `http://localhost:3000/login`
2. **Entrer les credentials** :
   - **Pour un étudiant** :
     - **Option 1** : Login = matricule (ex: `mat001`), Password = matricule (ex: `mat001`)
     - **Option 2** : Email (ex: `ahmed@email.com`), Password = matricule (ex: `mat001`)
   - **Pour un formateur** :
     - **Option 1** : Login = partie avant @ de l'email (ex: `dupont`), Password = login (ex: `dupont`)
     - **Option 2** : Email (ex: `dupont@formation.com`), Password = login (ex: `dupont`)
3. **Redirection automatique** selon le rôle :
   - Étudiant → `/etudiant/dashboard`
   - Formateur → `/formateur/dashboard`

### Interface Admin (Admin uniquement)

1. **Accéder à** : `http://localhost:8080/login`
2. **Entrer les credentials** :
   - Login: `admin`
   - Password: `admin`
3. **Redirection vers** : `/admin/dashboard`

## 📝 Credentials par défaut (Données de test)

### Admin
- **Login** : `admin`
- **Password** : `admin`
- **Rôle** : `ADMIN`

### Formateurs (créés automatiquement au démarrage)
- **Formateur 1** :
  - Login: `dupont` OU Email: `dupont@formation.com`
  - Password: `dupont`
  
- **Formateur 2** :
  - Login: `martin` OU Email: `martin@formation.com`
  - Password: `martin`
  
- **Formateur 3** :
  - Login: `bernard` OU Email: `bernard@formation.com`
  - Password: `bernard`

### Étudiants (créés automatiquement au démarrage)
- **Étudiant 1** :
  - Login: `mat001` OU Email: `ahmed@email.com`
  - Password: `mat001`
  - Matricule: `MAT001`
  
- **Étudiant 2** :
  - Login: `mat002` OU Email: `fatma@email.com`
  - Password: `mat002`
  - Matricule: `MAT002`
  
- **Étudiant 3** :
  - Login: `mat003` OU Email: `mohamed@email.com`
  - Password: `mat003`
  - Matricule: `MAT003`
  
- **Étudiant 4** :
  - Login: `mat004` OU Email: `sana@email.com`
  - Password: `mat004`
  - Matricule: `MAT004`

## 🔄 Workflow complet

### Scénario 1 : Nouvel étudiant

1. **Admin** se connecte à `/admin/dashboard`
2. **Admin** va dans "Gestion des Étudiants" → "Nouvel Étudiant"
3. **Admin** remplit le formulaire :
   - Matricule: `MAT005`
   - Nom: `Nouveau`
   - Prénom: `Étudiant`
   - Email: `nouveau@email.com`
4. **Système crée automatiquement** :
   - L'étudiant dans la base de données
   - Un User avec login: `mat005` et password: `mat005`
5. **L'étudiant peut maintenant se connecter** au frontend React avec :
   - **Option 1** : Login: `mat005`, Password: `mat005`
   - **Option 2** : Email: `nouveau@email.com`, Password: `mat005`

### Scénario 2 : Nouveau formateur

1. **Admin** se connecte à `/admin/dashboard`
2. **Admin** va dans "Gestion des Formateurs" → "Nouveau Formateur"
3. **Admin** remplit le formulaire :
   - Nom: `Nouveau`
   - Spécialité: `React`
   - Email: `nouveau@formation.com`
4. **Système crée automatiquement** :
   - Le formateur dans la base de données
   - Un User avec login: `nouveau` et password: `nouveau`
5. **Le formateur peut maintenant se connecter** au frontend React avec :
   - **Option 1** : Login: `nouveau`, Password: `nouveau`
   - **Option 2** : Email: `nouveau@formation.com`, Password: `nouveau`

## ⚠️ Important

- **Les mots de passe par défaut sont simples** (matricule ou login)
- **En production**, il est recommandé de :
  1. Forcer le changement de mot de passe au premier login
  2. Envoyer les credentials par email sécurisé
  3. Utiliser des mots de passe plus complexes

## 🔧 Modification des mots de passe

Actuellement, les mots de passe peuvent être modifiés via :
- L'API REST (endpoint à implémenter si nécessaire)
- Directement dans la base de données (non recommandé)

## 📞 Support

Si un utilisateur ne peut pas se connecter :
1. Vérifier que l'utilisateur existe dans la base de données
2. Vérifier qu'un User est associé à l'étudiant/formateur
3. Vérifier les credentials (login = matricule pour étudiants, login = partie avant @ pour formateurs)
4. Contacter l'admin pour réinitialiser le mot de passe

