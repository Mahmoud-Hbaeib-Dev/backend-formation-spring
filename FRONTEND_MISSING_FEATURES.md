# 📋 Fonctionnalités Manquantes dans le Frontend React

## ✅ Note Importante

**L'interface Admin utilise Thymeleaf (templates HTML côté serveur)** et est **COMPLÈTE** :
- ✅ Dashboard Admin (`/admin/dashboard`)
- ✅ CRUD Étudiants (`/admin/etudiants`)
- ✅ CRUD Formateurs (`/admin/formateurs`)
- ✅ CRUD Cours (`/admin/cours`)
- ✅ CRUD Sessions (`/admin/sessions`)
- ✅ CRUD Groupes (`/admin/groupes`)
- ✅ CRUD Séances (`/admin/seances`)
- ✅ CRUD Notes (`/admin/notes`)
- ✅ CRUD Inscriptions (`/admin/inscriptions`)
- ✅ Statistiques (`/admin/statistiques`)

**Ce document se concentre uniquement sur le Frontend React (Formateurs et Étudiants).**

---

## 🔍 Comparaison Backend REST API vs Frontend React

### 1. 📅 SÉANCES (Formateur)

#### ✅ Implémenté
- ✅ **Créer une séance** (`CreateSeance.jsx`) - `POST /api/seances`
- ✅ **Voir les séances** (`Seances.jsx`) - `GET /api/seances/formateur/{id}`

#### ❌ Manque
- ❌ **Modifier une séance** - `PUT /api/seances/{id}`
  - **Action**: Ajouter un bouton "Modifier" dans `Seances.jsx`
  - **Page**: Créer `EditSeance.jsx` ou modal d'édition
  
- ❌ **Supprimer une séance** - `DELETE /api/seances/{id}`
  - **Action**: Ajouter un bouton "Supprimer" dans `Seances.jsx`
  - **Action**: Confirmation avant suppression

- ❌ **Filtre par date** - `GET /api/seances/date?date=...`
  - **Action**: Ajouter un sélecteur de date dans `Seances.jsx`

- ❌ **Filtre par période** - `GET /api/seances/date-between?dateDebut=...&dateFin=...`
  - **Action**: Ajouter un filtre de période dans `Seances.jsx`

---

### 2. 📝 NOTES (Formateur)

#### ✅ Implémenté
- ✅ **Attribuer une note** (`Notes.jsx`) - `POST /api/notes`
- ✅ **Voir les notes d'un cours** (`Notes.jsx`) - `GET /api/notes/cours/{code}`

#### ❌ Manque
- ❌ **Modifier une note** - `PUT /api/notes/{id}`
  - **Action**: Ajouter un bouton "Modifier" dans le tableau des notes
  - **Action**: Modal ou page pour éditer la note

---

### 3. 📚 COURS (Formateur)

#### ✅ Implémenté
- ✅ **Voir les cours** (`Cours.jsx`) - `GET /api/formateurs/{id}/cours`
- ✅ **Détails d'un cours** (`CoursDetails.jsx`) - `GET /api/cours/{code}`
- ✅ **Statistiques d'un cours** (`CoursDetails.jsx`) - `GET /api/cours/{code}/statistiques`

#### ❌ Manque
- ❌ **Créer un cours** - `POST /api/cours`
  - **Note**: Peut être réservé à l'admin, mais l'API le permet pour FORMATEUR
  - **Action**: Créer `CreateCours.jsx` si nécessaire

- ❌ **Modifier un cours** - `PUT /api/cours/{code}`
  - **Note**: Peut être réservé à l'admin, mais l'API le permet pour FORMATEUR
  - **Action**: Ajouter un bouton "Modifier" dans `CoursDetails.jsx`

- ❌ **Supprimer un cours** - `DELETE /api/cours/{code}`
  - **Note**: Réservé à ADMIN uniquement (normal)

- ❌ **Recherche de cours** - `GET /api/cours/search/titre?titre=...`
  - **Action**: Ajouter une barre de recherche dans `Cours.jsx`

---

### 4. 🎓 INSCRIPTIONS (Étudiant)

#### ✅ Implémenté
- ✅ **S'inscrire à un cours** (`InscriptionCours.jsx`) - `POST /api/inscriptions`
- ✅ **Voir ses inscriptions** (`InscriptionCours.jsx`) - `GET /api/inscriptions/etudiant/{id}`

#### ❌ Manque
- ❌ **Se désinscrire d'un cours** - `DELETE /api/inscriptions/{id}`
  - **Action**: Ajouter un bouton "Se désinscrire" dans la liste des inscriptions
  - **Problème**: L'endpoint nécessite l'ID de l'inscription, pas le code du cours
  - **Solution**: Stocker l'ID de l'inscription dans la liste affichée

---

### 5. 🔍 RECHERCHE ET FILTRES (Général)

#### ❌ Manque
- ❌ **Recherche d'étudiants par nom** - `GET /api/etudiants/search/nom?nom=...`
  - **Utilisation**: Dans la page de gestion des notes (Formateur)
  - **Action**: Ajouter une barre de recherche dans `Notes.jsx`

- ❌ **Recherche de cours par titre** - `GET /api/cours/search/titre?titre=...`
  - **Utilisation**: Dans `Cours.jsx` (Formateur) et `InscriptionCours.jsx` (Étudiant)
  - **Action**: Ajouter une barre de recherche

- ❌ **Filtre par spécialité** (Formateurs) - `GET /api/formateurs/specialite/{specialite}`
  - **Utilisation**: Peut être utile pour filtrer les formateurs

---

### 6. 👤 PROFIL UTILISATEUR (Général)

#### ❌ Manque
- ❌ **Page de profil** pour Formateur et Étudiant
  - **Backend disponible**: `PUT /api/formateurs/{id}` et `PUT /api/etudiants/{id}`
  - **Action**: Créer `Profile.jsx` pour chaque rôle
  - **Fonctionnalités**: Voir et modifier ses informations personnelles

---

### 7. 📊 STATISTIQUES AVANCÉES

#### ✅ Implémenté
- ✅ **Statistiques d'un cours** (`CoursDetails.jsx`) - `GET /api/cours/{code}/statistiques`
- ✅ **Statistiques générales** (`Statistiques.jsx`) - `GET /api/statistiques/dashboard`

#### ❌ Manque (Optionnel)
- ❌ **Cours les plus suivis** - `GET /api/statistiques/cours-plus-suivis`
  - **Action**: Ajouter dans `Statistiques.jsx` (Formateur)

- ❌ **Moyenne générale d'un étudiant** - `GET /api/etudiants/{id}/moyenne`
  - **Action**: Afficher dans le Dashboard Étudiant

---

## 🎯 Priorités d'Implémentation

### 🔴 PRIORITÉ HAUTE

1. **Modifier une séance** (Formateur)
   - Bouton "Modifier" dans `Seances.jsx`
   - Page/modal `EditSeance.jsx`

2. **Supprimer une séance** (Formateur)
   - Bouton "Supprimer" avec confirmation dans `Seances.jsx`

3. **Modifier une note** (Formateur)
   - Bouton "Modifier" dans le tableau des notes (`Notes.jsx`)
   - Modal ou page d'édition

4. **Se désinscrire d'un cours** (Étudiant)
   - Bouton "Se désinscrire" dans `InscriptionCours.jsx`
   - **Important**: S'assurer que l'ID de l'inscription est disponible

### 🟡 PRIORITÉ MOYENNE

5. **Filtres par date pour séances** (Formateur)
   - Sélecteur de date dans `Seances.jsx`
   - Filtre par période (date début - date fin)

6. **Recherche de cours** (Formateur et Étudiant)
   - Barre de recherche dans `Cours.jsx` et `InscriptionCours.jsx`

7. **Page de profil utilisateur**
   - `Profile.jsx` pour Formateur et Étudiant

### 🟢 PRIORITÉ BASSE

8. **Créer/Modifier un cours** (Formateur)
   - Si nécessaire (peut être réservé à l'admin)

9. **Recherche d'étudiants** (Formateur)
   - Dans la page de gestion des notes

10. **Statistiques avancées**
    - Cours les plus suivis
    - Moyenne générale étudiant

---

## 📝 Résumé des Endpoints REST Non Utilisés

### Séances
- `PUT /api/seances/{id}` - Modifier une séance
- `DELETE /api/seances/{id}` - Supprimer une séance
- `GET /api/seances/date?date=...` - Par date
- `GET /api/seances/date-between?dateDebut=...&dateFin=...` - Par période

### Notes
- `PUT /api/notes/{id}` - Modifier une note

### Inscriptions
- `DELETE /api/inscriptions/{id}` - Désinscription

### Cours
- `POST /api/cours` - Créer un cours (si nécessaire)
- `PUT /api/cours/{code}` - Modifier un cours (si nécessaire)
- `GET /api/cours/search/titre?titre=...` - Recherche par titre

### Recherche
- `GET /api/etudiants/search/nom?nom=...` - Recherche d'étudiants
- `GET /api/formateurs/specialite/{specialite}` - Par spécialité

### Profil
- `PUT /api/formateurs/{id}` - Modifier profil formateur
- `PUT /api/etudiants/{id}` - Modifier profil étudiant

### Statistiques
- `GET /api/statistiques/cours-plus-suivis` - Cours les plus suivis
- `GET /api/etudiants/{id}/moyenne` - Moyenne générale

---

## 🚀 Plan d'Action Recommandé

### Phase 1 (Essentiel)
1. Modifier/Supprimer séances
2. Modifier notes
3. Désinscription étudiant

### Phase 2 (Important)
4. Filtres par date
5. Recherche de cours
6. Page de profil

### Phase 3 (Amélioration)
7. Statistiques avancées
8. Recherche d'étudiants
9. Création/Modification cours (si nécessaire)

