import { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext.jsx';
import { seancesApi } from '../../utils/api.js';
import { parseJsonSafely } from '../../utils/jsonParser.js';
import Layout from '../../components/Layout.jsx';
import { Calendar, Clock, MapPin } from 'lucide-react';

const EtudiantPlanning = () => {
  const { user } = useAuth();
  const [seances, setSeances] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedDate, setSelectedDate] = useState(new Date().toISOString().split('T')[0]);

  useEffect(() => {
    const loadSeances = async () => {
      try {
        const etudiantId = user?.etudiantId || user?.userId || user?.id;
        console.log('🔍 [ETUDIANT PLANNING] EtudiantId utilisé:', etudiantId);
        if (etudiantId) {
          const response = await seancesApi.getEmploiDuTempsEtudiant(etudiantId);
          console.log('🔍 [ETUDIANT PLANNING] Réponse brute:', response);
          
          // Parser la réponse si elle est une chaîne JSON
          let data = parseJsonSafely(response.data);
          if (!data) {
            console.warn('⚠️ [ETUDIANT PLANNING] Impossible de parser les séances');
            data = [];
          } else {
            console.log('✅ [ETUDIANT PLANNING] Séances parsées:', data);
          }
          
          const seancesArray = Array.isArray(data) ? data : [];
          console.log(`📊 [ETUDIANT PLANNING] Nombre de séances reçues: ${seancesArray.length}`);
          
          // Trier par date et heure
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
        console.error('❌ [ETUDIANT PLANNING] Erreur lors du chargement du planning:', error);
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

  const seancesFiltrees = selectedDate
    ? seances.filter((s) => s.date === selectedDate)
    : seances;

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
            <h1 className="text-3xl font-bold text-gray-900">Mon Planning</h1>
            <p className="mt-2 text-gray-600">Emploi du temps de toutes vos séances</p>
          </div>
          <div>
            <input
              type="date"
              value={selectedDate}
              onChange={(e) => setSelectedDate(e.target.value)}
              className="px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary-500 focus:border-primary-500"
            />
          </div>
        </div>

        {seancesFiltrees.length === 0 ? (
          <div className="bg-white rounded-lg shadow p-12 text-center">
            <Calendar className="h-16 w-16 text-gray-400 mx-auto mb-4" />
            <p className="text-gray-600">
              {selectedDate ? 'Aucune séance pour cette date' : 'Aucune séance planifiée'}
            </p>
          </div>
        ) : (
          <div className="bg-white rounded-lg shadow overflow-hidden">
            <div className="divide-y divide-gray-200">
              {seancesFiltrees.map((seance) => (
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
                        {seance.formateur && (
                          <div className="flex items-center">
                            <span className="text-gray-500">
                              Formateur: {seance.formateur.nom}
                            </span>
                          </div>
                        )}
                      </div>
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

export default EtudiantPlanning;

