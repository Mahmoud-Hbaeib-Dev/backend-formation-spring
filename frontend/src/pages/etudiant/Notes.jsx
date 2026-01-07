import { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext.jsx';
import { noteService } from '../../services/noteService.js';
import Layout from '../../components/Layout.jsx';
import { FileText, TrendingUp } from 'lucide-react';

const EtudiantNotes = () => {
  const { user } = useAuth();
  const [notes, setNotes] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadNotes = async () => {
      try {
        const etudiantId = user?.etudiantId || user?.userId || user?.id;
        if (etudiantId) {
          const data = await noteService.getByEtudiant(etudiantId);
          // Trier par date de saisie (plus récentes en premier)
          data.sort((a, b) => new Date(b.dateSaisie) - new Date(a.dateSaisie));
          setNotes(data);
        }
      } catch (error) {
        console.error('Erreur lors du chargement des notes:', error);
      } finally {
        setLoading(false);
      }
    };

    loadNotes();
  }, [user]);

  const moyenne =
    notes.length > 0
      ? notes.reduce((sum, n) => sum + n.valeur, 0) / notes.length
      : 0;

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
          <h1 className="text-3xl font-bold text-gray-900">Mes Notes</h1>
          <p className="mt-2 text-gray-600">Consultez toutes vos notes</p>
        </div>

        {/* Carte moyenne */}
        {notes.length > 0 && (
          <div className="bg-gradient-to-r from-primary-500 to-primary-600 rounded-lg shadow-lg p-6 text-white">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-primary-100 text-sm font-medium">Moyenne Générale</p>
                <p className="text-4xl font-bold mt-2">{moyenne.toFixed(2)}/20</p>
              </div>
              <TrendingUp className="h-12 w-12 text-primary-200" />
            </div>
          </div>
        )}

        {notes.length === 0 ? (
          <div className="bg-white rounded-lg shadow p-12 text-center">
            <FileText className="h-16 w-16 text-gray-400 mx-auto mb-4" />
            <p className="text-gray-600">Aucune note disponible</p>
          </div>
        ) : (
          <div className="bg-white rounded-lg shadow overflow-hidden">
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Cours
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Code
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
                        <div className="text-sm font-medium text-gray-900">
                          {note.cours?.titre}
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {note.cours?.code}
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
                        {new Date(note.dateSaisie).toLocaleDateString('fr-FR', {
                          year: 'numeric',
                          month: 'long',
                          day: 'numeric',
                        })}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </div>
    </Layout>
  );
};

export default EtudiantNotes;

