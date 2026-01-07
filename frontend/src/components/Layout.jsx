import { Outlet, Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';
import { LogOut, Menu, X } from 'lucide-react';
import { useState } from 'react';

const Layout = ({ children }) => {
  const { user, logout, hasRole } = useAuth();
  const navigate = useNavigate();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const getNavItems = () => {
    if (hasRole('ADMIN')) {
      return [
        { path: '/admin/dashboard', label: 'Dashboard', icon: '📊' },
        { path: '/admin/etudiants', label: 'Étudiants', icon: '👥' },
        { path: '/admin/formateurs', label: 'Formateurs', icon: '👨‍🏫' },
        { path: '/admin/cours', label: 'Cours', icon: '📚' },
      ];
    } else if (hasRole('FORMATEUR')) {
      return [
        { path: '/formateur/dashboard', label: 'Dashboard', icon: '📊' },
        { path: '/formateur/cours', label: 'Mes Cours', icon: '📚' },
        { path: '/formateur/seances', label: 'Mes Séances', icon: '📅' },
        { path: '/formateur/notes', label: 'Gestion Notes', icon: '📝' },
        { path: '/formateur/statistiques', label: 'Statistiques', icon: '📈' },
      ];
    } else if (hasRole('ETUDIANT')) {
      return [
        { path: '/etudiant/dashboard', label: 'Dashboard', icon: '📊' },
        { path: '/etudiant/cours', label: 'Mes Cours', icon: '📚' },
        { path: '/etudiant/inscription', label: 'S\'inscrire', icon: '➕' },
        { path: '/etudiant/notes', label: 'Mes Notes', icon: '📝' },
        { path: '/etudiant/planning', label: 'Planning', icon: '📅' },
      ];
    }
    return [];
  };

  const navItems = getNavItems();
  const basePath = hasRole('ADMIN') ? '/admin' : hasRole('FORMATEUR') ? '/formateur' : '/etudiant';

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Navbar */}
      <nav className="bg-white shadow-sm border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center">
              <button
                onClick={() => setSidebarOpen(!sidebarOpen)}
                className="lg:hidden p-2 rounded-md text-gray-600 hover:text-gray-900 hover:bg-gray-100"
              >
                {sidebarOpen ? <X size={24} /> : <Menu size={24} />}
              </button>
              <Link to={basePath + '/dashboard'} className="flex items-center ml-4 lg:ml-0">
                <span className="text-xl font-bold text-primary-600">Centre de Formation</span>
              </Link>
            </div>
            <div className="flex items-center space-x-4">
              <span className="text-sm text-gray-700">
                {user?.username} ({user?.roles?.[0]})
              </span>
              <button
                onClick={handleLogout}
                className="flex items-center space-x-2 px-4 py-2 text-sm text-gray-700 hover:text-gray-900 hover:bg-gray-100 rounded-md"
              >
                <LogOut size={18} />
                <span>Déconnexion</span>
              </button>
            </div>
          </div>
        </div>
      </nav>

      <div className="flex">
        {/* Sidebar */}
        <aside
          className={`${
            sidebarOpen ? 'translate-x-0' : '-translate-x-full'
          } lg:translate-x-0 fixed lg:static inset-y-0 left-0 z-50 w-64 bg-white shadow-lg transform transition-transform duration-300 ease-in-out lg:transition-none`}
        >
          <div className="h-full pt-16 lg:pt-0 pb-4 overflow-y-auto">
            <nav className="mt-8 px-4 space-y-2">
              {navItems.map((item) => (
                <Link
                  key={item.path}
                  to={item.path}
                  onClick={() => setSidebarOpen(false)}
                  className="flex items-center space-x-3 px-4 py-3 text-gray-700 rounded-lg hover:bg-primary-50 hover:text-primary-700 transition-colors"
                >
                  <span className="text-xl">{item.icon}</span>
                  <span className="font-medium">{item.label}</span>
                </Link>
              ))}
            </nav>
          </div>
        </aside>

        {/* Overlay pour mobile */}
        {sidebarOpen && (
          <div
            className="fixed inset-0 bg-black bg-opacity-50 z-40 lg:hidden"
            onClick={() => setSidebarOpen(false)}
          />
        )}

        {/* Main Content */}
        <main className="flex-1 lg:ml-0">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            {children || <Outlet />}
          </div>
        </main>
      </div>
    </div>
  );
};

export default Layout;

