import api from '../utils/api.js';

/**
 * Service pour les étudiants
 */
export const etudiantService = {
  /**
   * Obtenir tous les étudiants
   */
  getAll: async () => {
    const response = await api.get('/etudiants');
    return response.data;
  },

  /**
   * Obtenir un étudiant par ID
   */
  getById: async (id) => {
    const response = await api.get(`/etudiants/${id}`);
    return response.data;
  },

  /**
   * Obtenir un étudiant par matricule
   */
  getByMatricule: async (matricule) => {
    const response = await api.get(`/etudiants/matricule/${matricule}`);
    return response.data;
  },

  /**
   * Rechercher des étudiants par nom
   */
  searchByNom: async (nom) => {
    const response = await api.get(`/etudiants/search?nom=${nom}`);
    return response.data;
  },

  /**
   * Obtenir les notes d'un étudiant
   */
  getNotes: async (etudiantId) => {
    const response = await api.get(`/etudiants/${etudiantId}/notes`);
    return response.data;
  },

  /**
   * Obtenir les cours d'un étudiant
   */
  getCours: async (etudiantId) => {
    const response = await api.get(`/etudiants/${etudiantId}/cours`);
    return response.data;
  },

  /**
   * Obtenir la moyenne d'un étudiant
   */
  getMoyenne: async (etudiantId) => {
    const response = await api.get(`/etudiants/${etudiantId}/moyenne`);
    return response.data;
  },
};

