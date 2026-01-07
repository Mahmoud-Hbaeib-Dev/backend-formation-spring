import { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext.jsx';
import { coursService } from '../../services/coursService.js';
import { seanceService } from '../../services/seanceService.js';
import { BookOpen, Calendar, Users, TrendingUp } from 'lucide-react';
import Layout from '../../components/Layout.jsx';

const FormateurDashboard = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState({
    totalCours: 0,
    totalSeances: 0,
    seancesAujourdhui: 0,
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadStats = async () => {
      try {
        // Récupérer l'ID du formateur depuis le user
        // Note: Vous devrez peut-être adapter selon votre structure
        const formateurId = user?.formateurId || user?.userId || user?.id;
        
        if (formateurId) {
          const [cours, seances] = await Promise.all([
            coursService.getByFormateur(formateurId),
            seanceService.getByFormateur(formateurId),
          ]);

          const aujourdhui = new Date().toISOString().split('T')[0];
          const seancesAujourdhui = seances.filter(
            (s) => s.date === aujourdhui
          ).length;

          setStats({
            totalCours: cours.length,
            totalSeances: seances.length,
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
          <h1 className="text-3xl font-bold text-gray-900">Dashboard Formateur</h1>
          <p className="mt-2 text-gray-600">Bienvenue, {user?.username}</p>
        </div>

        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
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
                <Calendar className="h-6 w-6 text-green-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Total Séances</p>
                <p className="text-2xl font-bold text-gray-900">{stats.totalSeances}</p>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center">
              <div className="p-3 bg-purple-100 rounded-lg">
                <TrendingUp className="h-6 w-6 text-purple-600" />
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

export default FormateurDashboard;

