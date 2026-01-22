import { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext.jsx';
import { seancesApi } from '../../utils/api.js';
import { parseJsonSafely } from '../../utils/jsonParser.js';
import Layout from '../../components/Layout.jsx';
import { Calendar, Clock, MapPin } from 'lucide-react';

const EtudiantPlanning = () => {
  const { user } = useAuth();
  const [seances, setSeances] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedDate, setSelectedDate] = useState(new Date().toISOString().split('T')[0]);

  useEffect(() => {
    const loadSeances = async () => {
      try {
        const etudiantId = user?.etudiantId || user?.userId || user?.id;
        console.log('🔍 [ETUDIANT PLANNING] EtudiantId utilisé:', etudiantId);
        if (etudiantId) {
          const response = await seancesApi.getEmploiDuTempsEtudiant(etudiantId);
          console.log('🔍 [ETUDIANT PLANNING] Réponse brute:', response);
          
          // Parser la réponse - gestion robuste
          let data = null;
          
          // Si la réponse est déjà un tableau, l'utiliser directement
          if (Array.isArray(response.data)) {
            data = response.data;
            console.log('✅ [ETUDIANT PLANNING] Réponse déjà un tableau, utilisation directe');
          } else if (typeof response.data === 'string') {
            // Si c'est une chaîne, essayer de parser avec parseJsonSafely
            data = parseJsonSafely(response.data);
            
            // Si parseJsonSafely a échoué ou n'a pas récupéré toutes les séances, essayer d'extraire le tableau complet
            if (!data || (Array.isArray(data) && data.length === 0)) {
              console.warn('⚠️ [ETUDIANT PLANNING] parseJsonSafely a échoué ou retourné vide, tentative d\'extraction du tableau complet');
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
                    console.log(`🔍 [ETUDIANT PLANNING] Tableau extrait (${arrayString.length} caractères), tentative de parsing...`);
                    try {
                      const parsedArray = JSON.parse(arrayString);
                      if (Array.isArray(parsedArray) && parsedArray.length > 0) {
                        console.log(`✅ [ETUDIANT PLANNING] ${parsedArray.length} séances extraites du tableau complet`);
                        data = parsedArray;
                      }
                    } catch (parseError) {
                      console.warn('⚠️ [ETUDIANT PLANNING] Impossible de parser le tableau complet:', parseError.message);
                    }
                  }
                }
              } catch (e) {
                console.error('❌ [ETUDIANT PLANNING] Erreur lors de l\'extraction du tableau complet:', e);
              }
            }
          } else {
            // Si c'est déjà un objet parsé
            data = response.data;
          }
          
          if (!data) {
            console.warn('⚠️ [ETUDIANT PLANNING] Impossible de parser les séances');
            data = [];
          } else {
            console.log('✅ [ETUDIANT PLANNING] Séances parsées:', data);
          }
          
          const seancesArray = Array.isArray(data) ? data : [];
          console.log(`📊 [ETUDIANT PLANNING] Nombre de séances reçues: ${seancesArray.length}`);
          
          // Trier par date et heure
          if (seancesArray.length > 0) {
            seancesArray.sort((a, b) => {
              const dateA = new Date(`${a.date}T${a.heure}`);
              const dateB = new Date(`${b.date}T${b.heure}`);
              return dateA - dateB;
            });
          }
          setSeances(seancesArray);
        }
      } catch (error) {
        console.error('❌ [ETUDIANT PLANNING] Erreur lors du chargement du planning:', error);
      } finally {
        setLoading(false);
      }
    };

    loadSeances();
  }, [user]);

  const formatDate = (dateStr) => {
    const date = new Date(dateStr);
    return date.toLocaleDateString('fr-FR', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  // Grouper les séances par date
  const seancesParDate = seances.reduce((acc, seance) => {
    const date = seance.date;
    if (!acc[date]) {
      acc[date] = [];
    }
    acc[date].push(seance);
    return acc;
  }, {});

  // Trier les dates
  const datesTriees = Object.keys(seancesParDate).sort((a, b) => new Date(a) - new Date(b));

  // Filtrer par date sélectionnée si une date est choisie
  const datesAffichees = selectedDate
    ? datesTriees.filter((date) => date === selectedDate)
    : datesTriees;

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
            <h1 className="text-3xl font-bold text-gray-900">Mon Planning</h1>
            <p className="mt-2 text-gray-600">Emploi du temps de toutes vos séances</p>
          </div>
          <div className="flex items-center gap-3">
            <input
              type="date"
              value={selectedDate}
              onChange={(e) => setSelectedDate(e.target.value)}
              className="px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary-500 focus:border-primary-500"
            />
            {selectedDate && (
              <button
                onClick={() => setSelectedDate('')}
                className="px-4 py-2 text-sm text-gray-600 hover:text-gray-900 border border-gray-300 rounded-md hover:bg-gray-50 transition-colors"
              >
                Tout afficher
              </button>
            )}
          </div>
        </div>

        {datesAffichees.length === 0 ? (
          <div className="bg-white rounded-lg shadow p-12 text-center">
            <Calendar className="h-16 w-16 text-gray-400 mx-auto mb-4" />
            <p className="text-gray-600">
              {selectedDate ? 'Aucune séance pour cette date' : 'Aucune séance planifiée'}
            </p>
          </div>
        ) : (
          <div className="space-y-6">
            {datesAffichees.map((date) => (
              <div key={date} className="bg-white rounded-lg shadow overflow-hidden">
                <div className="bg-gradient-to-r from-primary-500 to-primary-600 px-6 py-4">
                  <h2 className="text-xl font-bold text-white">
                    {formatDate(date)}
                  </h2>
                  <p className="text-primary-100 text-sm mt-1">
                    {seancesParDate[date].length} séance{seancesParDate[date].length > 1 ? 's' : ''}
                  </p>
                </div>
                <div className="divide-y divide-gray-200">
                  {seancesParDate[date].map((seance) => (
                    <div key={seance.id} className="p-6 hover:bg-gray-50 transition-colors">
                      <div className="flex items-start justify-between">
                        <div className="flex-1">
                          <div className="flex items-start gap-4">
                            <div className="flex-shrink-0 w-16 h-16 bg-primary-100 rounded-lg flex items-center justify-center">
                              <Clock className="h-8 w-8 text-primary-600" />
                            </div>
                            <div className="flex-1">
                              <h3 className="text-lg font-semibold text-gray-900">
                                {seance.cours?.titre || 'Cours'}
                              </h3>
                              <p className="text-sm text-gray-500 mt-1">
                                Code: {seance.cours?.code || 'N/A'}
                              </p>
                              <div className="mt-4 flex flex-wrap gap-4 text-sm text-gray-600">
                                <div className="flex items-center">
                                  <Clock className="h-4 w-4 mr-2 text-primary-600" />
                                  <span className="font-medium">{seance.heure}</span>
                                </div>
                                {seance.salle && (
                                  <div className="flex items-center">
                                    <MapPin className="h-4 w-4 mr-2 text-primary-600" />
                                    <span>Salle: <span className="font-medium">{seance.salle}</span></span>
                                  </div>
                                )}
                                {seance.formateur && (
                                  <div className="flex items-center">
                                    <span className="text-gray-500">
                                      Formateur: <span className="font-medium text-gray-700">{seance.formateur.nom}</span>
                                    </span>
                                  </div>
                                )}
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </Layout>
  );
};

export default EtudiantPlanning;

