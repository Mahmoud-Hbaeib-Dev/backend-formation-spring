import { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext.jsx';
import { statistiquesApi } from '../../utils/api.js';
import { parseJsonSafely } from '../../utils/jsonParser.js';
import Layout from '../../components/Layout.jsx';
import { BarChart3, TrendingUp, Users, BookOpen } from 'lucide-react';

const FormateurStatistiques = () => {
  const { user } = useAuth();
  const [dashboardStats, setDashboardStats] = useState(null);
  const [coursPlusSuivis, setCoursPlusSuivis] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadStats = async () => {
      try {
        const [dashboardResponse, coursSuivisResponse] = await Promise.all([
          statistiquesApi.getDashboard(),
          statistiquesApi.getCoursPlusSuivis(),
        ]);
        
        let dashboardData = parseJsonSafely(dashboardResponse.data);
        if (!dashboardData) {
          dashboardData = null;
        }
        setDashboardStats(dashboardData);
        
        let coursSuivis = parseJsonSafely(coursSuivisResponse.data);
        if (!coursSuivis) {
          coursSuivis = [];
        }
        const coursSuivisArray = Array.isArray(coursSuivis) ? coursSuivis : [];
        setCoursPlusSuivis(coursSuivisArray);
      } catch (error) {
        console.error('Erreur lors du chargement des statistiques:', error);
      } finally {
        setLoading(false);
      }
    };

    loadStats();
  }, []);

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
          <h1 className="text-3xl font-bold text-gray-900">Statistiques</h1>
          <p className="mt-2 text-gray-600">Vue d'ensemble des statistiques du centre</p>
        </div>

        {/* Dashboard Stats */}
        {dashboardStats && (
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="bg-white rounded-lg shadow p-6">
              <div className="flex items-center">
                <div className="p-3 bg-blue-100 rounded-lg">
                  <Users className="h-6 w-6 text-blue-600" />
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-gray-600">Total Étudiants</p>
                  <p className="text-2xl font-bold text-gray-900">
                    {dashboardStats.nombreTotalEtudiants || 0}
                  </p>
                </div>
              </div>
            </div>

            <div className="bg-white rounded-lg shadow p-6">
              <div className="flex items-center">
                <div className="p-3 bg-green-100 rounded-lg">
                  <BookOpen className="h-6 w-6 text-green-600" />
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-gray-600">Total Cours</p>
                  <p className="text-2xl font-bold text-gray-900">
                    {dashboardStats.nombreTotalCours || 0}
                  </p>
                </div>
              </div>
            </div>

            <div className="bg-white rounded-lg shadow p-6">
              <div className="flex items-center">
                <div className="p-3 bg-purple-100 rounded-lg">
                  <TrendingUp className="h-6 w-6 text-purple-600" />
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-gray-600">Total Formateurs</p>
                  <p className="text-2xl font-bold text-gray-900">
                    {dashboardStats.nombreTotalFormateurs || 0}
                  </p>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Cours les plus suivis */}
        <div className="bg-white rounded-lg shadow">
          <div className="px-6 py-4 border-b border-gray-200">
            <h2 className="text-xl font-semibold flex items-center">
              <BarChart3 className="h-5 w-5 mr-2" />
              Cours les plus suivis
            </h2>
          </div>
          {coursPlusSuivis.length === 0 ? (
            <div className="p-12 text-center text-gray-500">
              Aucune donnée disponible
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Cours
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Code
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Nombre d'inscriptions
                    </th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {coursPlusSuivis.map((cours, index) => (
                    <tr key={cours.code || index} className="hover:bg-gray-50">
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm font-medium text-gray-900">
                          {cours.titre || cours.cours?.titre || 'N/A'}
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {cours.code || cours.cours?.code || 'N/A'}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className="px-2 py-1 text-sm font-semibold bg-blue-100 text-blue-800 rounded">
                          {cours.nombreInscriptions || cours.count || 0}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </Layout>
  );
};

export default FormateurStatistiques;

