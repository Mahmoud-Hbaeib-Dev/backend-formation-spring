import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext.jsx';
import { coursApi, formateursApi } from '../../utils/api.js';
import { parseJsonSafely } from '../../utils/jsonParser.js';
import Layout from '../../components/Layout.jsx';
import { ArrowLeft, Save, X } from 'lucide-react';

const CreateCours = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [sessions, setSessions] = useState([]);
  const [formData, setFormData] = useState({
    code: '',
    titre: '',
    description: '',
    sessionId: '',
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const formateurId = user?.formateurId || user?.userId || user?.id;

  useEffect(() => {
    const loadSessions = async () => {
      if (!formateurId) return;

      try {
        // Récupérer les cours du formateur pour extraire les sessions uniques
        const response = await formateursApi.getCours(formateurId);
        let coursData = parseJsonSafely(response.data);
        if (!coursData) {
          coursData = [];
        }
        const coursArray = Array.isArray(coursData) ? coursData : [];
        
        // Extraire les sessions uniques
        const sessionsMap = new Map();
        coursArray.forEach(cours => {
          if (cours.session && cours.session.id) {
            if (!sessionsMap.has(cours.session.id)) {
              sessionsMap.set(cours.session.id, cours.session);
            }
          }
        });
        
        setSessions(Array.from(sessionsMap.values()));
      } catch (error) {
        console.error('Erreur lors du chargement des sessions:', error);
      }
    };

    loadSessions();
  }, [formateurId]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);

    try {
      const coursData = {
        code: formData.code || undefined, // Le backend générera un code si vide
        titre: formData.titre,
        description: formData.description || undefined,
        formateur: {
          id: formateurId,
        },
        session: formData.sessionId ? {
          id: formData.sessionId,
        } : undefined,
      };

      await coursApi.create(coursData);
      setSuccess('Cours créé avec succès');
      setTimeout(() => {
        navigate('/formateur/cours');
      }, 1500);
    } catch (err) {
      setError(
        err.response?.data?.message ||
        err.response?.data?.error ||
        'Erreur lors de la création du cours'
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <Layout>
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <div>
            <button
              onClick={() => navigate('/formateur/cours')}
              className="flex items-center text-gray-600 hover:text-gray-900 mb-2"
            >
              <ArrowLeft className="h-4 w-4 mr-2" />
              Retour
            </button>
            <h1 className="text-3xl font-bold text-gray-900">Créer un nouveau cours</h1>
            <p className="mt-2 text-gray-600">Remplissez les informations du cours</p>
          </div>
        </div>

        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-md">
            {error}
          </div>
        )}

        {success && (
          <div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-md">
            {success}
          </div>
        )}

        <div className="bg-white rounded-lg shadow p-6">
          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Code du cours <span className="text-gray-400">(optionnel - généré automatiquement si vide)</span>
              </label>
              <input
                type="text"
                value={formData.code}
                onChange={(e) => setFormData({ ...formData, code: e.target.value })}
                placeholder="Ex: JAVA001"
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary-500 focus:border-primary-500"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Titre <span className="text-red-500">*</span>
              </label>
              <input
                type="text"
                value={formData.titre}
                onChange={(e) => setFormData({ ...formData, titre: e.target.value })}
                required
                placeholder="Ex: Java Fondamentaux"
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary-500 focus:border-primary-500"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Description
              </label>
              <textarea
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                rows={4}
                placeholder="Description du cours..."
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary-500 focus:border-primary-500"
              />
            </div>

            {sessions.length > 0 && (
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Session
                </label>
                <select
                  value={formData.sessionId}
                  onChange={(e) => setFormData({ ...formData, sessionId: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary-500 focus:border-primary-500"
                >
                  <option value="">-- Sélectionner une session --</option>
                  {sessions.map((session) => (
                    <option key={session.id} value={session.id}>
                      {session.semestre} - {session.anneeScolaire}
                    </option>
                  ))}
                </select>
                <p className="mt-1 text-sm text-gray-500">
                  Si aucune session n'est sélectionnée, le cours sera créé sans session
                </p>
              </div>
            )}

            <div className="flex space-x-3">
              <button
                type="submit"
                disabled={loading}
                className="flex items-center space-x-2 px-4 py-2 bg-primary-600 text-white rounded-md hover:bg-primary-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <Save className="h-5 w-5" />
                <span>{loading ? 'Création...' : 'Créer le cours'}</span>
              </button>
              <button
                type="button"
                onClick={() => navigate('/formateur/cours')}
                className="flex items-center space-x-2 px-4 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300 transition-colors"
              >
                <X className="h-5 w-5" />
                <span>Annuler</span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </Layout>
  );
};

export default CreateCours;
