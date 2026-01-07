import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { coursService } from '../../services/coursService.js';
import { noteService } from '../../services/noteService.js';
import { inscriptionService } from '../../services/inscriptionService.js';
import Layout from '../../components/Layout.jsx';
import { BookOpen, Users, FileText, ArrowLeft, BarChart3 } from 'lucide-react';

const FormateurCoursDetails = () => {
  const { code } = useParams();
  const [cours, setCours] = useState(null);
  const [etudiants, setEtudiants] = useState([]);
  const [notes, setNotes] = useState([]);
  const [statistiques, setStatistiques] = useState(null);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('etudiants');

  useEffect(() => {
    const loadData = async () => {
      try {
        const [coursData, etudiantsData, notesData, statsData] = await Promise.all([
          coursService.getByCode(code),
          coursService.getEtudiants(code),
          coursService.getNotes(code),
          coursService.getStatistiques(code),
        ]);
        setCours(coursData);
        setEtudiants(etudiantsData);
        setNotes(notesData);
        setStatistiques(statsData);
      } catch (error) {
        console.error('Erreur lors du chargement:', error);
      } finally {
        setLoading(false);
      }
    };

    if (code) {
      loadData();
    }
  }, [code]);

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
          <Link to="/formateur/cours" className="text-primary-600 hover:underline mt-4 inline-block">
            Retour à la liste
          </Link>
        </div>
      </Layout>
    );
  }

  return (
    <Layout>
      <div className="space-y-6">
        {/* Header */}
        <div className="flex items-center justify-between">
          <div>
            <Link
              to="/formateur/cours"
              className="flex items-center text-gray-600 hover:text-gray-900 mb-2"
            >
              <ArrowLeft className="h-4 w-4 mr-2" />
              Retour
            </Link>
            <h1 className="text-3xl font-bold text-gray-900">{cours.titre}</h1>
            <p className="mt-2 text-gray-600">Code: {cours.code}</p>
          </div>
        </div>

        {/* Statistiques */}
        {statistiques && (
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            <div className="bg-white rounded-lg shadow p-4">
              <p className="text-sm text-gray-600">Moyenne</p>
              <p className="text-2xl font-bold text-gray-900">
                {statistiques.moyenne ? statistiques.moyenne.toFixed(2) : 'N/A'}/20
              </p>
            </div>
            <div className="bg-white rounded-lg shadow p-4">
              <p className="text-sm text-gray-600">Taux de réussite</p>
              <p className="text-2xl font-bold text-gray-900">
                {statistiques.tauxReussite ? statistiques.tauxReussite.toFixed(1) : 'N/A'}%
              </p>
            </div>
            <div className="bg-white rounded-lg shadow p-4">
              <p className="text-sm text-gray-600">Inscriptions</p>
              <p className="text-2xl font-bold text-gray-900">
                {statistiques.nombreInscriptions || 0}
              </p>
            </div>
            <div className="bg-white rounded-lg shadow p-4">
              <p className="text-sm text-gray-600">Étudiants notés</p>
              <p className="text-2xl font-bold text-gray-900">
                {statistiques.nombreEtudiantsNotes || 0}
              </p>
            </div>
          </div>
        )}

        {/* Tabs */}
        <div className="bg-white rounded-lg shadow">
          <div className="border-b border-gray-200">
            <nav className="flex -mb-px">
              <button
                onClick={() => setActiveTab('etudiants')}
                className={`px-6 py-3 text-sm font-medium border-b-2 ${
                  activeTab === 'etudiants'
                    ? 'border-primary-500 text-primary-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
              >
                <Users className="h-4 w-4 inline mr-2" />
                Étudiants ({etudiants.length})
              </button>
              <button
                onClick={() => setActiveTab('notes')}
                className={`px-6 py-3 text-sm font-medium border-b-2 ${
                  activeTab === 'notes'
                    ? 'border-primary-500 text-primary-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
              >
                <FileText className="h-4 w-4 inline mr-2" />
                Notes ({notes.length})
              </button>
            </nav>
          </div>

          <div className="p-6">
            {/* Tab Étudiants */}
            {activeTab === 'etudiants' && (
              <div>
                {etudiants.length === 0 ? (
                  <p className="text-gray-500 text-center py-8">Aucun étudiant inscrit</p>
                ) : (
                  <div className="overflow-x-auto">
                    <table className="min-w-full divide-y divide-gray-200">
                      <thead className="bg-gray-50">
                        <tr>
                          <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Étudiant
                          </th>
                          <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Matricule
                          </th>
                          <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Email
                          </th>
                          <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Date d'inscription
                          </th>
                          <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Statut
                          </th>
                        </tr>
                      </thead>
                      <tbody className="bg-white divide-y divide-gray-200">
                        {etudiants.map((inscription) => (
                          <tr key={inscription.id} className="hover:bg-gray-50">
                            <td className="px-6 py-4 whitespace-nowrap">
                              {inscription.etudiant?.nom} {inscription.etudiant?.prenom}
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                              {inscription.etudiant?.matricule}
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                              {inscription.etudiant?.email}
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                              {new Date(inscription.dateInscription).toLocaleDateString('fr-FR')}
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                              <span
                                className={`px-2 py-1 text-xs font-semibold rounded ${
                                  inscription.status === 'ACTIVE'
                                    ? 'bg-green-100 text-green-800'
                                    : 'bg-red-100 text-red-800'
                                }`}
                              >
                                {inscription.status}
                              </span>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>
            )}

            {/* Tab Notes */}
            {activeTab === 'notes' && (
              <div>
                {notes.length === 0 ? (
                  <p className="text-gray-500 text-center py-8">Aucune note attribuée</p>
                ) : (
                  <div className="overflow-x-auto">
                    <table className="min-w-full divide-y divide-gray-200">
                      <thead className="bg-gray-50">
                        <tr>
                          <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Étudiant
                          </th>
                          <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Matricule
                          </th>
                          <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Note
                          </th>
                          <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Date
                          </th>
                        </tr>
                      </thead>
                      <tbody className="bg-white divide-y divide-gray-200">
                        {notes.map((note) => (
                          <tr key={note.id} className="hover:bg-gray-50">
                            <td className="px-6 py-4 whitespace-nowrap">
                              {note.etudiant?.nom} {note.etudiant?.prenom}
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                              {note.etudiant?.matricule}
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                              <span
                                className={`px-3 py-1 text-sm font-semibold rounded-full ${
                                  note.valeur >= 10
                                    ? 'bg-green-100 text-green-800'
                                    : 'bg-red-100 text-red-800'
                                }`}
                              >
                                {note.valeur}/20
                              </span>
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                              {new Date(note.dateSaisie).toLocaleDateString('fr-FR')}
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>
            )}
          </div>
        </div>
      </div>
    </Layout>
  );
};

export default FormateurCoursDetails;

