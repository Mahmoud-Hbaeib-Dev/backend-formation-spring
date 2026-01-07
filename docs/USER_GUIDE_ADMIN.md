# 👨‍💼 Guide d'utilisation - Administrateur (ADMIN)

Guide complet pour les administrateurs du Centre de Formation.

## 📋 Table des matières

- [Connexion](#connexion)
- [Dashboard](#dashboard)
- [Gestion des étudiants](#gestion-des-étudiants)
- [Gestion des formateurs](#gestion-des-formateurs)
- [Gestion des cours](#gestion-des-cours)
- [Gestion des inscriptions](#gestion-des-inscriptions)
- [Gestion des séances](#gestion-des-séances)
- [Gestion des notes](#gestion-des-notes)
- [Gestion des sessions](#gestion-des-sessions)
- [Gestion des groupes](#gestion-des-groupes)
- [Planning](#planning)
- [Statistiques](#statistiques)

## 🔐 Connexion

1. Accédez à l'URL : `http://localhost:8080/login`
2. Entrez vos identifiants :
   - **Login** : `admin`
   - **Password** : `admin` (par défaut)
3. Cliquez sur **"Se connecter"**

> ⚠️ **Important** : Changez le mot de passe par défaut après la première connexion !

## 📊 Dashboard

Le dashboard affiche un aperçu général du centre de formation :

- **Statistiques globales** :
  - Nombre total d'étudiants
  - Nombre total de formateurs
  - Nombre total de cours
  - Nombre d'inscriptions actives

- **Statistiques par cours** :
  - Nombre d'inscriptions par cours
  - Formateur assigné
  - Session

### Accès au dashboard

URL : `http://localhost:8080/admin/dashboard`

## 👥 Gestion des étudiants

### Liste des étudiants

**URL** : `/admin/etudiants`

**Actions disponibles** :
- ✅ Voir la liste complète des étudiants
- ✅ Rechercher un étudiant (par nom, prénom, matricule)
- ✅ Voir les détails d'un étudiant
- ✅ Créer un nouvel étudiant
- ✅ Modifier un étudiant existant
- ✅ Supprimer un étudiant

### Créer un étudiant

1. Cliquez sur **"Nouvel étudiant"**
2. Remplissez le formulaire :
   - **Matricule** : Identifiant unique (ex: ETU001)
   - **Nom** : Nom de famille
   - **Prénom** : Prénom
   - **Email** : Adresse email valide
   - **Date d'inscription** : Date d'inscription au centre
3. Cliquez sur **"Enregistrer"**

> ⚠️ Le matricule doit être unique !

### Modifier un étudiant

1. Cliquez sur **"Modifier"** dans la ligne de l'étudiant
2. Modifiez les champs souhaités
3. Cliquez sur **"Enregistrer"**

### Voir les détails d'un étudiant

Cliquez sur **"Détails"** pour voir :
- Informations personnelles
- Liste des inscriptions aux cours
- Notes obtenues
- Emploi du temps

## 👨‍🏫 Gestion des formateurs

### Liste des formateurs

**URL** : `/admin/formateurs`

**Actions disponibles** :
- ✅ Voir la liste complète des formateurs
- ✅ Rechercher un formateur (par nom, spécialité)
- ✅ Créer un nouveau formateur
- ✅ Modifier un formateur
- ✅ Supprimer un formateur
- ✅ Voir les cours assignés à un formateur

### Créer un formateur

1. Cliquez sur **"Nouveau formateur"**
2. Remplissez le formulaire :
   - **Nom** : Nom complet
   - **Spécialité** : Domaine d'expertise (ex: Java, Spring Boot, BDD)
   - **Email** : Adresse email
3. Cliquez sur **"Enregistrer"**

## 📚 Gestion des cours

### Liste des cours

**URL** : `/admin/cours`

**Actions disponibles** :
- ✅ Voir tous les cours
- ✅ Rechercher un cours (par titre, code)
- ✅ Créer un nouveau cours
- ✅ Modifier un cours
- ✅ Supprimer un cours
- ✅ Assigner un formateur
- ✅ Assigner des groupes

### Créer un cours

1. Cliquez sur **"Nouveau cours"**
2. Remplissez le formulaire :
   - **Code** : Code unique du cours (ex: JAVA101)
   - **Titre** : Titre du cours
   - **Description** : Description détaillée
   - **Formateur** : Sélectionnez un formateur
   - **Session** : Sélectionnez une session (S1 ou S2)
   - **Groupes** : Sélectionnez les groupes concernés
3. Cliquez sur **"Enregistrer"**

> ⚠️ Le code du cours doit être unique !

## 📝 Gestion des inscriptions

### Liste des inscriptions

**URL** : `/admin/inscriptions`

**Actions disponibles** :
- ✅ Voir toutes les inscriptions
- ✅ Inscrire un étudiant à un cours
- ✅ Désinscrire un étudiant
- ✅ Voir les inscriptions par cours
- ✅ Voir les inscriptions par étudiant

### Inscrire un étudiant

1. Cliquez sur **"Nouvelle inscription"**
2. Sélectionnez :
   - **Étudiant** : Choisissez l'étudiant
   - **Cours** : Choisissez le cours
3. Cliquez sur **"Enregistrer"**

> ⚠️ L'étudiant ne peut pas être inscrit deux fois au même cours !

## 📅 Gestion des séances

### Liste des séances

**URL** : `/admin/seances`

**Actions disponibles** :
- ✅ Voir toutes les séances
- ✅ Créer une nouvelle séance
- ✅ Modifier une séance
- ✅ Supprimer une séance
- ✅ Vérifier les conflits d'horaires

### Créer une séance

1. Cliquez sur **"Nouvelle séance"**
2. Remplissez le formulaire :
   - **Cours** : Sélectionnez le cours
   - **Formateur** : Sélectionnez le formateur
   - **Date** : Date de la séance
   - **Heure** : Heure de début (format HH:mm)
   - **Salle** : Numéro de salle
3. Cliquez sur **"Enregistrer"**

> ⚠️ Le système vérifie automatiquement les conflits d'horaires !

## 📊 Gestion des notes

### Liste des notes

**URL** : `/admin/notes`

**Actions disponibles** :
- ✅ Voir toutes les notes
- ✅ Attribuer une note
- ✅ Modifier une note
- ✅ Voir les notes par étudiant
- ✅ Voir les notes par cours

### Attribuer une note

1. Cliquez sur **"Nouvelle note"**
2. Sélectionnez :
   - **Étudiant** : Choisissez l'étudiant
   - **Cours** : Choisissez le cours
   - **Valeur** : Note entre 0 et 20
3. Cliquez sur **"Enregistrer"**

> ⚠️ La note doit être entre 0 et 20 !

## 🎓 Gestion des sessions

### Liste des sessions

**URL** : `/admin/sessions`

**Actions disponibles** :
- ✅ Voir toutes les sessions
- ✅ Créer une nouvelle session
- ✅ Modifier une session
- ✅ Voir les cours d'une session

### Créer une session

1. Cliquez sur **"Nouvelle session"**
2. Remplissez le formulaire :
   - **Semestre** : S1 ou S2
   - **Année scolaire** : Format AAAA-AAAA (ex: 2024-2025)
3. Cliquez sur **"Enregistrer"**

## 👥 Gestion des groupes

### Liste des groupes

**URL** : `/admin/groupes`

**Actions disponibles** :
- ✅ Voir tous les groupes
- ✅ Créer un nouveau groupe
- ✅ Modifier un groupe
- ✅ Supprimer un groupe
- ✅ Voir les cours d'un groupe

### Créer un groupe

1. Cliquez sur **"Nouveau groupe"**
2. Entrez le **nom du groupe** (ex: Groupe A, Groupe B)
3. Cliquez sur **"Enregistrer"**

## 📅 Planning

### Vue globale du planning

**URL** : `/admin/planning`

Affiche toutes les séances programmées avec :
- Date et heure
- Cours
- Formateur
- Salle

### Planning par étudiant

**URL** : `/admin/planning/etudiant/{id}`

Affiche l'emploi du temps d'un étudiant spécifique.

### Planning par formateur

**URL** : `/admin/planning/formateur/{id}`

Affiche l'emploi du temps d'un formateur spécifique.

## 📈 Statistiques

### Dashboard statistiques

**URL** : `/admin/statistiques`

Affiche :
- Statistiques globales
- Répartition des notes
- Taux d'inscription par cours
- Statistiques par session

### Statistiques par cours

**URL** : `/admin/statistiques/cours/{code}`

Affiche les statistiques détaillées d'un cours :
- Nombre d'inscriptions
- Moyenne des notes
- Répartition des notes

## 🔒 Sécurité

### Changer le mot de passe

> ⚠️ **À implémenter** : Fonctionnalité de changement de mot de passe

### Déconnexion

Cliquez sur **"Déconnexion"** dans le menu pour vous déconnecter.

## ❓ Aide et support

Pour toute question ou problème :
1. Consultez les logs de l'application
2. Vérifiez la documentation technique
3. Contactez l'équipe de développement

---

**Version** : 1.0.0  
**Dernière mise à jour** : 2025

