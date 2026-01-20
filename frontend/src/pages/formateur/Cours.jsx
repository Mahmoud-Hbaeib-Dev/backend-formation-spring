import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext.jsx';
import { formateursApi } from '../../utils/api.js';
import { parseJsonSafely } from '../../utils/jsonParser.js';
import Layout from '../../components/Layout.jsx';
import { BookOpen, Clock, Users } from 'lucide-react';

const FormateurCours = () => {
  const { user } = useAuth();
  const [cours, setCours] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadCours = async () => {
      try {
        console.log('📚 [FORMATEUR COURS] user:', user);
        const formateurId = user?.formateurId || user?.userId || user?.id;
        console.log('📚 [FORMATEUR COURS] formateurId utilisé:', formateurId);

        if (formateurId) {
          const response = await formateursApi.getCours(formateurId);
          console.log('📚 [FORMATEUR COURS] Réponse getCours:', response);
          
          // Parser la réponse si elle est une chaîne JSON
          let data = parseJsonSafely(response.data);
          if (!data) {
            console.warn('⚠️ [FORMATEUR COURS] Impossible de parser les cours');
            data = [];
          } else {
            console.log('✅ [FORMATEUR COURS] Cours parsés:', data);
          }
          
          const coursArray = Array.isArray(data) ? data : [];
          setCours(coursArray);
        } else {
          console.warn('[FORMATEUR COURS] Aucun formateurId trouvé dans le user');
        }
      } catch (error) {
        console.error('Erreur lors du chargement des cours:', error);
      } finally {
        setLoading(false);
      }
    };

    loadCours();
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
          <h1 className="text-3xl font-bold text-gray-900">Mes Cours</h1>
          <p className="mt-2 text-gray-600">Liste de tous vos cours</p>
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

