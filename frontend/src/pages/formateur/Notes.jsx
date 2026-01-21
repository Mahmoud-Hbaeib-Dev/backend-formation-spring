import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext.jsx';
import { formateursApi, notesApi, etudiantsApi } from '../../utils/api.js';
import { parseJsonSafely } from '../../utils/jsonParser.js';
import Layout from '../../components/Layout.jsx';
import { BookOpen, User, Plus, Edit } from 'lucide-react';

const FormateurNotes = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [cours, setCours] = useState([]);
  const [selectedCours, setSelectedCours] = useState(null);
  const [etudiants, setEtudiants] = useState([]);
  const [notes, setNotes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({
    etudiantId: '',
    valeur: '',
  });

  useEffect(() => {
    const loadData = async () => {
      try {
        const formateurId = user?.formateurId || user?.userId || user?.id;
        if (formateurId) {
          const response = await formateursApi.getCours(formateurId);
          let coursData = parseJsonSafely(response.data);
          if (!coursData) {
            coursData = [];
          }
          const coursArray = Array.isArray(coursData) ? coursData : [];
          setCours(coursArray);
          if (coursArray.length > 0) {
            setSelectedCours(coursArray[0].code);
          }
        }
      } catch (error) {
        console.error('Erreur lors du chargement:', error);
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, [user]);

  useEffect(() => {
    const loadNotes = async () => {
      if (selectedCours) {
        try {
          const [notesResponse, etudiantsResponse] = await Promise.all([
            notesApi.getByCours(selectedCours),
            etudiantsApi.list(),
          ]);
          
          let notesData = parseJsonSafely(notesResponse.data);
          if (!notesData) {
            notesData = [];
          }
          
          let etudiantsData = parseJsonSafely(etudiantsResponse.data);
          if (!etudiantsData) {
            etudiantsData = [];
          }
          
          const notesArray = Array.isArray(notesData) ? notesData : [];
          const etudiantsArray = Array.isArray(etudiantsData) ? etudiantsData : [];
          setNotes(notesArray);
          setEtudiants(etudiantsArray);
        } catch (error) {
          console.error('Erreur lors du chargement des notes:', error);
        }
      }
    };

    loadNotes();
  }, [selectedCours]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await notesApi.attribuer(
        formData.etudiantId,
        selectedCours,
        parseFloat(formData.valeur)
      );
      setShowForm(false);
      setFormData({ etudiantId: '', valeur: '' });
      // Recharger les notes
      const notesResponse = await notesApi.getByCours(selectedCours);
      let notesData = parseJsonSafely(notesResponse.data);
      if (!notesData) {
        notesData = [];
      }
      const notesArray = Array.isArray(notesData) ? notesData : [];
      setNotes(notesArray);
    } catch (error) {
      alert('Erreur lors de l\'attribution de la note: ' + (error.response?.data?.message || error.message));
    }
  };

  if (loading) {
    return (
      <Layout>
        <div className="flex justify-center items-center h-64">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
        </div>
      </Layout>
    );
  }

  return (
    <Layout>
      <div className="space-y-6">
        <div className="flex justify-between items-center">
          <div>
            <h1 className="text-3xl font-bold text-gray-900">Gestion des Notes</h1>
            <p className="mt-2 text-gray-600">Attribuer et modifier les notes</p>
          </div>
          {selectedCours && (
            <button
              onClick={() => setShowForm(true)}
              className="flex items-center space-x-2 px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors"
            >
              <Plus size={20} />
              <span>Attribuer une note</span>
            </button>
          )}
        </div>

        {/* Sélection du cours */}
        <div className="bg-white rounded-lg shadow p-6">
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Sélectionner un cours
          </label>
          <select
            value={selectedCours || ''}
            onChange={(e) => setSelectedCours(e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary-500 focus:border-primary-500"
          >
            <option value="">-- Sélectionner un cours --</option>
            {cours.map((c) => (
              <option key={c.code} value={c.code}>
                {c.titre} ({c.code})
              </option>
            ))}
          </select>
        </div>

        {/* Formulaire d'attribution */}
        {showForm && selectedCours && (
          <div className="bg-white rounded-lg shadow p-6">
            <h2 className="text-xl font-semibold mb-4">Attribuer une note</h2>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Étudiant
                </label>
                <select
                  value={formData.etudiantId}
                  onChange={(e) => setFormData({ ...formData, etudiantId: e.target.value })}
                  required
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary-500 focus:border-primary-500"
                >
                  <option value="">-- Sélectionner un étudiant --</option>
                  {etudiants.map((e) => (
                    <option key={e.id} value={e.id}>
                      {e.nom} {e.prenom} ({e.matricule})
                    </option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Note (0-20)
                </label>
                <input
                  type="number"
                  min="0"
                  max="20"
                  step="0.5"
                  value={formData.valeur}
                  onChange={(e) => setFormData({ ...formData, valeur: e.target.value })}
                  required
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary-500 focus:border-primary-500"
                />
              </div>
              <div className="flex space-x-3">
                <button
                  type="submit"
                  className="px-4 py-2 bg-primary-600 text-white rounded-md hover:bg-primary-700 transition-colors"
                >
                  Attribuer
                </button>
                <button
                  type="button"
                  onClick={() => {
                    setShowForm(false);
                    setFormData({ etudiantId: '', valeur: '' });
                  }}
                  className="px-4 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300 transition-colors"
                >
                  Annuler
                </button>
              </div>
            </form>
          </div>
        )}

        {/* Liste des notes */}
        {selectedCours && (
          <div className="bg-white rounded-lg shadow overflow-hidden">
            <div className="px-6 py-4 border-b border-gray-200">
              <h2 className="text-xl font-semibold">Notes du cours</h2>
            </div>
            {notes.length === 0 ? (
              <div className="p-12 text-center text-gray-500">
                Aucune note attribuée pour ce cours
              </div>
            ) : (
              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Étudiant
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Matricule
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Note
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Date
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Actions
                      </th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {notes.map((note) => (
                      <tr key={note.id} className="hover:bg-gray-50">
                        <td className="px-6 py-4 whitespace-nowrap">
                          {note.etudiant?.nom} {note.etudiant?.prenom}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                          {note.etudiant?.matricule}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <span
                            className={`px-2 py-1 text-sm font-semibold rounded ${
                              note.valeur >= 10
                                ? 'bg-green-100 text-green-800'
                                : 'bg-red-100 text-red-800'
                            }`}
                          >
                            {note.valeur}/20
                          </span>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                          {new Date(note.dateSaisie).toLocaleDateString('fr-FR')}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                          <button
                            onClick={() => navigate(`/formateur/notes/${note.id}/edit`)}
                            className="text-blue-600 hover:text-blue-900 p-2 hover:bg-blue-50 rounded-md transition-colors"
                            title="Modifier"
                          >
                            <Edit className="h-5 w-5" />
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        )}
      </div>
    </Layout>
  );
};

export default FormateurNotes;

