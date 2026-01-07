import { createContext, useContext, useState, useEffect } from 'react';
import { authService } from '../services/authService.js';
import { getToken, setToken, removeToken, getUser, setUser, removeUser } from '../utils/auth.js';

const AuthContext = createContext(null);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUserState] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Vérifier si l'utilisateur est déjà connecté
    const token = getToken();
    const savedUser = getUser();
    
    if (token && savedUser) {
      setUserState(savedUser);
      // Vérifier si le token est toujours valide
      authService.getCurrentUser()
        .then((userData) => {
          setUserState(userData);
          setUser(userData);
        })
        .catch(() => {
          // Token invalide
          removeToken();
          removeUser();
          setUserState(null);
        })
        .finally(() => {
          setLoading(false);
        });
    } else {
      setLoading(false);
    }
  }, []);

  const login = async (login, password) => {
    console.log('🔐 [AUTH CONTEXT] Début de la connexion');
    try {
      console.log('🔄 [AUTH CONTEXT] Appel de authService.login...');
      const response = await authService.login(login, password);
      console.log('✅ [AUTH CONTEXT] Réponse reçue:', response);
      
      if (response.token) {
        console.log('💾 [AUTH CONTEXT] Sauvegarde du token et des données utilisateur');
        setToken(response.token);
        setUser(response);
        setUserState(response);
        console.log('✅ [AUTH CONTEXT] Connexion réussie et données sauvegardées');
      } else {
        console.error('❌ [AUTH CONTEXT] Pas de token dans la réponse!');
        throw new Error('Token manquant dans la réponse');
      }
      
      return response;
    } catch (error) {
      console.error('❌ [AUTH CONTEXT] Erreur lors de la connexion:');
      console.error('📦 Erreur complète:', error);
      throw error;
    }
  };

  const logout = () => {
    removeToken();
    removeUser();
    setUserState(null);
  };

  const value = {
    user,
    loading,
    login,
    logout,
    isAuthenticated: !!user,
    hasRole: (role) => user?.roles?.includes(role) || false,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

