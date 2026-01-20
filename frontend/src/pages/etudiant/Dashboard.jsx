import { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext.jsx';
import { inscriptionsApi, notesApi, seancesApi } from '../../utils/api.js';
import Layout from '../../components/Layout.jsx';
import { BookOpen, FileText, Calendar, TrendingUp } from 'lucide-react';

const EtudiantDashboard = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState({
    totalCours: 0,
    totalNotes: 0,
    moyenne: 0,
    seancesAujourdhui: 0,
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadStats = async () => {
      try {
        // Utiliser etudiantId si disponible, sinon userId
        const etudiantId = user?.etudiantId || user?.userId || user?.id;
        
        if (etudiantId) {
          const [inscriptionsResponse, notesResponse, seancesResponse] = await Promise.all([
            inscriptionsApi.getByEtudiant(etudiantId),
            notesApi.getByEtudiant(etudiantId),
            seancesApi.getEmploiDuTempsEtudiant(etudiantId),
          ]);

          const inscriptions = Array.isArray(inscriptionsResponse.data) 
            ? inscriptionsResponse.data 
            : Array.isArray(inscriptionsResponse) 
              ? inscriptionsResponse 
              : [];
          const notes = Array.isArray(notesResponse.data) 
            ? notesResponse.data 
            : Array.isArray(notesResponse) 
              ? notesResponse 
              : [];
          const seances = Array.isArray(seancesResponse.data) 
            ? seancesResponse.data 
            : Array.isArray(seancesResponse) 
              ? seancesResponse 
              : [];

          const inscriptionsActives = Array.isArray(inscriptions) 
            ? inscriptions.filter((i) => i.status === 'ACTIVE') 
            : [];
          const aujourdhui = new Date().toISOString().split('T')[0];
          const seancesAujourdhui = seances.filter((s) => s.date === aujourdhui).length;

          const moyenne =
            notes.length > 0
              ? notes.reduce((sum, n) => sum + n.valeur, 0) / notes.length
              : 0;

          setStats({
            totalCours: inscriptionsActives.length,
            totalNotes: notes.length,
            moyenne: moyenne.toFixed(2),
            seancesAujourdhui,
          });
        }
      } catch (error) {
        console.error('Erreur lors du chargement des statistiques:', error);
      } finally {
        setLoading(false);
      }
    };

    loadStats();
  }, [user]);

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
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Dashboard Étudiant</h1>
          <p className="mt-2 text-gray-600">Bienvenue, {user?.username}</p>
        </div>

        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center">
              <div className="p-3 bg-blue-100 rounded-lg">
                <BookOpen className="h-6 w-6 text-blue-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Mes Cours</p>
                <p className="text-2xl font-bold text-gray-900">{stats.totalCours}</p>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center">
              <div className="p-3 bg-green-100 rounded-lg">
                <FileText className="h-6 w-6 text-green-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Notes Reçues</p>
                <p className="text-2xl font-bold text-gray-900">{stats.totalNotes}</p>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center">
              <div className="p-3 bg-purple-100 rounded-lg">
                <TrendingUp className="h-6 w-6 text-purple-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Moyenne</p>
                <p className="text-2xl font-bold text-gray-900">
                  {stats.moyenne > 0 ? stats.moyenne : 'N/A'}
                </p>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center">
              <div className="p-3 bg-orange-100 rounded-lg">
                <Calendar className="h-6 w-6 text-orange-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Séances Aujourd'hui</p>
                <p className="text-2xl font-bold text-gray-900">{stats.seancesAujourdhui}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Layout>
  );
};

export default EtudiantDashboard;

