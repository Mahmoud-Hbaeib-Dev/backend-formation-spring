import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext.jsx';
import { seancesApi } from '../../utils/api.js';
import { parseJsonSafely } from '../../utils/jsonParser.js';
import Layout from '../../components/Layout.jsx';
import { Calendar, Clock, MapPin, Edit, Trash2 } from 'lucide-react';

const FormateurSeances = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [seances, setSeances] = useState([]);
  const [loading, setLoading] = useState(true);
  const [deletingId, setDeletingId] = useState(null);

  useEffect(() => {
    const loadSeances = async () => {
      try {
        const formateurId = user?.formateurId || user?.userId || user?.id;
        console.log('🔍 [FORMATEUR SEANCES] FormateurId utilisé:', formateurId);
        console.log('🔍 [FORMATEUR SEANCES] User object:', user);
        if (formateurId) {
          const response = await seancesApi.getByFormateur(formateurId);
          console.log('🔍 [FORMATEUR SEANCES] Réponse brute:', response);
          
          // Parser la réponse si elle est une chaîne JSON
          let data = parseJsonSafely(response.data);
          if (!data) {
            console.warn('⚠️ [FORMATEUR SEANCES] Impossible de parser les séances');
            data = [];
          } else {
            console.log('✅ [FORMATEUR SEANCES] Séances parsées:', data);
          }
          
          const seancesArray = Array.isArray(data) ? data : [];
          console.log(`📊 [FORMATEUR SEANCES] Nombre de séances reçues: ${seancesArray.length}`);
          // Trier par date et heure
          if (seancesArray.length > 0) {
            seancesArray.sort((a, b) => {
              const dateA = new Date(`${a.date}T${a.heure}`);
              const dateB = new Date(`${b.date}T${b.heure}`);
              return dateA - dateB;
            });
          }
          setSeances(seancesArray);
          console.log(`✅ [FORMATEUR SEANCES] ${seancesArray.length} séance(s) chargée(s) dans le state`);
        }
      } catch (error) {
        console.error('Erreur lors du chargement des séances:', error);
      } finally {
        setLoading(false);
      }
    };

    loadSeances();
  }, [user]);

  const formatDate = (dateStr) => {
    const date = new Date(dateStr);
    return date.toLocaleDateString('fr-FR', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  const handleDelete = async (seanceId) => {
    if (!window.confirm('Êtes-vous sûr de vouloir supprimer cette séance ?')) {
      return;
    }

    setDeletingId(seanceId);
    try {
      await seancesApi.remove(seanceId);
      // Recharger les séances
      const formateurId = user?.formateurId || user?.userId || user?.id;
      if (formateurId) {
        const response = await seancesApi.getByFormateur(formateurId);
        let data = parseJsonSafely(response.data);
        if (!data) {
          data = [];
        }
        const seancesArray = Array.isArray(data) ? data : [];
        if (seancesArray.length > 0) {
          seancesArray.sort((a, b) => {
            const dateA = new Date(`${a.date}T${a.heure}`);
            const dateB = new Date(`${b.date}T${b.heure}`);
            return dateA - dateB;
          });
        }
        setSeances(seancesArray);
      }
    } catch (error) {
      alert('Erreur lors de la suppression: ' + (error.response?.data?.message || error.message));
    } finally {
      setDeletingId(null);
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
            <h1 className="text-3xl font-bold text-gray-900">Mes Séances</h1>
            <p className="mt-2 text-gray-600">Planning de toutes vos séances</p>
          </div>
          <Link
            to="/formateur/seances/new"
            className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors"
          >
            Créer une séance
          </Link>
        </div>

        {seances.length === 0 ? (
          <div className="bg-white rounded-lg shadow p-12 text-center">
            <Calendar className="h-16 w-16 text-gray-400 mx-auto mb-4" />
            <p className="text-gray-600">Aucune séance planifiée</p>
          </div>
        ) : (
          <div className="bg-white rounded-lg shadow overflow-hidden">
            <div className="divide-y divide-gray-200">
              {seances.map((seance) => (
                <div key={seance.id} className="p-6 hover:bg-gray-50 transition-colors">
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <h3 className="text-lg font-semibold text-gray-900">
                        {seance.cours?.titre || 'Cours'}
                      </h3>
                      <p className="text-sm text-gray-500 mt-1">
                        {seance.cours?.code}
                      </p>
                      <div className="mt-4 flex flex-wrap gap-4 text-sm text-gray-600">
                        <div className="flex items-center">
                          <Calendar className="h-4 w-4 mr-2" />
                          <span>{formatDate(seance.date)}</span>
                        </div>
                        <div className="flex items-center">
                          <Clock className="h-4 w-4 mr-2" />
                          <span>{seance.heure}</span>
                        </div>
                        {seance.salle && (
                          <div className="flex items-center">
                            <MapPin className="h-4 w-4 mr-2" />
                            <span>Salle: {seance.salle}</span>
                          </div>
                        )}
                      </div>
                    </div>
                    <div className="flex items-center space-x-2">
                      <button
                        onClick={() => navigate(`/formateur/seances/${seance.id}/edit`)}
                        className="p-2 text-blue-600 hover:bg-blue-50 rounded-md transition-colors"
                        title="Modifier"
                      >
                        <Edit className="h-5 w-5" />
                      </button>
                      <button
                        onClick={() => handleDelete(seance.id)}
                        disabled={deletingId === seance.id}
                        className="p-2 text-red-600 hover:bg-red-50 rounded-md transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                        title="Supprimer"
                      >
                        {deletingId === seance.id ? (
                          <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-red-600"></div>
                        ) : (
                          <Trash2 className="h-5 w-5" />
                        )}
                      </button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </Layout>
  );
};

export default FormateurSeances;

