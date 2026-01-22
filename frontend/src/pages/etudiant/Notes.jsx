import { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext.jsx';
import { notesApi } from '../../utils/api.js';
import { parseJsonSafely } from '../../utils/jsonParser.js';
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
        console.log('🔍 [ETUDIANT NOTES] EtudiantId utilisé:', etudiantId);
        if (etudiantId) {
          const response = await notesApi.getByEtudiant(etudiantId);
          console.log('🔍 [ETUDIANT NOTES] Réponse brute:', response);
          
          // Parser la réponse - gestion robuste
          let data = null;
          
          // Si la réponse est déjà un tableau, l'utiliser directement
          if (Array.isArray(response.data)) {
            data = response.data;
            console.log('✅ [ETUDIANT NOTES] Réponse déjà un tableau, utilisation directe');
          } else if (typeof response.data === 'string') {
            // Si c'est une chaîne, essayer de parser avec parseJsonSafely
            data = parseJsonSafely(response.data);
            
            // Si parseJsonSafely a échoué ou n'a pas récupéré toutes les notes, essayer d'extraire le tableau complet
            if (!data || (Array.isArray(data) && data.length === 0)) {
              console.warn('⚠️ [ETUDIANT NOTES] parseJsonSafely a échoué ou retourné vide, tentative d\'extraction du tableau complet');
              try {
                // Trouver le début du tableau JSON
                const arrayStart = response.data.indexOf('[');
                if (arrayStart !== -1) {
                  // D'abord, trouver où commence l'objet d'erreur (s'il existe)
                  let errorStart = -1;
                  const errorPatterns = ['{"timestamp"', '{"error"', '{"status"', '"timestamp"', '"error"', '"status"'];
                  for (const pattern of errorPatterns) {
                    const pos = response.data.indexOf(pattern, arrayStart);
                    if (pos !== -1 && (errorStart === -1 || pos < errorStart)) {
                      errorStart = pos;
                    }
                  }
                  
                  // Extraire le tableau complet en comptant les crochets
                  let bracketCount = 0;
                  let inString = false;
                  let escapeNext = false;
                  let arrayEnd = -1;
                  const searchLimit = errorStart !== -1 ? errorStart : response.data.length;
                  
                  for (let i = arrayStart; i < searchLimit; i++) {
                    const char = response.data[i];
                    
                    if (escapeNext) {
                      escapeNext = false;
                      continue;
                    }
                    
                    if (char === '\\') {
                      escapeNext = true;
                      continue;
                    }
                    
                    if (char === '"' && !escapeNext) {
                      inString = !inString;
                      continue;
                    }
                    
                    if (!inString) {
                      if (char === '[') {
                        bracketCount++;
                      } else if (char === ']') {
                        bracketCount--;
                        if (bracketCount === 0) {
                          arrayEnd = i;
                          break;
                        }
                      }
                    }
                  }
                  
                  if (arrayEnd > arrayStart) {
                    const arrayString = response.data.substring(arrayStart, arrayEnd + 1);
                    console.log(`🔍 [ETUDIANT NOTES] Tableau extrait (${arrayString.length} caractères), tentative de parsing...`);
                    try {
                      const parsedArray = JSON.parse(arrayString);
                      if (Array.isArray(parsedArray) && parsedArray.length > 0) {
                        console.log(`✅ [ETUDIANT NOTES] ${parsedArray.length} notes extraites du tableau complet`);
                        data = parsedArray;
                      }
                    } catch (parseError) {
                      console.warn('⚠️ [ETUDIANT NOTES] Impossible de parser le tableau complet:', parseError.message);
                    }
                  }
                }
              } catch (e) {
                console.error('❌ [ETUDIANT NOTES] Erreur lors de l\'extraction du tableau complet:', e);
              }
            }
          } else {
            // Si c'est déjà un objet parsé
            data = response.data;
          }
          
          if (!data) {
            console.warn('⚠️ [ETUDIANT NOTES] Impossible de parser les notes');
            data = [];
          } else {
            console.log('✅ [ETUDIANT NOTES] Notes parsées:', data);
          }
          
          const notesArray = Array.isArray(data) ? data : [];
          console.log(`📊 [ETUDIANT NOTES] Nombre de notes reçues: ${notesArray.length}`);
          
          // Trier par date de saisie (plus récentes en premier)
          if (notesArray.length > 0) {
            notesArray.sort((a, b) => new Date(b.dateSaisie) - new Date(a.dateSaisie));
          }
          setNotes(notesArray);
        }
      } catch (error) {
        console.error('❌ [ETUDIANT NOTES] Erreur lors du chargement des notes:', error);
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

