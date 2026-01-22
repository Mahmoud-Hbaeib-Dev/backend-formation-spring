import { useEffect, useState, useMemo } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext.jsx';
import { seancesApi } from '../../utils/api.js';
import { parseJsonSafely } from '../../utils/jsonParser.js';
import Layout from '../../components/Layout.jsx';
import { Calendar, Clock, MapPin, Edit, Trash2, Filter, X } from 'lucide-react';

const FormateurSeances = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [seances, setSeances] = useState([]);
  const [allSeances, setAllSeances] = useState([]);
  const [loading, setLoading] = useState(true);
  const [deletingId, setDeletingId] = useState(null);
  const [filterType, setFilterType] = useState('all'); // 'all', 'date', 'period'
  const [filterDate, setFilterDate] = useState('');
  const [filterDateDebut, setFilterDateDebut] = useState('');
  const [filterDateFin, setFilterDateFin] = useState('');
  
  const formateurId = useMemo(() => user?.formateurId || user?.userId || user?.id, [user]);

  useEffect(() => {
    if (!formateurId) {
      setLoading(false);
      return;
    }

    const loadSeances = async () => {
      try {
        const response = await seancesApi.getByFormateur(formateurId);
        let data = parseJsonSafely(response.data);
        if (!data) {
          data = [];
        }
        const seancesArray = Array.isArray(data) ? data : [];
        
        // Trier par date et heure
        if (seancesArray.length > 0) {
          seancesArray.sort((a, b) => {
            const dateA = new Date(`${a.date}T${a.heure}`);
            const dateB = new Date(`${b.date}T${b.heure}`);
            return dateA - dateB;
          });
        }
        setSeances(seancesArray);
        setAllSeances(seancesArray);
      } catch (error) {
        console.error('Erreur lors du chargement des séances:', error);
        setSeances([]);
        setAllSeances([]);
      } finally {
        setLoading(false);
      }
    };

    loadSeances();
  }, [formateurId]);

  // Appliquer les filtres
  useEffect(() => {
    if (!formateurId) return;

    const applyFilters = async () => {

      try {
        let filteredSeances = [];
        
        if (filterType === 'all') {
          // Charger toutes les séances
          const response = await seancesApi.getByFormateur(formateurId);
          let data = parseJsonSafely(response.data);
          filteredSeances = Array.isArray(data) ? data : [];
        } else if (filterType === 'date' && filterDate) {
          // Filtrer par date
          const response = await seancesApi.getByDate(filterDate);
          let data = parseJsonSafely(response.data);
          filteredSeances = Array.isArray(data) ? data : [];
          // Filtrer pour ne garder que celles du formateur
          filteredSeances = filteredSeances.filter(
            s => s.formateur?.id === formateurId || s.formateurInfo?.id === formateurId
          );
        } else if (filterType === 'period' && filterDateDebut && filterDateFin) {
          // Filtrer par période
          const response = await seancesApi.getBetweenDates(filterDateDebut, filterDateFin);
          let data = parseJsonSafely(response.data);
          filteredSeances = Array.isArray(data) ? data : [];
          // Filtrer pour ne garder que celles du formateur
          filteredSeances = filteredSeances.filter(
            s => s.formateur?.id === formateurId || s.formateurInfo?.id === formateurId
          );
        } else {
          // Pas de filtre actif, utiliser toutes les séances
          filteredSeances = allSeances;
        }

        // Trier par date et heure
        if (filteredSeances.length > 0) {
          filteredSeances.sort((a, b) => {
            const dateA = new Date(`${a.date}T${a.heure}`);
            const dateB = new Date(`${b.date}T${b.heure}`);
            return dateA - dateB;
          });
        }
        
        setSeances(filteredSeances);
      } catch (error) {
        console.error('Erreur lors de l\'application des filtres:', error);
      }
    };

    applyFilters();
  }, [filterType, filterDate, filterDateDebut, filterDateFin, user, allSeances]);

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
        setAllSeances(seancesArray);
      }
    } catch (error) {
      alert('Erreur lors de la suppression: ' + (error.response?.data?.message || error.message));
    } finally {
      setDeletingId(null);
    }
  };

  const handleClearFilters = () => {
    setFilterType('all');
    setFilterDate('');
    setFilterDateDebut('');
    setFilterDateFin('');
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

        {/* Filtres */}
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center space-x-4 mb-4">
            <Filter className="h-5 w-5 text-gray-500" />
            <h2 className="text-lg font-semibold text-gray-900">Filtres</h2>
            {(filterType !== 'all' || filterDate || filterDateDebut || filterDateFin) && (
              <button
                onClick={handleClearFilters}
                className="ml-auto flex items-center space-x-1 text-sm text-gray-600 hover:text-gray-900"
              >
                <X className="h-4 w-4" />
                <span>Effacer</span>
              </button>
            )}
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Type de filtre
              </label>
              <select
                value={filterType}
                onChange={(e) => {
                  setFilterType(e.target.value);
                  setFilterDate('');
                  setFilterDateDebut('');
                  setFilterDateFin('');
                }}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary-500 focus:border-primary-500"
              >
                <option value="all">Toutes les séances</option>
                <option value="date">Par date</option>
                <option value="period">Par période</option>
              </select>
            </div>

            {filterType === 'date' && (
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Date
                </label>
                <input
                  type="date"
                  value={filterDate}
                  onChange={(e) => setFilterDate(e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary-500 focus:border-primary-500"
                />
              </div>
            )}

            {filterType === 'period' && (
              <>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Date début
                  </label>
                  <input
                    type="date"
                    value={filterDateDebut}
                    onChange={(e) => setFilterDateDebut(e.target.value)}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary-500 focus:border-primary-500"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Date fin
                  </label>
                  <input
                    type="date"
                    value={filterDateFin}
                    onChange={(e) => setFilterDateFin(e.target.value)}
                    min={filterDateDebut}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary-500 focus:border-primary-500"
                  />
                </div>
              </>
            )}
          </div>
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

