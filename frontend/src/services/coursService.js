import api from '../utils/api.js';

/**
 * Service pour les cours
 */
export const coursService = {
  /**
   * Obtenir tous les cours
   */
  getAll: async () => {
    const response = await api.get('/cours');
    return response.data;
  },

  /**
   * Obtenir un cours par code
   */
  getByCode: async (code) => {
    const response = await api.get(`/cours/${code}`);
    return response.data;
  },

  /**
   * Obtenir les cours d'un formateur
   */
  getByFormateur: async (formateurId) => {
    const response = await api.get(`/cours/formateur/${formateurId}`);
    return response.data;
  },

  /**
   * Obtenir les cours d'une session
   */
  getBySession: async (sessionId) => {
    const response = await api.get(`/cours/session/${sessionId}`);
    return response.data;
  },

  /**
   * Rechercher des cours par titre
   */
  searchByTitre: async (titre) => {
    const response = await api.get(`/cours/search?titre=${titre}`);
    return response.data;
  },

  /**
   * Obtenir les étudiants d'un cours
   */
  getEtudiants: async (coursCode) => {
    const response = await api.get(`/cours/${coursCode}/etudiants`);
    return response.data;
  },

  /**
   * Obtenir les notes d'un cours
   */
  getNotes: async (coursCode) => {
    const response = await api.get(`/cours/${coursCode}/notes`);
    return response.data;
  },

  /**
   * Obtenir les statistiques d'un cours
   */
  getStatistiques: async (coursCode) => {
    const response = await api.get(`/cours/${coursCode}/statistiques`);
    return response.data;
  },

  /**
   * Obtenir les groupes d'un cours
   */
  getGroupes: async (coursCode) => {
    const response = await api.get(`/cours/${coursCode}/groupes`);
    return response.data;
  },
};

