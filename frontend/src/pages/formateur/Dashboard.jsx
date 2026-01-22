import { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext.jsx';
import { formateursApi, seancesApi } from '../../utils/api.js';
import { parseJsonSafely } from '../../utils/jsonParser.js';
import { BookOpen, Calendar, TrendingUp, Sparkles } from 'lucide-react';
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
        const formateurId = user?.formateurId || user?.userId || user?.id;

        if (formateurId) {
          const [coursResponse, seancesResponse] = await Promise.all([
            formateursApi.getCours(formateurId),
            seancesApi.getByFormateur(formateurId),
          ]);

          let coursData = parseJsonSafely(coursResponse.data);
          if (!coursData) {
            coursData = [];
          }

          let seancesData = parseJsonSafely(seancesResponse.data);
          if (!seancesData) {
            seancesData = [];
          }

          const cours = Array.isArray(coursData) ? coursData : [];
          const seances = Array.isArray(seancesData) ? seancesData : [];

          const aujourdhui = new Date().toISOString().split('T')[0];
          const seancesAujourdhui = Array.isArray(seances)
            ? seances.filter((s) => s.date === aujourdhui).length
            : 0;

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
          <div className="relative">
            <div className="animate-spin rounded-full h-16 w-16 border-4 border-primary-200 border-t-primary-600"></div>
            <div className="absolute inset-0 flex items-center justify-center">
              <Sparkles className="h-6 w-6 text-primary-600 animate-pulse" />
            </div>
          </div>
        </div>
      </Layout>
    );
  }

  const statCards = [
    {
      title: 'Mes Cours',
      value: stats.totalCours,
      icon: BookOpen,
      gradient: 'from-blue-500 to-blue-600',
      bgGradient: 'from-blue-50 to-blue-100',
      iconBg: 'bg-blue-100',
      iconColor: 'text-blue-600',
    },
    {
      title: 'Total Séances',
      value: stats.totalSeances,
      icon: Calendar,
      gradient: 'from-green-500 to-green-600',
      bgGradient: 'from-green-50 to-green-100',
      iconBg: 'bg-green-100',
      iconColor: 'text-green-600',
    },
    {
      title: 'Séances Aujourd\'hui',
      value: stats.seancesAujourdhui,
      icon: TrendingUp,
      gradient: 'from-purple-500 to-purple-600',
      bgGradient: 'from-purple-50 to-purple-100',
      iconBg: 'bg-purple-100',
      iconColor: 'text-purple-600',
    },
  ];

  return (
    <Layout>
      <div className="space-y-8 animate-fade-in">
        {/* Header */}
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h1 className="text-4xl font-bold bg-gradient-to-r from-gray-900 to-gray-700 bg-clip-text text-transparent">
              Dashboard Formateur
            </h1>
            <p className="mt-2 text-lg text-gray-600">
              Bienvenue, <span className="font-semibold text-primary-600">{user?.username}</span>
            </p>
          </div>
        </div>

        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {statCards.map((stat, index) => {
            const Icon = stat.icon;
            return (
              <div
                key={stat.title}
                className="stat-card card-hover animate-slide-up"
                style={{ animationDelay: `${index * 100}ms` }}
              >
                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-4">
                    <div className={`${stat.iconBg} p-4 rounded-xl shadow-sm`}>
                      <Icon className={`h-7 w-7 ${stat.iconColor}`} />
                    </div>
                    <div>
                      <p className="text-sm font-medium text-gray-600 mb-1">{stat.title}</p>
                      <p className="text-3xl font-bold text-gray-900">{stat.value}</p>
                    </div>
                  </div>
                </div>
                <div className={`absolute bottom-0 right-0 w-24 h-24 bg-gradient-to-br ${stat.gradient} opacity-10 rounded-full -mr-12 -mb-12`}></div>
              </div>
            );
          })}
        </div>

        {/* Quick Actions */}
        <div className="card p-6">
          <h2 className="text-xl font-bold text-gray-900 mb-4">Actions Rapides</h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            <a
              href="/formateur/cours/new"
              className="flex items-center space-x-3 p-4 bg-gradient-to-r from-primary-50 to-primary-100 rounded-lg hover:from-primary-100 hover:to-primary-200 transition-all duration-200 group"
            >
              <div className="bg-primary-600 p-2 rounded-lg group-hover:scale-110 transition-transform">
                <BookOpen className="h-5 w-5 text-white" />
              </div>
              <span className="font-medium text-gray-700 group-hover:text-primary-700">Créer un cours</span>
            </a>
            <a
              href="/formateur/seances/new"
              className="flex items-center space-x-3 p-4 bg-gradient-to-r from-green-50 to-green-100 rounded-lg hover:from-green-100 hover:to-green-200 transition-all duration-200 group"
            >
              <div className="bg-green-600 p-2 rounded-lg group-hover:scale-110 transition-transform">
                <Calendar className="h-5 w-5 text-white" />
              </div>
              <span className="font-medium text-gray-700 group-hover:text-green-700">Créer une séance</span>
            </a>
            <a
              href="/formateur/notes"
              className="flex items-center space-x-3 p-4 bg-gradient-to-r from-purple-50 to-purple-100 rounded-lg hover:from-purple-100 hover:to-purple-200 transition-all duration-200 group"
            >
              <div className="bg-purple-600 p-2 rounded-lg group-hover:scale-110 transition-transform">
                <TrendingUp className="h-5 w-5 text-white" />
              </div>
              <span className="font-medium text-gray-700 group-hover:text-purple-700">Gérer les notes</span>
            </a>
          </div>
        </div>
      </div>
    </Layout>
  );
};

export default FormateurDashboard;
