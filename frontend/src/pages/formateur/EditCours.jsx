import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext.jsx';
import { coursApi, formateursApi } from '../../utils/api.js';
import { parseJsonSafely } from '../../utils/jsonParser.js';
import Layout from '../../components/Layout.jsx';
import { ArrowLeft, Save, X } from 'lucide-react';

const EditCours = () => {
  const { code } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [sessions, setSessions] = useState([]);
  const [cours, setCours] = useState(null);
  const [formData, setFormData] = useState({
    titre: '',
    description: '',
    sessionId: '',
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const formateurId = user?.formateurId || user?.userId || user?.id;

  useEffect(() => {
    const loadData = async () => {
      if (!code || !formateurId) {
        setLoading(false);
        return;
      }

      try {
        // Charger le cours
        const coursResponse = await coursApi.getByCode(code);
        let coursData = parseJsonSafely(coursResponse.data);
        if (!coursData) {
          setError('Cours non trouvé');
          setLoading(false);
          return;
        }

        setCours(coursData);
        setFormData({
          titre: coursData.titre || '',
          description: coursData.description || '',
          sessionId: coursData.session?.id || '',
        });

        // Charger les sessions disponibles
        const formateurResponse = await formateursApi.getCours(formateurId);
        let formateurCoursData = parseJsonSafely(formateurResponse.data);
        if (!formateurCoursData) {
          formateurCoursData = [];
        }
        const formateurCoursArray = Array.isArray(formateurCoursData) ? formateurCoursData : [];
        
        // Extraire les sessions uniques
        const sessionsMap = new Map();
        formateurCoursArray.forEach(c => {
          if (c.session && c.session.id) {
            if (!sessionsMap.has(c.session.id)) {
              sessionsMap.set(c.session.id, c.session);
            }
          }
        });
        
        // Ajouter la session actuelle du cours si elle n'est pas dans la liste
        if (coursData.session && coursData.session.id && !sessionsMap.has(coursData.session.id)) {
          sessionsMap.set(coursData.session.id, coursData.session);
        }
        
        setSessions(Array.from(sessionsMap.values()));
      } catch (error) {
        console.error('Erreur lors du chargement:', error);
        setError('Erreur lors du chargement du cours');
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, [code, formateurId]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setSaving(true);

    try {
      const coursData = {
        titre: formData.titre,
        description: formData.description || undefined,
        formateur: {
          id: formateurId,
        },
        session: formData.sessionId ? {
          id: formData.sessionId,
        } : undefined,
      };

      await coursApi.update(code, coursData);
      setSuccess('Cours mis à jour avec succès');
      setTimeout(() => {
        navigate(`/formateur/cours/${code}`);
      }, 1500);
    } catch (err) {
      setError(
        err.response?.data?.message ||
        err.response?.data?.error ||
        'Erreur lors de la mise à jour du cours'
      );
    } finally {
      setSaving(false);
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

  if (!cours) {
    return (
      <Layout>
        <div className="text-center py-12">
          <p className="text-gray-600">Cours non trouvé</p>
          <button
            onClick={() => navigate('/formateur/cours')}
            className="text-primary-600 hover:underline mt-4"
          >
            Retour à la liste
          </button>
        </div>
      </Layout>
    );
  }

  return (
    <Layout>
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <div>
            <button
              onClick={() => navigate(`/formateur/cours/${code}`)}
              className="flex items-center text-gray-600 hover:text-gray-900 mb-2"
            >
              <ArrowLeft className="h-4 w-4 mr-2" />
              Retour
            </button>
            <h1 className="text-3xl font-bold text-gray-900">Modifier le cours</h1>
            <p className="mt-2 text-gray-600">Code: {code}</p>
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
                Code du cours
              </label>
              <input
                type="text"
                value={code}
                disabled
                className="w-full px-3 py-2 border border-gray-300 rounded-md bg-gray-50 text-gray-500"
              />
              <p className="mt-1 text-sm text-gray-500">Le code ne peut pas être modifié</p>
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
                  <option value="">-- Aucune session --</option>
                  {sessions.map((session) => (
                    <option key={session.id} value={session.id}>
                      {session.semestre} - {session.anneeScolaire}
                    </option>
                  ))}
                </select>
              </div>
            )}

            <div className="flex space-x-3">
              <button
                type="submit"
                disabled={saving}
                className="flex items-center space-x-2 px-4 py-2 bg-primary-600 text-white rounded-md hover:bg-primary-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <Save className="h-5 w-5" />
                <span>{saving ? 'Enregistrement...' : 'Enregistrer les modifications'}</span>
              </button>
              <button
                type="button"
                onClick={() => navigate(`/formateur/cours/${code}`)}
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

export default EditCours;
