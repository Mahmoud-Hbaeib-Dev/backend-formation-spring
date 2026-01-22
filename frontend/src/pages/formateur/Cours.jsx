import { useEffect, useState, useMemo } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext.jsx';
import { formateursApi, coursApi } from '../../utils/api.js';
import { parseJsonSafely } from '../../utils/jsonParser.js';
import { useDebounce } from '../../hooks/useDebounce.js';
import Layout from '../../components/Layout.jsx';
import { BookOpen, Clock, Users, Search, X } from 'lucide-react';

const FormateurCours = () => {
  const { user } = useAuth();
  const [cours, setCours] = useState([]);
  const [allCours, setAllCours] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearchTerm = useDebounce(searchTerm, 400);
  
  const formateurId = useMemo(() => user?.formateurId || user?.userId || user?.id, [user]);

  useEffect(() => {
    if (!formateurId) {
      setLoading(false);
      return;
    }

    const loadCours = async () => {
      try {
        const response = await formateursApi.getCours(formateurId);
        let data = parseJsonSafely(response.data);
        if (!data) {
          data = [];
        }
        const coursArray = Array.isArray(data) ? data : [];
        setCours(coursArray);
        setAllCours(coursArray);
      } catch (error) {
        console.error('Erreur lors du chargement des cours:', error);
        setCours([]);
        setAllCours([]);
      } finally {
        setLoading(false);
      }
    };

    loadCours();
  }, [formateurId]);

  // Recherche de cours avec debouncing
  useEffect(() => {
    if (!debouncedSearchTerm.trim()) {
      setCours(allCours);
      return;
    }

    const performSearch = async () => {
      try {
        const response = await coursApi.searchByTitre(debouncedSearchTerm);
        let data = parseJsonSafely(response.data);
        if (!data) {
          data = [];
        }
        const searchResults = Array.isArray(data) ? data : [];
        
        // Filtrer pour ne garder que les cours du formateur
        const filteredResults = searchResults.filter(
          c => c.formateur?.id === formateurId || c.formateurInfo?.id === formateurId
        );
        
        setCours(filteredResults);
      } catch (error) {
        console.error('Erreur lors de la recherche:', error);
        // En cas d'erreur, filtrer localement
        const filtered = allCours.filter(
          c => c.titre?.toLowerCase().includes(debouncedSearchTerm.toLowerCase()) ||
               c.code?.toLowerCase().includes(debouncedSearchTerm.toLowerCase())
        );
        setCours(filtered);
      }
    };

    performSearch();
  }, [debouncedSearchTerm, allCours, formateurId]);

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
            <h1 className="text-3xl font-bold text-gray-900">Mes Cours</h1>
            <p className="mt-2 text-gray-600">Liste de tous vos cours</p>
          </div>
          <Link
            to="/formateur/cours/new"
            className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors"
          >
            Créer un cours
          </Link>
        </div>

        {/* Barre de recherche */}
        <div className="bg-white rounded-lg shadow p-4">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
            <input
              type="text"
              placeholder="Rechercher un cours par titre ou code..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-10 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary-500 focus:border-primary-500"
            />
            {searchTerm && (
              <button
                onClick={() => setSearchTerm('')}
                className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600"
              >
                <X className="h-5 w-5" />
              </button>
            )}
          </div>
        </div>

        {cours.length === 0 ? (
          <div className="bg-white rounded-lg shadow p-12 text-center">
            <BookOpen className="h-16 w-16 text-gray-400 mx-auto mb-4" />
            <p className="text-gray-600">Aucun cours assigné</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {cours.map((c) => (
              <div key={c.code} className="bg-white rounded-lg shadow hover:shadow-lg transition-shadow p-6">
                <div className="flex items-start justify-between mb-4">
                  <div className="flex-1">
                    <h3 className="text-lg font-semibold text-gray-900">{c.titre}</h3>
                    <p className="text-sm text-gray-500 mt-1">Code: {c.code}</p>
                  </div>
                  <Link
                    to={`/formateur/cours/${c.code}`}
                    className="ml-4 px-4 py-2 bg-primary-600 text-white text-sm rounded-lg hover:bg-primary-700 transition-colors"
                  >
                    Détails
                  </Link>
                </div>
                {c.description && (
                  <p className="text-sm text-gray-600 mb-4 line-clamp-2">{c.description}</p>
                )}
                <div className="flex items-center space-x-4 text-sm text-gray-500">
                  {c.session && (
                    <div className="flex items-center">
                      <Clock className="h-4 w-4 mr-1" />
                      <span>{c.session.semestre} - {c.session.anneeScolaire}</span>
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </Layout>
  );
};

export default FormateurCours;

