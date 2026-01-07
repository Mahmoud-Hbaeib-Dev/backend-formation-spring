import api from '../utils/api.js';

/**
 * Service pour les notes
 */
export const noteService = {
  /**
   * Obtenir toutes les notes
   */
  getAll: async () => {
    const response = await api.get('/notes');
    return response.data;
  },

  /**
   * Obtenir les notes d'un étudiant
   */
  getByEtudiant: async (etudiantId) => {
    const response = await api.get(`/notes/etudiant/${etudiantId}`);
    return response.data;
  },

  /**
   * Obtenir les notes d'un cours
   */
  getByCours: async (coursCode) => {
    const response = await api.get(`/notes/cours/${coursCode}`);
    return response.data;
  },

  /**
   * Attribuer une note
   */
  attribuer: async (etudiantId, coursCode, valeur) => {
    const response = await api.post('/notes', {
      etudiantId,
      coursCode,
      valeur,
    });
    return response.data;
  },

  /**
   * Modifier une note
   */
  modifier: async (noteId, valeur) => {
    const response = await api.put(`/notes/${noteId}`, { valeur });
    return response.data;
  },
};

