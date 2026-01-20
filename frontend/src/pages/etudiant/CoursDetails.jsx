import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext.jsx';
import { coursApi, seancesApi, notesApi } from '../../utils/api.js';
import Layout from '../../components/Layout.jsx';
import { BookOpen, Calendar, Clock, MapPin, ArrowLeft, FileText, User } from 'lucide-react';

const EtudiantCoursDetails = () => {
  const { code } = useParams();
  const { user } = useAuth();
  const [cours, setCours] = useState(null);
  const [seances, setSeances] = useState([]);
  const [maNote, setMaNote] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadData = async () => {
      try {
        const coursResponse = await coursApi.getByCode(code);
        setCours(coursResponse.data);

        // Charger les séances du cours
        const seancesResponse = await seancesApi.getByCours(code);
        const seancesData = Array.isArray(seancesResponse.data) 
          ? seancesResponse.data 
          : Array.isArray(seancesResponse) 
            ? seancesResponse 
            : [];
        if (Array.isArray(seancesData)) {
          seancesData.sort((a, b) => {
            const dateA = new Date(`${a.date}T${a.heure}`);
            const dateB = new Date(`${b.date}T${b.heure}`);
            return dateA - dateB;
          });
        }
        setSeances(seancesData);

        // Charger ma note si elle existe
        const etudiantId = user?.etudiantId || user?.userId || user?.id;
        if (etudiantId) {
          const notesResponse = await notesApi.getByEtudiant(etudiantId);
          const notes = Array.isArray(notesResponse.data) 
            ? notesResponse.data 
            : Array.isArray(notesResponse) 
              ? notesResponse 
              : [];
          const noteCours = Array.isArray(notes) 
            ? notes.find((n) => n.cours?.code === code) 
            : null;
          setMaNote(noteCours);
        }
      } catch (error) {
        console.error('Erreur lors du chargement:', error);
      } finally {
        setLoading(false);
      }
    };

    if (code) {
      loadData();
    }
  }, [code, user]);

  if (loading) {
    return (
      <Layout>
        <div className="flex justify-center items-center h-64">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
        </div>
      </Layout>
    );
  }

  if (!cours) {
    return (
      <Layout>
        <div className="text-center py-12">
          <p className="text-gray-600">Cours non trouvé</p>
          <Link to="/etudiant/cours" className="text-primary-600 hover:underline mt-4 inline-block">
            Retour à la liste
          </Link>
        </div>
      </Layout>
    );
  }

  const formatDate = (dateStr) => {
    const date = new Date(dateStr);
    return date.toLocaleDateString('fr-FR', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  return (
    <Layout>
      <div className="space-y-6">
        {/* Header */}
        <div>
          <Link
            to="/etudiant/cours"
            className="flex items-center text-gray-600 hover:text-gray-900 mb-2"
          >
            <ArrowLeft className="h-4 w-4 mr-2" />
            Retour
          </Link>
          <h1 className="text-3xl font-bold text-gray-900">{cours.titre}</h1>
          <p className="mt-2 text-gray-600">Code: {cours.code}</p>
        </div>

        {/* Informations du cours */}
        <div className="bg-white rounded-lg shadow p-6">
          <h2 className="text-xl font-semibold mb-4">Informations du cours</h2>
          {cours.description && (
            <p className="text-gray-600 mb-4">{cours.description}</p>
          )}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {cours.formateur && (
              <div className="flex items-center text-gray-600">
                <User className="h-5 w-5 mr-2" />
                <span>Formateur: {cours.formateur.nom}</span>
              </div>
            )}
            {cours.session && (
              <div className="flex items-center text-gray-600">
                <Calendar className="h-5 w-5 mr-2" />
                <span>
                  {cours.session.semestre} - {cours.session.anneeScolaire}
                </span>
              </div>
            )}
          </div>
        </div>

        {/* Ma note */}
        {maNote && (
          <div className="bg-gradient-to-r from-primary-500 to-primary-600 rounded-lg shadow-lg p-6 text-white">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-primary-100 text-sm font-medium">Ma note</p>
                <p className="text-4xl font-bold mt-2">{maNote.valeur}/20</p>
                <p className="text-primary-100 text-sm mt-2">
                  Date: {new Date(maNote.dateSaisie).toLocaleDateString('fr-FR')}
                </p>
              </div>
              <FileText className="h-12 w-12 text-primary-200" />
            </div>
          </div>
        )}

        {/* Séances */}
        <div className="bg-white rounded-lg shadow">
          <div className="px-6 py-4 border-b border-gray-200">
            <h2 className="text-xl font-semibold flex items-center">
              <Calendar className="h-5 w-5 mr-2" />
              Séances du cours
            </h2>
          </div>
          {seances.length === 0 ? (
            <div className="p-12 text-center text-gray-500">
              Aucune séance planifiée
            </div>
          ) : (
            <div className="divide-y divide-gray-200">
              {seances.map((seance) => (
                <div key={seance.id} className="p-6 hover:bg-gray-50 transition-colors">
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <div className="flex flex-wrap gap-4 text-sm text-gray-600 mb-2">
                        <div className="flex items-center">
                          <Calendar className="h-4 w-4 mr-2" />
                          <span>{formatDate(seance.date)}</span>
                        </div>
                        <div className="flex items-center">
                          <Clock className="h-4 w-4 mr-2" />
                          <span>{seance.heure}</span>
                        </div>
                        {seance.salle && (
                          <div className="flex items-center">
                            <MapPin className="h-4 w-4 mr-2" />
                            <span>Salle: {seance.salle}</span>
                          </div>
                        )}
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </Layout>
  );
};

export default EtudiantCoursDetails;

