import { useEffect, useState, useMemo } from 'react';
import { useAuth } from '../../context/AuthContext.jsx';
import { inscriptionsApi, notesApi, seancesApi, etudiantsApi } from '../../utils/api.js';
import { parseJsonSafely } from '../../utils/jsonParser.js';
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

  const etudiantId = useMemo(() => user?.etudiantId || user?.userId || user?.id, [user]);

  useEffect(() => {
    if (!etudiantId) {
      setLoading(false);
      return;
    }

    const loadStats = async () => {
      try {
        const [inscriptionsResponse, notesResponse, seancesResponse, moyenneResponse] = await Promise.all([
          inscriptionsApi.getByEtudiant(etudiantId),
          notesApi.getByEtudiant(etudiantId),
          seancesApi.getEmploiDuTempsEtudiant(etudiantId),
          etudiantsApi.getMoyenne(etudiantId).catch(() => ({ data: null })),
        ]);

        // Parser les réponses
        let inscriptions = parseJsonSafely(inscriptionsResponse.data);
        if (!inscriptions) {
          inscriptions = [];
        }
        const inscriptionsArray = Array.isArray(inscriptions) ? inscriptions : [];

        let notes = parseJsonSafely(notesResponse.data);
        if (!notes) {
          notes = [];
        }
        const notesArray = Array.isArray(notes) ? notes : [];

        let seances = parseJsonSafely(seancesResponse.data);
        if (!seances) {
          seances = [];
        }
        const seancesArray = Array.isArray(seances) ? seances : [];

        const inscriptionsActives = Array.isArray(inscriptionsArray) 
          ? inscriptionsArray.filter((i) => i.status === 'ACTIVE') 
          : [];
        const aujourdhui = new Date().toISOString().split('T')[0];
        const seancesAujourdhui = seancesArray.filter((s) => s.date === aujourdhui).length;

        // Utiliser la moyenne de l'API si disponible, sinon calculer localement
        let moyenne = 0;
        if (moyenneResponse?.data) {
          const moyenneData = parseJsonSafely(moyenneResponse.data);
          if (typeof moyenneData === 'number') {
            moyenne = moyenneData;
          } else if (moyenneData?.moyenne !== undefined) {
            moyenne = moyenneData.moyenne;
          }
        }
        
        // Fallback: calculer localement si l'API ne retourne pas de moyenne
        if (moyenne === 0 && notesArray.length > 0) {
          moyenne = notesArray.reduce((sum, n) => sum + n.valeur, 0) / notesArray.length;
        }

        setStats({
          totalCours: inscriptionsActives.length,
          totalNotes: notesArray.length,
          moyenne: moyenne.toFixed(2),
          seancesAujourdhui,
        });
      } catch (error) {
        console.error('Erreur lors du chargement des statistiques:', error);
        setStats({
          totalCours: 0,
          totalNotes: 0,
          moyenne: 0,
          seancesAujourdhui: 0,
        });
      } finally {
        setLoading(false);
      }
    };

    loadStats();
  }, [etudiantId]);

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

