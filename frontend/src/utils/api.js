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
      console.log('🔑 [API] Token ajouté à la requête');
    }
    console.log('📤 [API] Requête:', config.method?.toUpperCase(), config.url);
    console.log('📦 [API] Données:', config.data);
    return config;
  },
  (error) => {
    console.error('❌ [API] Erreur dans la requête:', error);
    return Promise.reject(error);
  }
);

// Intercepteur pour gérer les erreurs de réponse
api.interceptors.response.use(
  (response) => {
    console.log('✅ [API] Réponse reçue:', response.status, response.config.url);
    console.log('📦 [API] Données de réponse:', response.data);
    return response;
  },
  (error) => {
    console.error('❌ [API] Erreur de réponse:');
    console.error('📊 Status:', error.response?.status);
    console.error('📝 Status Text:', error.response?.statusText);
    console.error('💬 Message:', error.response?.data?.message || error.response?.data?.error);
    console.error('🔗 URL:', error.config?.url);
    console.error('🌐 Base URL:', error.config?.baseURL);
    
    if (error.response?.status === 401) {
      console.warn('⚠️ [API] Token expiré ou invalide, redirection vers login');
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
      console.error('🌐 [API] Erreur réseau - Le serveur ne répond pas');
      console.error('💡 Vérifiez que le backend est démarré sur http://localhost:8080');
    }
    
    return Promise.reject(error);
  }
);

export default api;

