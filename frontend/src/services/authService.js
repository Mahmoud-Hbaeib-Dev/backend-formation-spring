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
    try {
      const response = await api.post('/auth/login', { login, password });
      return response.data;
    } catch (error) {
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

