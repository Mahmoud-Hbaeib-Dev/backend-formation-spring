import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext.jsx';
import { inscriptionService } from '../../services/inscriptionService.js';
import Layout from '../../components/Layout.jsx';
import { BookOpen, CheckCircle, XCircle } from 'lucide-react';

const EtudiantCours = () => {
  const { user } = useAuth();
  const [inscriptions, setInscriptions] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadInscriptions = async () => {
      try {
        const etudiantId = user?.etudiantId || user?.userId || user?.id;
        if (etudiantId) {
          const data = await inscriptionService.getByEtudiant(etudiantId);
          setInscriptions(data);
        }
      } catch (error) {
        console.error('Erreur lors du chargement des cours:', error);
      } finally {
        setLoading(false);
      }
    };

    loadInscriptions();
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

  const inscriptionsActives = inscriptions.filter((i) => i.status === 'ACTIVE');

  return (
    <Layout>
      <div className="space-y-6">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Mes Cours</h1>
          <p className="mt-2 text-gray-600">Liste de tous vos cours inscrits</p>
        </div>

        {inscriptionsActives.length === 0 ? (
          <div className="bg-white rounded-lg shadow p-12 text-center">
            <BookOpen className="h-16 w-16 text-gray-400 mx-auto mb-4" />
            <p className="text-gray-600">Aucun cours inscrit</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {inscriptionsActives.map((inscription) => (
              <div
                key={inscription.id}
                className="bg-white rounded-lg shadow hover:shadow-lg transition-shadow p-6"
              >
                <div className="flex items-start justify-between mb-4">
                  <div className="flex-1">
                    <h3 className="text-lg font-semibold text-gray-900">
                      {inscription.cours?.titre}
                    </h3>
                    <p className="text-sm text-gray-500 mt-1">
                      Code: {inscription.cours?.code}
                    </p>
                  </div>
                  <div className="ml-4 flex items-center space-x-2">
                    {inscription.status === 'ACTIVE' ? (
                      <CheckCircle className="h-5 w-5 text-green-500" />
                    ) : (
                      <XCircle className="h-5 w-5 text-red-500" />
                    )}
                    <Link
                      to={`/etudiant/cours/${inscription.cours?.code}`}
                      className="px-3 py-1 bg-primary-600 text-white text-sm rounded hover:bg-primary-700 transition-colors"
                    >
                      Détails
                    </Link>
                  </div>
                </div>
                {inscription.cours?.description && (
                  <p className="text-sm text-gray-600 mb-4 line-clamp-2">
                    {inscription.cours.description}
                  </p>
                )}
                <div className="flex items-center justify-between text-sm">
                  <span className="text-gray-500">
                    Inscrit le {new Date(inscription.dateInscription).toLocaleDateString('fr-FR')}
                  </span>
                  <span
                    className={`px-2 py-1 rounded ${
                      inscription.status === 'ACTIVE'
                        ? 'bg-green-100 text-green-800'
                        : 'bg-red-100 text-red-800'
                    }`}
                  >
                    {inscription.status}
                  </span>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </Layout>
  );
};

export default EtudiantCours;

