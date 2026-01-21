import { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext.jsx';
import { coursApi, inscriptionsApi } from '../../utils/api.js';
import { parseJsonSafely } from '../../utils/jsonParser.js';
import Layout from '../../components/Layout.jsx';
import { BookOpen, Plus, CheckCircle, X, Trash2 } from 'lucide-react';

const EtudiantInscriptionCours = () => {
  const { user } = useAuth();
  const [coursDisponibles, setCoursDisponibles] = useState([]);
  const [mesInscriptions, setMesInscriptions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [inscribing, setInscribing] = useState(null);
  const [desinscribing, setDesinscribing] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    const loadData = async () => {
      try {
        const etudiantId = user?.etudiantId || user?.userId || user?.id;
        console.log('🔍 [ETUDIANT INSCRIPTION] EtudiantId utilisé:', etudiantId);
        if (etudiantId) {
          const [allCoursResponse, inscriptionsResponse] = await Promise.all([
            coursApi.list(),
            inscriptionsApi.getByEtudiant(etudiantId),
          ]);

          console.log('🔍 [ETUDIANT INSCRIPTION] Réponse cours:', allCoursResponse);
          console.log('🔍 [ETUDIANT INSCRIPTION] Réponse inscriptions:', inscriptionsResponse);

          // Parser les réponses
          let allCours = parseJsonSafely(allCoursResponse.data);
          if (!allCours) {
            allCours = [];
          }
          const allCoursArray = Array.isArray(allCours) ? allCours : [];
          console.log(`📊 [ETUDIANT INSCRIPTION] Nombre de cours disponibles: ${allCoursArray.length}`);

          let inscriptions = parseJsonSafely(inscriptionsResponse.data);
          if (!inscriptions) {
            inscriptions = [];
          }
          const inscriptionsArray = Array.isArray(inscriptions) ? inscriptions : [];
          console.log(`📊 [ETUDIANT INSCRIPTION] Nombre d'inscriptions: ${inscriptionsArray.length}`);

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
          
          console.log(`📊 [ETUDIANT INSCRIPTION] Cours non inscrits: ${coursNonInscrits.length}`);
          setCoursDisponibles(coursNonInscrits);
        }
      } catch (error) {
        console.error('❌ [ETUDIANT INSCRIPTION] Erreur lors du chargement:', error);
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, [user]);

  const reloadData = async () => {
    const etudiantId = user?.etudiantId || user?.userId || user?.id;
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
  };

  const handleInscription = async (coursCode) => {
    setError('');
    setInscribing(coursCode);

    try {
      const etudiantId = user?.etudiantId || user?.userId || user?.id;
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

