# Centre de Formation - Frontend React

Frontend client-side (CSR) pour l'application de gestion de centre de formation.

## 🚀 Technologies

- **React 18** avec **Vite**
- **Tailwind CSS** pour le design
- **React Router** pour la navigation
- **Axios** pour les appels API
- **Lucide React** pour les icônes

## 📁 Structure du projet

```
frontend/
├── src/
│   ├── components/          # Composants réutilisables
│   │   ├── Layout.jsx      # Layout principal avec navbar/sidebar
│   │   └── ProtectedRoute.jsx  # Route protégée
│   ├── pages/              # Pages de l'application
│   │   ├── Login.jsx       # Page de connexion
│   │   ├── formateur/       # Pages formateur
│   │   │   ├── Dashboard.jsx
│   │   │   ├── Cours.jsx
│   │   │   ├── Seances.jsx
│   │   │   └── Notes.jsx
│   │   └── etudiant/        # Pages étudiant
│   │       ├── Dashboard.jsx
│   │       ├── Cours.jsx
│   │       ├── Notes.jsx
│   │       └── Planning.jsx
│   ├── services/           # Services API
│   │   ├── authService.js
│   │   ├── etudiantService.js
│   │   ├── formateurService.js
│   │   ├── coursService.js
│   │   ├── inscriptionService.js
│   │   ├── seanceService.js
│   │   └── noteService.js
│   ├── context/            # Context API
│   │   └── AuthContext.jsx
│   ├── utils/              # Utilitaires
│   │   ├── api.js          # Configuration Axios
│   │   └── auth.js         # Helpers authentification
│   ├── App.jsx             # Composant principal avec routes
│   ├── main.jsx            # Point d'entrée
│   └── index.css          # Styles Tailwind
├── public/
├── index.html
├── package.json
├── vite.config.js
├── tailwind.config.js
└── postcss.config.js
```

## 📦 Installation

```bash
cd frontend
npm install
```

## 🏃 Développement

```bash
npm run dev
```

L'application sera accessible sur : http://localhost:3000

## 🔧 Configuration

### Variables d'environnement

Créer un fichier `.env` :

```env
VITE_API_URL=http://localhost:8080/api
```

### Backend

Assurez-vous que le backend Spring Boot est démarré sur `http://localhost:8080`

## 🔐 Authentification

L'application utilise l'authentification JWT :

1. L'utilisateur se connecte via `/login`
2. Le token JWT est stocké dans `localStorage`
3. Le token est automatiquement ajouté aux requêtes API
4. Les routes sont protégées selon le rôle (FORMATEUR, ETUDIANT)

## 📱 Pages disponibles

### Formateur
- `/formateur/dashboard` - Dashboard avec statistiques
- `/formateur/cours` - Liste des cours assignés
- `/formateur/seances` - Planning des séances
- `/formateur/notes` - Gestion des notes

### Étudiant
- `/etudiant/dashboard` - Dashboard avec statistiques
- `/etudiant/cours` - Liste des cours inscrits
- `/etudiant/notes` - Consultation des notes
- `/etudiant/planning` - Emploi du temps

## 🎨 Design

L'interface utilise Tailwind CSS avec un design moderne et responsive :
- Couleurs primaires personnalisables
- Composants réutilisables
- Navigation intuitive avec sidebar
- Cards et tableaux stylisés

## 📝 Notes

- Tous les fichiers sont en `.jsx` (pas `.js`)
- Les composants utilisent les hooks React modernes
- L'authentification est gérée via Context API
- Les appels API sont centralisés dans les services
