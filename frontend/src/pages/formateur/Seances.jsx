import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext.jsx';
import { seanceService } from '../../services/seanceService.js';
import Layout from '../../components/Layout.jsx';
import { Calendar, Clock, MapPin } from 'lucide-react';

const FormateurSeances = () => {
  const { user } = useAuth();
  const [seances, setSeances] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadSeances = async () => {
      try {
        const formateurId = user?.formateurId || user?.userId || user?.id;
        if (formateurId) {
          const data = await seanceService.getByFormateur(formateurId);
          // Trier par date et heure
          data.sort((a, b) => {
            const dateA = new Date(`${a.date}T${a.heure}`);
            const dateB = new Date(`${b.date}T${b.heure}`);
            return dateA - dateB;
          });
          setSeances(data);
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

