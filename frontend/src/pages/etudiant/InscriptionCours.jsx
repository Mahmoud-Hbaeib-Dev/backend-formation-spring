import { useEffect, useState, useMemo } from 'react';
import { useAuth } from '../../context/AuthContext.jsx';
import { coursApi, inscriptionsApi } from '../../utils/api.js';
import { parseJsonSafely } from '../../utils/jsonParser.js';
import { useDebounce } from '../../hooks/useDebounce.js';
import Layout from '../../components/Layout.jsx';
import { BookOpen, Plus, CheckCircle, X, Trash2, Search } from 'lucide-react';

const EtudiantInscriptionCours = () => {
  const { user } = useAuth();
  const [coursDisponibles, setCoursDisponibles] = useState([]);
  const [allCoursDisponibles, setAllCoursDisponibles] = useState([]);
  const [mesInscriptions, setMesInscriptions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [inscribing, setInscribing] = useState(null);
  const [desinscribing, setDesinscribing] = useState(null);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearchTerm = useDebounce(searchTerm, 400);
  
  const etudiantId = useMemo(() => user?.etudiantId || user?.userId || user?.id, [user]);

  useEffect(() => {
    if (!etudiantId) {
      setLoading(false);
      return;
    }

    const loadData = async () => {
      try {
        const [allCoursResponse, inscriptionsResponse] = await Promise.all([
          coursApi.list(),
          inscriptionsApi.getByEtudiant(etudiantId),
        ]);

        // Parser les réponses
        let allCours = parseJsonSafely(allCoursResponse.data);
        if (!allCours) {
          allCours = [];
        }
        const allCoursArray = Array.isArray(allCours) ? allCours : [];

        let inscriptions = parseJsonSafely(inscriptionsResponse.data);
        if (!inscriptions) {
          inscriptions = [];
        }
        const inscriptionsArray = Array.isArray(inscriptions) ? inscriptions : [];

        setMesInscriptions(inscriptionsArray);
        
        // Filtrer les cours où l'étudiant n'est pas déjà inscrit
        const inscriptionsActives = Array.isArray(inscriptionsArray)
          ? inscriptionsArray
              .filter((i) => i.status === 'ACTIVE')
              .map((i) => i.cours?.code)
          : [];
        
        const coursNonInscrits = Array.isArray(allCoursArray)
          ? allCoursArray.filter((c) => !inscriptionsActives.includes(c.code))
          : [];
        
        setCoursDisponibles(coursNonInscrits);
        setAllCoursDisponibles(coursNonInscrits);
      } catch (error) {
        console.error('Erreur lors du chargement:', error);
        setCoursDisponibles([]);
        setAllCoursDisponibles([]);
        setMesInscriptions([]);
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, [etudiantId]);

  // Recherche de cours avec debouncing
  useEffect(() => {
    if (!debouncedSearchTerm.trim()) {
      setCoursDisponibles(allCoursDisponibles);
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
        
        // Filtrer pour ne garder que les cours non inscrits
        const inscriptionsActives = Array.isArray(mesInscriptions)
          ? mesInscriptions
              .filter((i) => i.status === 'ACTIVE')
              .map((i) => i.cours?.code)
          : [];
        
        const filteredResults = searchResults.filter(
          c => !inscriptionsActives.includes(c.code)
        );
        
        setCoursDisponibles(filteredResults);
      } catch (error) {
        console.error('Erreur lors de la recherche:', error);
        // En cas d'erreur, filtrer localement
        const filtered = allCoursDisponibles.filter(
          c => c.titre?.toLowerCase().includes(debouncedSearchTerm.toLowerCase()) ||
               c.code?.toLowerCase().includes(debouncedSearchTerm.toLowerCase())
        );
        setCoursDisponibles(filtered);
      }
    };

    performSearch();
  }, [debouncedSearchTerm, allCoursDisponibles, mesInscriptions]);

  const reloadData = async () => {
    if (!etudiantId) return;

    const [allCoursResponse, inscriptionsResponse] = await Promise.all([
      coursApi.list(),
      inscriptionsApi.getByEtudiant(etudiantId),
    ]);

    // Parser les réponses
    let allCours = parseJsonSafely(allCoursResponse.data);
    if (!allCours) {
      allCours = [];
    }
    const allCoursArray = Array.isArray(allCours) ? allCours : [];

    let inscriptions = parseJsonSafely(inscriptionsResponse.data);
    if (!inscriptions) {
      inscriptions = [];
    }
    const inscriptionsArray = Array.isArray(inscriptions) ? inscriptions : [];

    setMesInscriptions(inscriptionsArray);
    
    const inscriptionsActives = Array.isArray(inscriptionsArray)
      ? inscriptionsArray
          .filter((i) => i.status === 'ACTIVE')
          .map((i) => i.cours?.code)
      : [];
    
    const coursNonInscrits = Array.isArray(allCoursArray)
      ? allCoursArray.filter((c) => !inscriptionsActives.includes(c.code))
      : [];
    
    setCoursDisponibles(coursNonInscrits);
    setAllCoursDisponibles(coursNonInscrits);
  };

  const handleInscription = async (coursCode) => {
    setError('');
    setInscribing(coursCode);

    try {
      await inscriptionsApi.inscrire(etudiantId, coursCode);
      await reloadData();
    } catch (err) {
      setError(
        err.response?.data?.message ||
        err.response?.data?.error ||
        'Erreur lors de l\'inscription'
      );
    } finally {
      setInscribing(null);
    }
  };

  const handleDesinscription = async (inscriptionId) => {
    if (!window.confirm('Êtes-vous sûr de vouloir vous désinscrire de ce cours ?')) {
      return;
    }

    setError('');
    setDesinscribing(inscriptionId);

    try {
      await inscriptionsApi.desinscrire(inscriptionId);
      await reloadData();
    } catch (err) {
      setError(
        err.response?.data?.message ||
        err.response?.data?.error ||
        'Erreur lors de la désinscription'
      );
    } finally {
      setDesinscribing(null);
    }
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

  // Calculer les inscriptions actives avant le rendu
  const inscriptionsActives = Array.isArray(mesInscriptions)
    ? mesInscriptions.filter((i) => i.status === 'ACTIVE')
    : [];

  return (
    <Layout>
      <div className="space-y-6">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Inscription aux Cours</h1>
          <p className="mt-2 text-gray-600">Choisissez les cours auxquels vous souhaitez vous inscrire</p>
        </div>

        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-md text-sm">
            {error}
          </div>
        )}

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

        {/* Mes inscriptions actives */}
        {inscriptionsActives.length > 0 && (
          <div>
            <h2 className="text-xl font-semibold text-gray-900 mb-4">Mes inscriptions</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {inscriptionsActives.map((inscription) => (
                    <div key={inscription.id} className="bg-white rounded-lg shadow hover:shadow-lg transition-shadow p-6">
                      <div className="flex items-start justify-between mb-4">
                        <div className="flex-1">
                          <h3 className="text-lg font-semibold text-gray-900">
                            {inscription.cours?.titre}
                          </h3>
                          <p className="text-sm text-gray-500 mt-1">
                            Code: {inscription.cours?.code}
                          </p>
                        </div>
                        <CheckCircle className="h-5 w-5 text-green-500 ml-2" />
                      </div>
                      {inscription.cours?.description && (
                        <p className="text-sm text-gray-600 mb-4 line-clamp-2">
                          {inscription.cours.description}
                        </p>
                      )}
                      {inscription.cours?.formateur && (
                        <p className="text-sm text-gray-500 mb-4">
                          Formateur: {inscription.cours.formateur.nom}
                        </p>
                      )}
                      <button
                        onClick={() => handleDesinscription(inscription.id)}
                        disabled={desinscribing === inscription.id}
                        className="w-full flex items-center justify-center space-x-2 px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                      >
                        {desinscribing === inscription.id ? (
                          <>
                            <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                            <span>Désinscription...</span>
                          </>
                        ) : (
                          <>
                            <Trash2 size={18} />
                            <span>Se désinscrire</span>
                          </>
                        )}
                      </button>
                    </div>
              ))}
            </div>
          </div>
        )}

        {/* Cours disponibles */}
        <div>
          <h2 className="text-xl font-semibold text-gray-900 mb-4">Cours disponibles</h2>
          {coursDisponibles.length === 0 ? (
            <div className="bg-white rounded-lg shadow p-12 text-center">
              <CheckCircle className="h-16 w-16 text-green-400 mx-auto mb-4" />
              <p className="text-gray-600">Vous êtes inscrit à tous les cours disponibles</p>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {coursDisponibles.map((cours) => (
                <div key={cours.code} className="bg-white rounded-lg shadow hover:shadow-lg transition-shadow p-6">
                  <div className="flex items-start justify-between mb-4">
                    <div className="flex-1">
                      <h3 className="text-lg font-semibold text-gray-900">{cours.titre}</h3>
                      <p className="text-sm text-gray-500 mt-1">Code: {cours.code}</p>
                    </div>
                  </div>
                  {cours.description && (
                    <p className="text-sm text-gray-600 mb-4 line-clamp-2">{cours.description}</p>
                  )}
                  {cours.formateur && (
                    <p className="text-sm text-gray-500 mb-4">
                      Formateur: {cours.formateur.nom}
                    </p>
                  )}
                  <button
                    onClick={() => handleInscription(cours.code)}
                    disabled={inscribing === cours.code}
                    className="w-full flex items-center justify-center space-x-2 px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    {inscribing === cours.code ? (
                      <>
                        <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                        <span>Inscription...</span>
                      </>
                    ) : (
                      <>
                        <Plus size={18} />
                        <span>S'inscrire</span>
                      </>
                    )}
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </Layout>
  );
};

export default EtudiantInscriptionCours;

