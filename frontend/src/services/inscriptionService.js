import api from '../utils/api.js';

/**
 * Service pour les inscriptions
 */
export const inscriptionService = {
  /**
   * Obtenir toutes les inscriptions
   */
  getAll: async () => {
    const response = await api.get('/inscriptions');
    return response.data;
  },

  /**
   * Obtenir les inscriptions d'un étudiant
   */
  getByEtudiant: async (etudiantId) => {
    const response = await api.get(`/inscriptions/etudiant/${etudiantId}`);
    return response.data;
  },

  /**
   * Obtenir les inscriptions d'un cours
   */
  getByCours: async (coursCode) => {
    const response = await api.get(`/inscriptions/cours/${coursCode}`);
    return response.data;
  },

  /**
   * Inscrire un étudiant à un cours
   */
  inscrire: async (etudiantId, coursCode) => {
    const response = await api.post('/inscriptions', {
      etudiantId,
      coursCode,
    });
    return response.data;
  },

  /**
   * Désinscrire un étudiant d'un cours
   */
  desinscrire: async (inscriptionId) => {
    const response = await api.delete(`/inscriptions/${inscriptionId}`);
    return response.data;
  },
};

