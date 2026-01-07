import api from '../utils/api.js';

/**
 * Service pour les statistiques
 */
export const statistiquesService = {
  /**
   * Obtenir les statistiques du dashboard
   */
  getDashboard: async () => {
    const response = await api.get('/statistiques/dashboard');
    return response.data;
  },

  /**
   * Obtenir les cours les plus suivis
   */
  getCoursPlusSuivis: async () => {
    const response = await api.get('/statistiques/cours-plus-suivis');
    return response.data;
  },

  /**
   * Obtenir le taux de réussite d'un cours
   */
  getTauxReussite: async (coursCode) => {
    const response = await api.get(`/statistiques/taux-reussite/${coursCode}`);
    return response.data;
  },
};

