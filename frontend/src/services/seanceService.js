import api from '../utils/api.js';

/**
 * Service pour les séances
 */
export const seanceService = {
  /**
   * Obtenir toutes les séances
   */
  getAll: async () => {
    const response = await api.get('/seances');
    return response.data;
  },

  /**
   * Obtenir les séances d'un cours
   */
  getByCours: async (coursCode) => {
    const response = await api.get(`/seances/cours/${coursCode}`);
    return response.data;
  },

  /**
   * Obtenir les séances d'un formateur
   */
  getByFormateur: async (formateurId) => {
    const response = await api.get(`/seances/formateur/${formateurId}`);
    return response.data;
  },

  /**
   * Obtenir l'emploi du temps d'un étudiant
   */
  getEmploiDuTempsEtudiant: async (etudiantId) => {
    const response = await api.get(`/seances/etudiant/${etudiantId}`);
    return response.data;
  },

  /**
   * Obtenir les séances d'une date
   */
  getByDate: async (date) => {
    const response = await api.get(`/seances/date?date=${date}`);
    return response.data;
  },

  /**
   * Obtenir une séance par ID
   */
  getById: async (id) => {
    const response = await api.get(`/seances/${id}`);
    return response.data;
  },

  /**
   * Créer une séance
   */
  create: async (seanceData) => {
    const response = await api.post('/seances', seanceData);
    return response.data;
  },

  /**
   * Modifier une séance
   */
  update: async (id, seanceData) => {
    const response = await api.put(`/seances/${id}`, seanceData);
    return response.data;
  },

  /**
   * Supprimer une séance
   */
  delete: async (id) => {
    const response = await api.delete(`/seances/${id}`);
    return response.data;
  },
};

