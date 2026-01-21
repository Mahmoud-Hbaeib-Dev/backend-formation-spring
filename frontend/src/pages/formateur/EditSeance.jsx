import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext.jsx';
import { seancesApi, formateursApi } from '../../utils/api.js';
import { parseJsonSafely } from '../../utils/jsonParser.js';
import Layout from '../../components/Layout.jsx';
import { Calendar, Clock, MapPin, ArrowLeft } from 'lucide-react';

const FormateurEditSeance = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const { id } = useParams();
  const [cours, setCours] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [formData, setFormData] = useState({
    date: '',
    heure: '',
    salle: '',
    coursCode: '',
    formateurId: user?.formateurId || user?.userId || user?.id || '',
  });
  const [error, setError] = useState('');

  useEffect(() => {
    const loadData = async () => {
      try {
        const formateurId = user?.formateurId || user?.userId || user?.id;
        
        // Charger les cours et la séance en parallèle
        const [coursResponse, seanceResponse] = await Promise.all([
          formateursApi.getCours(formateurId),
          seancesApi.getById(id),
        ]);

        // Parser les cours
        let coursData = parseJsonSafely(coursResponse.data);
        if (!coursData) {
          coursData = [];
        }
        const coursArray = Array.isArray(coursData) ? coursData : [];
        setCours(coursArray);

        // Parser la séance
        let seanceData = parseJsonSafely(seanceResponse.data);
        if (seanceData) {
          // Formater la date pour l'input date (YYYY-MM-DD)
          const dateStr = seanceData.date;
          const formattedDate = dateStr ? dateStr.split('T')[0] : '';
          
          // Formater l'heure pour l'input time (HH:MM)
          const heureStr = seanceData.heure;
          const formattedHeure = heureStr ? heureStr.substring(0, 5) : '';

          setFormData({
            date: formattedDate,
            heure: formattedHeure,
            salle: seanceData.salle || '',
            coursCode: seanceData.cours?.code || (coursArray.length > 0 ? coursArray[0].code : ''),
            formateurId: seanceData.formateur?.id || formateurId || '',
          });
        }
      } catch (error) {
        console.error('Erreur lors du chargement:', error);
        setError('Erreur lors du chargement de la séance');
      } finally {
        setLoading(false);
      }
    };

    if (id && user) {
      loadData();
    }
  }, [id, user]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSaving(true);

    try {
      // Formater les données pour le backend (le backend attend des objets Cours et Formateur)
      const dataToSend = {
        date: formData.date,
        heure: formData.heure,
        salle: formData.salle,
        cours: {
          code: formData.coursCode
        },
        formateur: {
          id: formData.formateurId
        }
      };
      await seancesApi.update(id, dataToSend);
      navigate('/formateur/seances');
    } catch (err) {
      setError(
        err.response?.data?.message ||
        err.response?.data?.error ||
        'Erreur lors de la modification de la séance'
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

  return (
    <Layout>
      <div className="max-w-2xl mx-auto">
        <div className="mb-6">
          <button
            onClick={() => navigate('/formateur/seances')}
            className="flex items-center text-gray-600 hover:text-gray-900 mb-4"
          >
            <ArrowLeft className="h-4 w-4 mr-2" />
            Retour
          </button>
          <h1 className="text-3xl font-bold text-gray-900">Modifier la séance</h1>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          {error && (
            <div className="mb-4 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-md text-sm">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                <Calendar className="h-4 w-4 inline mr-2" />
                Cours
              </label>
              <select
                value={formData.coursCode}
                onChange={(e) => setFormData({ ...formData, coursCode: e.target.value })}
                required
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

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                <Calendar className="h-4 w-4 inline mr-2" />
                Date
              </label>
              <input
                type="date"
                value={formData.date}
                onChange={(e) => setFormData({ ...formData, date: e.target.value })}
                required
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary-500 focus:border-primary-500"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                <Clock className="h-4 w-4 inline mr-2" />
                Heure
              </label>
              <input
                type="time"
                value={formData.heure}
                onChange={(e) => setFormData({ ...formData, heure: e.target.value })}
                required
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary-500 focus:border-primary-500"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                <MapPin className="h-4 w-4 inline mr-2" />
                Salle
              </label>
              <input
                type="text"
                value={formData.salle}
                onChange={(e) => setFormData({ ...formData, salle: e.target.value })}
                required
                placeholder="Ex: Salle A, Salle B..."
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary-500 focus:border-primary-500"
              />
            </div>

            <div className="flex space-x-3">
              <button
                type="submit"
                disabled={saving}
                className="flex-1 px-4 py-2 bg-primary-600 text-white rounded-md hover:bg-primary-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {saving ? 'Enregistrement...' : 'Enregistrer les modifications'}
              </button>
              <button
                type="button"
                onClick={() => navigate('/formateur/seances')}
                className="px-4 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300 transition-colors"
              >
                Annuler
              </button>
            </div>
          </form>
        </div>
      </div>
    </Layout>
  );
};

export default FormateurEditSeance;

