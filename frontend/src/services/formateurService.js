import api from '../utils/api.js';

/**
 * Service pour les formateurs
 */
export const formateurService = {
  /**
   * Obtenir tous les formateurs
   */
  getAll: async () => {
    const response = await api.get('/formateurs');
    return response.data;
  },

  /**
   * Obtenir un formateur par ID
   */
  getById: async (id) => {
    const response = await api.get(`/formateurs/${id}`);
    return response.data;
  },

  /**
   * Obtenir les formateurs par spécialité
   */
  getBySpecialite: async (specialite) => {
    const response = await api.get(`/formateurs/specialite/${specialite}`);
    return response.data;
  },
};

