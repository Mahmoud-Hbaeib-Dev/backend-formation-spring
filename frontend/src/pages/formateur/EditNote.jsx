import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext.jsx';
import { notesApi } from '../../utils/api.js';
import { parseJsonSafely } from '../../utils/jsonParser.js';
import Layout from '../../components/Layout.jsx';
import { ArrowLeft, Save } from 'lucide-react';

const FormateurEditNote = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const { id } = useParams();
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [note, setNote] = useState(null);
  const [valeur, setValeur] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    const loadNote = async () => {
      try {
        const response = await notesApi.getById(id);
        let noteData = parseJsonSafely(response.data);
        if (noteData) {
          setNote(noteData);
          setValeur(noteData.valeur?.toString() || '');
        } else {
          setError('Note introuvable');
        }
      } catch (error) {
        console.error('Erreur lors du chargement de la note:', error);
        setError('Erreur lors du chargement de la note');
      } finally {
        setLoading(false);
      }
    };

    if (id) {
      loadNote();
    }
  }, [id]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    
    const valeurNum = parseFloat(valeur);
    if (isNaN(valeurNum) || valeurNum < 0 || valeurNum > 20) {
      setError('La note doit être un nombre entre 0 et 20');
      return;
    }

    setSaving(true);
    try {
      await notesApi.update(id, valeurNum);
      // Retourner à la page des notes avec le cours sélectionné
      if (note?.cours?.code) {
        navigate(`/formateur/notes?cours=${note.cours.code}`);
      } else {
        navigate('/formateur/notes');
      }
    } catch (err) {
      setError(
        err.response?.data?.message ||
        err.response?.data?.error ||
        'Erreur lors de la modification de la note'
      );
    } finally {
      setSaving(false);
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

  if (error && !note) {
    return (
      <Layout>
        <div className="max-w-2xl mx-auto">
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-md">
            {error}
          </div>
          <button
            onClick={() => navigate('/formateur/notes')}
            className="mt-4 px-4 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300 transition-colors"
          >
            Retour
          </button>
        </div>
      </Layout>
    );
  }

  return (
    <Layout>
      <div className="max-w-2xl mx-auto">
        <div className="mb-6">
          <button
            onClick={() => {
              if (note?.cours?.code) {
                navigate(`/formateur/notes?cours=${note.cours.code}`);
              } else {
                navigate('/formateur/notes');
              }
            }}
            className="flex items-center text-gray-600 hover:text-gray-900 mb-4"
          >
            <ArrowLeft className="h-4 w-4 mr-2" />
            Retour
          </button>
          <h1 className="text-3xl font-bold text-gray-900">Modifier la note</h1>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          {note && (
            <div className="mb-6 p-4 bg-gray-50 rounded-md">
              <p className="text-sm text-gray-600">
                <span className="font-medium">Étudiant:</span> {note.etudiant?.nom} {note.etudiant?.prenom} ({note.etudiant?.matricule})
              </p>
              <p className="text-sm text-gray-600 mt-1">
                <span className="font-medium">Cours:</span> {note.cours?.titre} ({note.cours?.code})
              </p>
              <p className="text-sm text-gray-600 mt-1">
                <span className="font-medium">Note actuelle:</span> {note.valeur}/20
              </p>
            </div>
          )}

          {error && (
            <div className="mb-4 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-md text-sm">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Nouvelle note (0-20)
              </label>
              <input
                type="number"
                min="0"
                max="20"
                step="0.5"
                value={valeur}
                onChange={(e) => setValeur(e.target.value)}
                required
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary-500 focus:border-primary-500"
                placeholder="Ex: 15.5"
              />
              <p className="mt-1 text-sm text-gray-500">
                La note doit être entre 0 et 20
              </p>
            </div>

            <div className="flex space-x-3">
              <button
                type="submit"
                disabled={saving}
                className="flex items-center space-x-2 flex-1 px-4 py-2 bg-primary-600 text-white rounded-md hover:bg-primary-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <Save className="h-4 w-4" />
                <span>{saving ? 'Enregistrement...' : 'Enregistrer la modification'}</span>
              </button>
              <button
                type="button"
                onClick={() => {
                  if (note?.cours?.code) {
                    navigate(`/formateur/notes?cours=${note.cours.code}`);
                  } else {
                    navigate('/formateur/notes');
                  }
                }}
                className="px-4 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300 transition-colors"
              >
                Annuler
              </button>
            </div>
          </form>
        </div>
      </div>
    </Layout>
  );
};

export default FormateurEditNote;

