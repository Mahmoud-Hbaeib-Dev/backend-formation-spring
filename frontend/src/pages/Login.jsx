import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';
import { authService } from '../services/authService.js';
import { GraduationCap } from 'lucide-react';

const Login = () => {
  const [login, setLogin] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login: authLogin } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    console.log('🚀 [LOGIN PAGE] Soumission du formulaire');
    console.log('📝 Données:', { login, password: password ? '***' : 'vide' });
    
    setError('');
    setLoading(true);

    try {
      console.log('🔄 [LOGIN PAGE] Appel de authLogin...');
      const response = await authLogin(login, password);
      console.log('✅ [LOGIN PAGE] Connexion réussie!');
      console.log('📦 Réponse complète:', response);
      
      const role = response.roles?.[0];
      console.log('👤 Rôle détecté:', role);

      // Rediriger selon le rôle
      if (role === 'ADMIN') {
        console.log('➡️ Redirection vers /admin/dashboard');
        navigate('/admin/dashboard');
      } else if (role === 'FORMATEUR') {
        console.log('➡️ Redirection vers /formateur/dashboard');
        navigate('/formateur/dashboard');
      } else if (role === 'ETUDIANT') {
        console.log('➡️ Redirection vers /etudiant/dashboard');
        navigate('/etudiant/dashboard');
      } else {
        console.log('⚠️ Rôle inconnu, redirection vers /dashboard');
        navigate('/dashboard');
      }
    } catch (err) {
      console.error('❌ [LOGIN PAGE] Erreur lors de la connexion:');
      console.error('📊 Status:', err.response?.status);
      console.error('📝 Status Text:', err.response?.statusText);
      console.error('💬 Message:', err.response?.data?.message);
      console.error('🔴 Error:', err.response?.data?.error);
      console.error('📦 Données complètes:', err.response?.data);
      console.error('🌐 URL:', err.config?.url);
      console.error('❌ Erreur complète:', err);
      
      const errorMessage = 
        err.response?.data?.message || 
        err.response?.data?.error || 
        err.message ||
        'Email/nom d\'utilisateur ou mot de passe incorrect';
      
      console.error('💬 Message d\'erreur affiché:', errorMessage);
      setError(errorMessage);
    } finally {
      setLoading(false);
      console.log('🏁 [LOGIN PAGE] Fin du processus de connexion');
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-primary-50 to-primary-100 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8 bg-white p-10 rounded-xl shadow-lg">
        <div className="text-center">
          <div className="flex justify-center">
            <div className="bg-primary-100 p-3 rounded-full">
              <GraduationCap className="h-12 w-12 text-primary-600" />
            </div>
          </div>
          <h2 className="mt-6 text-3xl font-extrabold text-gray-900">
            Centre de Formation
          </h2>
          <p className="mt-2 text-sm text-gray-600">
            Connectez-vous à votre compte
          </p>
        </div>
        <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-md text-sm">
              {error}
            </div>
          )}
          <div className="space-y-4">
            <div>
              <label htmlFor="login" className="block text-sm font-medium text-gray-700">
                Email ou Nom d'utilisateur
              </label>
              <input
                id="login"
                name="login"
                type="text"
                required
                value={login}
                onChange={(e) => setLogin(e.target.value)}
                className="mt-1 appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-md focus:outline-none focus:ring-primary-500 focus:border-primary-500 focus:z-10 sm:text-sm"
                placeholder="Email ou nom d'utilisateur"
              />
            </div>
            <div>
              <label htmlFor="password" className="block text-sm font-medium text-gray-700">
                Mot de passe
              </label>
              <input
                id="password"
                name="password"
                type="password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="mt-1 appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-md focus:outline-none focus:ring-primary-500 focus:border-primary-500 focus:z-10 sm:text-sm"
                placeholder="Entrez votre mot de passe"
              />
            </div>
          </div>

          <div>
            <button
              type="submit"
              disabled={loading}
              className="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-primary-600 hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? 'Connexion...' : 'Se connecter'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Login;

