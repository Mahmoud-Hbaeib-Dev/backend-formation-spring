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
        console.log('🔍 [INSCRIPTION] Chargement des cours et inscriptions...');
        const [allCoursResponse, inscriptionsResponse] = await Promise.all([
          coursApi.list(),
          inscriptionsApi.getByEtudiant(etudiantId),
        ]);

        console.log('📦 [INSCRIPTION] Réponse cours brute:', allCoursResponse);
        console.log('📦 [INSCRIPTION] Réponse inscriptions brute:', inscriptionsResponse);

        // Parser les réponses - gestion robuste pour récupérer TOUS les cours
        let allCours = null;
        
        // Si la réponse est déjà un tableau, l'utiliser directement
        if (Array.isArray(allCoursResponse.data)) {
          allCours = allCoursResponse.data;
          console.log('✅ [INSCRIPTION] Réponse déjà un tableau, utilisation directe');
        } else if (typeof allCoursResponse.data === 'string') {
          // Si c'est une chaîne, essayer de parser avec parseJsonSafely
          allCours = parseJsonSafely(allCoursResponse.data);
          
          // Toujours essayer d'extraire tous les cours manuellement pour être sûr
          // Compter le nombre d'occurrences de "code" dans la chaîne pour estimer le nombre de cours
          const codeMatches = allCoursResponse.data.match(/"code"\s*:\s*"[^"]+"/g);
          const estimatedCount = codeMatches ? codeMatches.length : (allCours && Array.isArray(allCours) ? allCours.length : 0);
          console.log(`🔍 [INSCRIPTION] Estimation: ${estimatedCount} cours dans la réponse`);
          
          // Si on estime qu'il y a plus de cours que ce qui a été parsé, essayer d'extraire manuellement
          if (estimatedCount > (allCours && Array.isArray(allCours) ? allCours.length : 0)) {
            console.warn(`⚠️ [INSCRIPTION] ${estimatedCount} cours estimés mais seulement ${allCours && Array.isArray(allCours) ? allCours.length : 0} parsés, tentative d'extraction manuelle`);
            try {
              // Chercher le début du tableau JSON
              const arrayStart = allCoursResponse.data.indexOf('[');
              if (arrayStart !== -1) {
                // Extraire tous les objets JSON complets du tableau
                const jsonString = allCoursResponse.data.substring(arrayStart);
                const coursMatches = [];
                let depth = 0;
                let bracketDepth = 0;
                let start = -1;
                let inString = false;
                let escapeNext = false;
                
                for (let i = 0; i < jsonString.length; i++) {
                  const char = jsonString[i];
                  
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
                    if (char === '{') {
                      if (bracketDepth === 0) start = i;
                      bracketDepth++;
                    } else if (char === '}') {
                      bracketDepth--;
                      if (bracketDepth === 0 && start !== -1) {
                        const objStr = jsonString.substring(start, i + 1);
                        if (objStr.includes('"code"')) {
                          try {
                            const parsed = JSON.parse(objStr);
                            if (parsed.code && !coursMatches.find(c => c.code === parsed.code)) {
                              coursMatches.push(parsed);
                            }
                          } catch (e) {
                            // Ignorer les objets invalides
                          }
                        }
                        start = -1;
                      }
                    } else if (char === '[') {
                      depth++;
                    } else if (char === ']') {
                      depth--;
                      if (depth < 0) break; // Fin du tableau
                    }
                  }
                }
                
                if (coursMatches.length > 0) {
                  console.log(`✅ [INSCRIPTION] ${coursMatches.length} cours extraits manuellement`);
                  // Fusionner avec les cours déjà parsés (éviter les doublons)
                  if (allCours && Array.isArray(allCours)) {
                    const existingCodes = new Set(allCours.map(c => c.code));
                    const newCours = coursMatches.filter(c => !existingCodes.has(c.code));
                    allCours = [...allCours, ...newCours];
                    console.log(`✅ [INSCRIPTION] Total après fusion: ${allCours.length} cours`);
                  } else {
                    allCours = coursMatches;
                  }
                }
              }
            } catch (e) {
              console.error('❌ [INSCRIPTION] Erreur lors de l\'extraction manuelle:', e);
            }
          }
          
          // Si parseJsonSafely a échoué ou n'a pas récupéré tous les cours, essayer d'extraire le tableau complet
          if (!allCours || (Array.isArray(allCours) && allCours.length < estimatedCount)) {
            console.warn(`⚠️ [INSCRIPTION] parseJsonSafely a échoué ou retourné ${allCours && Array.isArray(allCours) ? allCours.length : 0} cours, tentative d'extraction du tableau complet`);
            try {
              // Trouver le début du tableau JSON
              const arrayStart = allCoursResponse.data.indexOf('[');
              if (arrayStart !== -1) {
                // D'abord, trouver où commence l'objet d'erreur (s'il existe)
                let errorStart = -1;
                const errorPatterns = ['{"timestamp"', '{"error"', '{"status"', '"timestamp"', '"error"', '"status"'];
                for (const pattern of errorPatterns) {
                  const pos = allCoursResponse.data.indexOf(pattern, arrayStart);
                  if (pos !== -1 && (errorStart === -1 || pos < errorStart)) {
                    errorStart = pos;
                  }
                }
                
                // Extraire le tableau complet en comptant les crochets
                let bracketCount = 0;
                let inString = false;
                let escapeNext = false;
                let arrayEnd = -1;
                const searchLimit = errorStart !== -1 ? errorStart : allCoursResponse.data.length;
                
                for (let i = arrayStart; i < searchLimit; i++) {
                  const char = allCoursResponse.data[i];
                  
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
                  const arrayString = allCoursResponse.data.substring(arrayStart, arrayEnd + 1);
                  console.log(`🔍 [INSCRIPTION] Tableau extrait (${arrayString.length} caractères), tentative de parsing...`);
                  try {
                    const parsedArray = JSON.parse(arrayString);
                    if (Array.isArray(parsedArray) && parsedArray.length > 0) {
                      console.log(`✅ [INSCRIPTION] ${parsedArray.length} cours extraits du tableau complet:`, parsedArray.map(c => c.code));
                      // Fusionner avec les cours déjà parsés (éviter les doublons)
                      if (allCours && Array.isArray(allCours)) {
                        const existingCodes = new Set(allCours.map(c => c.code));
                        const newCours = parsedArray.filter(c => c && c.code && !existingCodes.has(c.code));
                        allCours = [...allCours, ...newCours];
                        console.log(`✅ [INSCRIPTION] Total après fusion: ${allCours.length} cours`);
                      } else {
                        allCours = parsedArray;
                      }
                    } else {
                      console.warn(`⚠️ [INSCRIPTION] Tableau parsé mais vide ou invalide:`, parsedArray);
                    }
                  } catch (parseError) {
                    console.warn('⚠️ [INSCRIPTION] Impossible de parser le tableau complet, tentative objet par objet. Erreur:', parseError.message);
                    // Si le parsing du tableau complet échoue, essayer d'extraire chaque objet individuellement
                    const coursMatches = [];
                    let objDepth = 0;
                    let objStart = -1;
                    inString = false;
                    escapeNext = false;
                    
                    // Parcourir arrayString directement (qui commence à l'index 0)
                    for (let i = 1; i < arrayString.length - 1; i++) {
                      const char = arrayString[i];
                      
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
                        if (char === '{') {
                          if (objDepth === 0) objStart = i;
                          objDepth++;
                        } else if (char === '}') {
                          objDepth--;
                          if (objDepth === 0 && objStart !== -1) {
                            const objStr = arrayString.substring(objStart, i + 1);
                            if (objStr.includes('"code"')) {
                              try {
                                const parsed = JSON.parse(objStr);
                                if (parsed.code && !coursMatches.find(c => c.code === parsed.code)) {
                                  coursMatches.push(parsed);
                                }
                              } catch (e) {
                                // Ignorer les objets invalides
                              }
                            }
                            objStart = -1;
                          }
                        }
                      }
                    }
                    
                    if (coursMatches.length > 0) {
                      console.log(`✅ [INSCRIPTION] ${coursMatches.length} cours extraits objet par objet`);
                      if (allCours && Array.isArray(allCours)) {
                        const existingCodes = new Set(allCours.map(c => c.code));
                        const newCours = coursMatches.filter(c => !existingCodes.has(c.code));
                        allCours = [...allCours, ...newCours];
                        console.log(`✅ [INSCRIPTION] Total après fusion: ${allCours.length} cours`);
                      } else {
                        allCours = coursMatches;
                      }
                    }
                  }
                }
              }
            } catch (e) {
              console.error('❌ [INSCRIPTION] Erreur lors de l\'extraction du tableau complet:', e);
            }
          }
        } else {
          // Si c'est déjà un objet parsé
          allCours = allCoursResponse.data;
        }
        
        if (!allCours) {
          allCours = [];
        }
        const allCoursArray = Array.isArray(allCours) ? allCours : [];
        console.log(`✅ [INSCRIPTION] ${allCoursArray.length} cours récupérés au total:`, allCoursArray.map(c => c.code || c));

        let inscriptions = parseJsonSafely(inscriptionsResponse.data);
        if (!inscriptions) {
          console.warn('⚠️ [INSCRIPTION] Aucune inscription parsée, tentative avec la réponse brute');
          if (Array.isArray(inscriptionsResponse.data)) {
            inscriptions = inscriptionsResponse.data;
          } else {
            inscriptions = [];
          }
        }
        const inscriptionsArray = Array.isArray(inscriptions) ? inscriptions : [];
        console.log(`✅ [INSCRIPTION] ${inscriptionsArray.length} inscriptions parsées:`, inscriptionsArray);
        
        // Log détaillé de la structure de la première inscription pour déboguer
        if (inscriptionsArray.length > 0) {
          console.log('🔍 [INSCRIPTION] Structure de la première inscription:', JSON.stringify(inscriptionsArray[0], null, 2));
          console.log('🔍 [INSCRIPTION] inscription.cours:', inscriptionsArray[0].cours);
          console.log('🔍 [INSCRIPTION] inscription.cours?.code:', inscriptionsArray[0].cours?.code);
          console.log('🔍 [INSCRIPTION] inscription.coursCode:', inscriptionsArray[0].coursCode);
        }

        setMesInscriptions(inscriptionsArray);
        
        // Créer un map des inscriptions actives pour faciliter la recherche
        // Gérer différents formats possibles de l'objet inscription
        const inscriptionsActivesMap = new Map();
        Array.isArray(inscriptionsArray)
          ? inscriptionsArray
              .filter((i) => i.status === 'ACTIVE')
              .forEach((i) => {
                // Essayer différentes façons d'accéder au code du cours
                // Le backend peut retourner cours comme objet, comme string, ou comme null (lazy loading)
                let coursCode = null;
                
                // Vérifier si cours existe et n'est pas null
                if (i.cours) {
                  if (typeof i.cours === 'string') {
                    coursCode = i.cours;
                  } else if (typeof i.cours === 'object') {
                    coursCode = i.cours.code || i.cours.coursCode || i.cours.cours_code;
                  }
                }
                
                // Autres alternatives (champs directs)
                if (!coursCode) {
                  coursCode = i.coursCode || i.cours_code || i.coursId;
                }
                
                // Si on a toujours pas le code, essayer de le trouver dans tous les champs
                if (!coursCode && i.cours && typeof i.cours === 'object') {
                  // Parcourir tous les champs de l'objet cours
                  for (const key in i.cours) {
                    if (key.toLowerCase().includes('code') && i.cours[key]) {
                      coursCode = i.cours[key];
                      break;
                    }
                  }
                }
                
                if (coursCode) {
                  inscriptionsActivesMap.set(coursCode, i);
                  console.log(`✅ [INSCRIPTION] Inscription trouvée pour le cours: ${coursCode}`);
                } else {
                  console.warn('⚠️ [INSCRIPTION] Impossible de trouver le code du cours dans l\'inscription. Structure complète:', JSON.stringify(i, null, 2));
                }
              })
          : [];
        
        console.log('📋 [INSCRIPTION] Codes de cours déjà inscrits:', Array.from(inscriptionsActivesMap.keys()));
        
        // Afficher TOUS les cours (inscrits et non inscrits)
        // Enrichir chaque cours avec l'information d'inscription
        const tousLesCours = Array.isArray(allCoursArray)
          ? allCoursArray.map((c) => {
              const inscription = inscriptionsActivesMap.get(c.code);
              return {
                ...c,
                estInscrit: !!inscription,
                inscriptionId: inscription?.id,
              };
            })
          : [];
        
        console.log(`📚 [INSCRIPTION] ${tousLesCours.length} cours au total (${tousLesCours.filter(c => c.estInscrit).length} inscrits, ${tousLesCours.filter(c => !c.estInscrit).length} disponibles)`);
        
        setCoursDisponibles(tousLesCours);
        setAllCoursDisponibles(tousLesCours);
      } catch (error) {
        console.error('❌ [INSCRIPTION] Erreur lors du chargement:', error);
        console.error('❌ [INSCRIPTION] Détails de l\'erreur:', error.response?.data || error.message);
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
        
        // Créer un map des inscriptions actives
        const inscriptionsActivesMap = new Map();
        Array.isArray(mesInscriptions)
          ? mesInscriptions
              .filter((i) => i.status === 'ACTIVE')
              .forEach((i) => {
                if (i.cours?.code) {
                  inscriptionsActivesMap.set(i.cours.code, i);
                }
              })
          : [];
        
        // Enrichir les résultats de recherche avec l'information d'inscription
        const filteredResults = searchResults.map((c) => {
          const inscription = inscriptionsActivesMap.get(c.code);
          return {
            ...c,
            estInscrit: !!inscription,
            inscriptionId: inscription?.id,
          };
        });
        
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

    try {
      const [allCoursResponse, inscriptionsResponse] = await Promise.all([
        coursApi.list(),
        inscriptionsApi.getByEtudiant(etudiantId),
      ]);

      // Parser les réponses
      let allCours = parseJsonSafely(allCoursResponse.data);
      if (!allCours) {
        if (Array.isArray(allCoursResponse.data)) {
          allCours = allCoursResponse.data;
        } else {
          allCours = [];
        }
      }
      const allCoursArray = Array.isArray(allCours) ? allCours : [];

      let inscriptions = parseJsonSafely(inscriptionsResponse.data);
      if (!inscriptions) {
        if (Array.isArray(inscriptionsResponse.data)) {
          inscriptions = inscriptionsResponse.data;
        } else {
          inscriptions = [];
        }
      }
      const inscriptionsArray = Array.isArray(inscriptions) ? inscriptions : [];

      setMesInscriptions(inscriptionsArray);
      
      // Créer un map des inscriptions actives
      const inscriptionsActivesMap = new Map();
      Array.isArray(inscriptionsArray)
        ? inscriptionsArray
            .filter((i) => i.status === 'ACTIVE')
            .forEach((i) => {
              if (i.cours?.code) {
                inscriptionsActivesMap.set(i.cours.code, i);
              }
            })
        : [];
      
      // Afficher TOUS les cours avec information d'inscription
      const tousLesCours = Array.isArray(allCoursArray)
        ? allCoursArray.map((c) => {
            const inscription = inscriptionsActivesMap.get(c.code);
            return {
              ...c,
              estInscrit: !!inscription,
              inscriptionId: inscription?.id,
            };
          })
        : [];
      
      setCoursDisponibles(tousLesCours);
      setAllCoursDisponibles(tousLesCours);
    } catch (error) {
      console.error('❌ [INSCRIPTION] Erreur lors du rechargement:', error);
    }
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

  // Séparer les cours inscrits et non inscrits pour l'affichage
  const coursInscrits = coursDisponibles.filter(c => c.estInscrit);
  const coursNonInscrits = coursDisponibles.filter(c => !c.estInscrit);

  return (
    <Layout>
      <div className="space-y-6">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Inscription aux Cours</h1>
          <p className="mt-2 text-gray-600">Consultez tous les cours disponibles et gérez vos inscriptions</p>
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

        {/* Tous les cours - avec indication d'inscription */}
        <div>
          <h2 className="text-xl font-semibold text-gray-900 mb-4">
            Tous les cours {coursDisponibles.length > 0 && `(${coursDisponibles.length})`}
          </h2>
          {coursDisponibles.length === 0 ? (
            <div className="bg-white rounded-lg shadow p-12 text-center">
              <BookOpen className="h-16 w-16 text-gray-400 mx-auto mb-4" />
              <p className="text-gray-600">
                Aucun cours disponible pour le moment
              </p>
              {allCoursDisponibles.length === 0 && (
                <p className="text-sm text-gray-500 mt-2">
                  Vérifiez la console pour plus de détails sur les erreurs de chargement
                </p>
              )}
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {coursDisponibles.map((cours) => {
                const estInscrit = cours.estInscrit;
                return (
                  <div 
                    key={cours.code} 
                    className={`bg-white rounded-lg shadow hover:shadow-lg transition-shadow p-6 ${
                      estInscrit ? 'ring-2 ring-green-200 bg-green-50/30' : ''
                    }`}
                  >
                    <div className="flex items-start justify-between mb-4">
                      <div className="flex-1">
                        <div className="flex items-center gap-2">
                          <h3 className="text-lg font-semibold text-gray-900">{cours.titre}</h3>
                          {estInscrit && (
                            <CheckCircle className="h-5 w-5 text-green-500 flex-shrink-0" />
                          )}
                        </div>
                        <p className="text-sm text-gray-500 mt-1">Code: {cours.code}</p>
                        {estInscrit && (
                          <span className="inline-block mt-2 px-2 py-1 text-xs font-semibold bg-green-100 text-green-800 rounded-full">
                            Déjà inscrit
                          </span>
                        )}
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
                    {estInscrit ? (
                      <button
                        onClick={() => handleDesinscription(cours.inscriptionId)}
                        disabled={desinscribing === cours.inscriptionId}
                        className="w-full flex items-center justify-center space-x-2 px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                      >
                        {desinscribing === cours.inscriptionId ? (
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
                    ) : (
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
                    )}
                  </div>
                );
              })}
            </div>
          )}
        </div>
      </div>
    </Layout>
  );
};

export default EtudiantInscriptionCours;

