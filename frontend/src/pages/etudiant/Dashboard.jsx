import { useEffect, useState, useMemo } from 'react';
import { useAuth } from '../../context/AuthContext.jsx';
import { inscriptionsApi, notesApi, seancesApi, etudiantsApi } from '../../utils/api.js';
import { parseJsonSafely } from '../../utils/jsonParser.js';
import Layout from '../../components/Layout.jsx';
import { BookOpen, FileText, Calendar, TrendingUp, Sparkles, Award } from 'lucide-react';

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

        let moyenne = 0;
        if (moyenneResponse?.data) {
          const moyenneData = parseJsonSafely(moyenneResponse.data);
          if (typeof moyenneData === 'number') {
            moyenne = moyenneData;
          } else if (moyenneData?.moyenne !== undefined) {
            moyenne = moyenneData.moyenne;
          }
        }
        
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

  const getMoyenneColor = (moyenne) => {
    if (moyenne >= 16) return 'from-green-500 to-emerald-600';
    if (moyenne >= 14) return 'from-blue-500 to-blue-600';
    if (moyenne >= 10) return 'from-yellow-500 to-orange-600';
    return 'from-red-500 to-red-600';
  };

  const statCards = [
    {
      title: 'Mes Cours',
      value: stats.totalCours,
      icon: BookOpen,
      gradient: 'from-blue-500 to-blue-600',
      iconBg: 'bg-blue-100',
      iconColor: 'text-blue-600',
    },
    {
      title: 'Notes Reçues',
      value: stats.totalNotes,
      icon: FileText,
      gradient: 'from-green-500 to-green-600',
      iconBg: 'bg-green-100',
      iconColor: 'text-green-600',
    },
    {
      title: 'Moyenne',
      value: stats.moyenne > 0 ? stats.moyenne : 'N/A',
      icon: Award,
      gradient: getMoyenneColor(parseFloat(stats.moyenne)),
      iconBg: stats.moyenne > 0 ? (parseFloat(stats.moyenne) >= 10 ? 'bg-green-100' : 'bg-yellow-100') : 'bg-gray-100',
      iconColor: stats.moyenne > 0 ? (parseFloat(stats.moyenne) >= 10 ? 'text-green-600' : 'text-yellow-600') : 'text-gray-600',
      highlight: stats.moyenne > 0,
    },
    {
      title: 'Séances Aujourd\'hui',
      value: stats.seancesAujourdhui,
      icon: Calendar,
      gradient: 'from-purple-500 to-purple-600',
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
              Dashboard Étudiant
            </h1>
            <p className="mt-2 text-lg text-gray-600">
              Bienvenue, <span className="font-semibold text-primary-600">{user?.username}</span>
            </p>
          </div>
        </div>

        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {statCards.map((stat, index) => {
            const Icon = stat.icon;
            return (
              <div
                key={stat.title}
                className={`stat-card card-hover animate-slide-up ${stat.highlight ? 'ring-2 ring-primary-200' : ''}`}
                style={{ animationDelay: `${index * 100}ms` }}
              >
                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-4">
                    <div className={`${stat.iconBg} p-4 rounded-xl shadow-sm`}>
                      <Icon className={`h-7 w-7 ${stat.iconColor}`} />
                    </div>
                    <div>
                      <p className="text-sm font-medium text-gray-600 mb-1">{stat.title}</p>
                      <p className={`text-3xl font-bold ${stat.highlight ? `bg-gradient-to-r ${stat.gradient} bg-clip-text text-transparent` : 'text-gray-900'}`}>
                        {stat.value}
                        {stat.title === 'Moyenne' && stat.value !== 'N/A' && <span className="text-lg text-gray-500">/20</span>}
                      </p>
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
              href="/etudiant/inscription"
              className="flex items-center space-x-3 p-4 bg-gradient-to-r from-primary-50 to-primary-100 rounded-lg hover:from-primary-100 hover:to-primary-200 transition-all duration-200 group"
            >
              <div className="bg-primary-600 p-2 rounded-lg group-hover:scale-110 transition-transform">
                <BookOpen className="h-5 w-5 text-white" />
              </div>
              <span className="font-medium text-gray-700 group-hover:text-primary-700">S'inscrire à un cours</span>
            </a>
            <a
              href="/etudiant/notes"
              className="flex items-center space-x-3 p-4 bg-gradient-to-r from-green-50 to-green-100 rounded-lg hover:from-green-100 hover:to-green-200 transition-all duration-200 group"
            >
              <div className="bg-green-600 p-2 rounded-lg group-hover:scale-110 transition-transform">
                <FileText className="h-5 w-5 text-white" />
              </div>
              <span className="font-medium text-gray-700 group-hover:text-green-700">Voir mes notes</span>
            </a>
            <a
              href="/etudiant/planning"
              className="flex items-center space-x-3 p-4 bg-gradient-to-r from-purple-50 to-purple-100 rounded-lg hover:from-purple-100 hover:to-purple-200 transition-all duration-200 group"
            >
              <div className="bg-purple-600 p-2 rounded-lg group-hover:scale-110 transition-transform">
                <Calendar className="h-5 w-5 text-white" />
              </div>
              <span className="font-medium text-gray-700 group-hover:text-purple-700">Mon planning</span>
            </a>
          </div>
        </div>
      </div>
    </Layout>
  );
};

export default EtudiantDashboard;
