import api from '../utils/api.js';

/**
 * Service d'authentification
 */
export const authService = {
  /**
   * Connexion
   * @param {string} login 
   * @param {string} password 
   * @returns {Promise} Réponse avec token et user info
   */
  login: async (login, password) => {
    console.log('🔐 [AUTH SERVICE] Tentative de connexion...');
    console.log('📧 Login/Email:', login);
    console.log('🔑 Password:', password ? '***' : 'vide');
    console.log('🌐 URL API:', import.meta.env.VITE_API_URL || 'http://localhost:8080/api');
    
    try {
      const response = await api.post('/auth/login', { login, password });
      console.log('✅ [AUTH SERVICE] Connexion réussie!');
      console.log('📦 Réponse:', response.data);
      return response.data;
    } catch (error) {
      console.error('❌ [AUTH SERVICE] Erreur de connexion:');
      console.error('📊 Status:', error.response?.status);
      console.error('📝 Status Text:', error.response?.statusText);
      console.error('💬 Message:', error.response?.data?.message || error.response?.data?.error);
      console.error('📦 Données complètes:', error.response?.data);
      console.error('🔗 URL:', error.config?.url);
      console.error('🌐 Base URL:', error.config?.baseURL);
      console.error('❌ Erreur complète:', error);
      throw error;
    }
  },

  /**
   * Obtenir les informations de l'utilisateur connecté
   * @returns {Promise} User info
   */
  getCurrentUser: async () => {
    const response = await api.get('/auth/me');
    return response.data;
  },
};

