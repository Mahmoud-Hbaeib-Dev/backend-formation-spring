import { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext.jsx';
import { coursService } from '../../services/coursService.js';
import { inscriptionService } from '../../services/inscriptionService.js';
import Layout from '../../components/Layout.jsx';
import { BookOpen, Plus, CheckCircle } from 'lucide-react';

const EtudiantInscriptionCours = () => {
  const { user } = useAuth();
  const [coursDisponibles, setCoursDisponibles] = useState([]);
  const [mesInscriptions, setMesInscriptions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [inscribing, setInscribing] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    const loadData = async () => {
      try {
        const etudiantId = user?.etudiantId || user?.userId || user?.id;
        if (etudiantId) {
          const [allCours, inscriptions] = await Promise.all([
            coursService.getAll(),
            inscriptionService.getByEtudiant(etudiantId),
          ]);

          setMesInscriptions(inscriptions);
          
          // Filtrer les cours où l'étudiant n'est pas déjà inscrit
          const inscriptionsActives = inscriptions
            .filter((i) => i.status === 'ACTIVE')
            .map((i) => i.cours?.code);
          
          const coursNonInscrits = allCours.filter(
            (c) => !inscriptionsActives.includes(c.code)
          );
          
          setCoursDisponibles(coursNonInscrits);
        }
      } catch (error) {
        console.error('Erreur lors du chargement:', error);
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, [user]);

  const handleInscription = async (coursCode) => {
    setError('');
    setInscribing(coursCode);

    try {
      const etudiantId = user?.userId || user?.id;
      await inscriptionService.inscrire(etudiantId, coursCode);
      
      // Recharger les données
      const [allCours, inscriptions] = await Promise.all([
        coursService.getAll(),
        inscriptionService.getByEtudiant(etudiantId),
      ]);

      setMesInscriptions(inscriptions);
      
      const inscriptionsActives = inscriptions
        .filter((i) => i.status === 'ACTIVE')
        .map((i) => i.cours?.code);
      
      const coursNonInscrits = allCours.filter(
        (c) => !inscriptionsActives.includes(c.code)
      );
      
      setCoursDisponibles(coursNonInscrits);
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
          <h1 className="text-3xl font-bold text-gray-900">Inscription aux Cours</h1>
          <p className="mt-2 text-gray-600">Choisissez les cours auxquels vous souhaitez vous inscrire</p>
        </div>

        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-md text-sm">
            {error}
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

