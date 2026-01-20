import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

// Créer une instance axios avec configuration par défaut
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Intercepteur pour ajouter le token JWT à chaque requête
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Intercepteur pour gérer les erreurs de réponse
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response?.status === 401) {
      // Token expiré ou invalide
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      // Ne pas rediriger si on est déjà sur la page de login
      if (window.location.pathname !== '/login') {
        window.location.href = '/login';
      }
    }
    
    // Si c'est une erreur réseau (pas de réponse)
    if (!error.response) {
      // On pourrait afficher une notification utilisateur ici
    }
    
    return Promise.reject(error);
  }
);

/**
 * ============================
 *  Clients d'API organisés
 *  (basés sur les contrôleurs backend /api/**)
 * ============================
 */

// Authentification (/api/auth/**)
export const authApi = {
  login: (login, password) => api.post('/auth/login', { login, password }),
  me: () => api.get('/auth/me'),
  test: () => api.get('/auth/test'),
};

// Étudiants (/api/etudiants/**)
export const etudiantsApi = {
  list: (params) => api.get('/etudiants', { params }), // page, size (optionnels)
  getById: (id) => api.get(`/etudiants/${id}`),
  getByMatricule: (matricule) => api.get(`/etudiants/matricule/${matricule}`),
  create: (data) => api.post('/etudiants', data),
  update: (id, data) => api.put(`/etudiants/${id}`, data),
  remove: (id) => api.delete(`/etudiants/${id}`),
  searchByNom: (nom) => api.get('/etudiants/search/nom', { params: { nom } }),
  getInscriptions: (id) => api.get(`/etudiants/${id}/inscriptions`),
  getNotes: (id) => api.get(`/etudiants/${id}/notes`),
  getMoyenne: (id) => api.get(`/etudiants/${id}/moyenne`),
};

// Formateurs (/api/formateurs/**)
export const formateursApi = {
  list: () => api.get('/formateurs'),
  getById: (id) => api.get(`/formateurs/${id}`),
  create: (data) => api.post('/formateurs', data),
  update: (id, data) => api.put(`/formateurs/${id}`, data),
  remove: (id) => api.delete(`/formateurs/${id}`),
  getCours: (id) => api.get(`/formateurs/${id}/cours`),
  getBySpecialite: (specialite) => api.get(`/formateurs/specialite/${specialite}`),
};

// Cours (/api/cours/**)
export const coursApi = {
  list: () => api.get('/cours'),
  getByCode: (code) => api.get(`/cours/${code}`),
  create: (data) => api.post('/cours', data),
  update: (code, data) => api.put(`/cours/${code}`, data),
  remove: (code) => api.delete(`/cours/${code}`),
  getEtudiants: (code) => api.get(`/cours/${code}/etudiants`),
  getNotes: (code) => api.get(`/cours/${code}/notes`),
  getStatistiques: (code) => api.get(`/cours/${code}/statistiques`),
  getGroupes: (code) => api.get(`/cours/${code}/groupes`),
  searchByTitre: (titre) => api.get('/cours/search/titre', { params: { titre } }),
};

// Inscriptions (/api/inscriptions/**)
export const inscriptionsApi = {
  list: () => api.get('/inscriptions'),
  getById: (id) => api.get(`/inscriptions/${id}`),
  inscrire: (etudiantId, coursCode) =>
    api.post('/inscriptions', { etudiantId, coursCode }),
  desinscrire: (id) => api.delete(`/inscriptions/${id}`),
  getByEtudiant: (etudiantId) => api.get(`/inscriptions/etudiant/${etudiantId}`),
  getByCours: (coursCode) => api.get(`/inscriptions/cours/${coursCode}`),
};

// Séances (/api/seances/**)
export const seancesApi = {
  list: () => api.get('/seances'),
  getById: (id) => api.get(`/seances/${id}`),
  create: (data) => api.post('/seances', data),
  update: (id, data) => api.put(`/seances/${id}`, data),
  remove: (id) => api.delete(`/seances/${id}`),
  getByCours: (coursCode) => api.get(`/seances/cours/${coursCode}`),
  getByFormateur: (formateurId) => api.get(`/seances/formateur/${formateurId}`),
  getEmploiDuTempsEtudiant: (etudiantId) =>
    api.get(`/seances/etudiant/${etudiantId}`),
  getByDate: (date) => api.get('/seances/date', { params: { date } }),
  getBetweenDates: (dateDebut, dateFin) =>
    api.get('/seances/date-between', { params: { dateDebut, dateFin } }),
};

// Notes (/api/notes/**)
export const notesApi = {
  list: () => api.get('/notes'),
  getById: (id) => api.get(`/notes/${id}`),
  attribuer: (etudiantId, coursCode, valeur) =>
    api.post('/notes', { etudiantId, coursCode, valeur }),
  update: (id, valeur) => api.put(`/notes/${id}`, { valeur }),
  getByEtudiant: (etudiantId) => api.get(`/notes/etudiant/${etudiantId}`),
  getByCours: (coursCode) => api.get(`/notes/cours/${coursCode}`),
};

// Statistiques (/api/statistiques/**)
export const statistiquesApi = {
  getDashboard: () => api.get('/statistiques/dashboard'),
  getCoursPlusSuivis: () => api.get('/statistiques/cours-plus-suivis'),
  getTauxReussite: (coursCode) =>
    api.get(`/statistiques/taux-reussite/${coursCode}`),
};

// Diagnostic (/api/diagnostic/**)
export const diagnosticApi = {
  getStatus: () => api.get('/diagnostic/status'),
  testUser: (email) => api.get('/diagnostic/test-user', { params: { email } }),
};

export default api;

