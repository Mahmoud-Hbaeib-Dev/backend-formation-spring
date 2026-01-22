import { Outlet, Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';
import { LogOut, Menu, X, LayoutDashboard, BookOpen, Calendar, FileText, BarChart3, User, GraduationCap, Plus, ClipboardList } from 'lucide-react';
import { useState } from 'react';

const Layout = ({ children }) => {
  const { user, logout, hasRole } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const getNavItems = () => {
    if (hasRole('ADMIN')) {
      return [
        { path: '/admin/dashboard', label: 'Dashboard', icon: LayoutDashboard },
        { path: '/admin/etudiants', label: 'Étudiants', icon: GraduationCap },
        { path: '/admin/formateurs', label: 'Formateurs', icon: User },
        { path: '/admin/cours', label: 'Cours', icon: BookOpen },
      ];
    } else if (hasRole('FORMATEUR')) {
      return [
        { path: '/formateur/dashboard', label: 'Dashboard', icon: LayoutDashboard },
        { path: '/formateur/cours', label: 'Mes Cours', icon: BookOpen },
        { path: '/formateur/seances', label: 'Mes Séances', icon: Calendar },
        { path: '/formateur/notes', label: 'Gestion Notes', icon: FileText },
        { path: '/formateur/statistiques', label: 'Statistiques', icon: BarChart3 },
        { path: '/formateur/profile', label: 'Profil', icon: User },
      ];
    } else if (hasRole('ETUDIANT')) {
      return [
        { path: '/etudiant/dashboard', label: 'Dashboard', icon: LayoutDashboard },
        { path: '/etudiant/cours', label: 'Mes Cours', icon: BookOpen },
        { path: '/etudiant/inscription', label: 'S\'inscrire', icon: Plus },
        { path: '/etudiant/notes', label: 'Mes Notes', icon: FileText },
        { path: '/etudiant/planning', label: 'Planning', icon: ClipboardList },
        { path: '/etudiant/profile', label: 'Profil', icon: User },
      ];
    }
    return [];
  };

  const navItems = getNavItems();
  const basePath = hasRole('ADMIN') ? '/admin' : hasRole('FORMATEUR') ? '/formateur' : '/etudiant';
  const isActive = (path) => location.pathname === path || location.pathname.startsWith(path + '/');

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 via-white to-gray-50">
      {/* Navbar */}
      <nav className="bg-white/80 backdrop-blur-lg shadow-sm border-b border-gray-200/50 sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center">
              <button
                onClick={() => setSidebarOpen(!sidebarOpen)}
                className="lg:hidden p-2 rounded-lg text-gray-600 hover:text-gray-900 hover:bg-gray-100 transition-colors"
              >
                {sidebarOpen ? <X size={24} /> : <Menu size={24} />}
              </button>
              <Link to={basePath + '/dashboard'} className="flex items-center ml-4 lg:ml-0 space-x-2 group">
                <div className="bg-gradient-to-br from-primary-600 to-primary-700 p-2 rounded-lg group-hover:scale-110 transition-transform">
                  <GraduationCap className="h-6 w-6 text-white" />
                </div>
                <span className="text-xl font-bold bg-gradient-to-r from-primary-600 to-primary-700 bg-clip-text text-transparent">
                  Centre de Formation
                </span>
              </Link>
            </div>
            <div className="flex items-center space-x-4">
              <div className="hidden sm:flex items-center space-x-3 px-4 py-2 bg-gray-50 rounded-lg">
                <div className="w-8 h-8 bg-gradient-to-br from-primary-500 to-primary-600 rounded-full flex items-center justify-center text-white text-sm font-semibold">
                  {user?.username?.charAt(0).toUpperCase()}
                </div>
                <div className="flex flex-col">
                  <span className="text-sm font-medium text-gray-900">{user?.username}</span>
                  <span className="text-xs text-gray-500">{user?.roles?.[0]}</span>
                </div>
              </div>
              <button
                onClick={handleLogout}
                className="flex items-center space-x-2 px-4 py-2 text-sm font-medium text-gray-700 hover:text-gray-900 hover:bg-gray-100 rounded-lg transition-all duration-200"
              >
                <LogOut size={18} />
                <span className="hidden sm:inline">Déconnexion</span>
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
          } lg:translate-x-0 fixed lg:static inset-y-0 left-0 z-40 w-64 bg-white/80 backdrop-blur-lg shadow-lg border-r border-gray-200/50 transform transition-transform duration-300 ease-in-out lg:transition-none h-[calc(100vh-4rem)] lg:h-auto mt-16 lg:mt-0`}
        >
          <div className="h-full pt-8 pb-4 overflow-y-auto">
            <nav className="px-4 space-y-2">
              {navItems.map((item) => {
                const Icon = item.icon;
                const active = isActive(item.path);
                return (
                  <Link
                    key={item.path}
                    to={item.path}
                    onClick={() => setSidebarOpen(false)}
                    className={`flex items-center space-x-3 px-4 py-3 rounded-xl transition-all duration-200 group ${
                      active
                        ? 'bg-gradient-to-r from-primary-600 to-primary-700 text-white shadow-md'
                        : 'text-gray-700 hover:bg-gray-50 hover:text-primary-600'
                    }`}
                  >
                    <Icon 
                      size={20} 
                      className={active ? 'text-white' : 'text-gray-500 group-hover:text-primary-600'} 
                    />
                    <span className={`font-medium ${active ? 'text-white' : ''}`}>{item.label}</span>
                  </Link>
                );
              })}
            </nav>
          </div>
        </aside>

        {/* Overlay pour mobile */}
        {sidebarOpen && (
          <div
            className="fixed inset-0 bg-black/50 backdrop-blur-sm z-30 lg:hidden"
            onClick={() => setSidebarOpen(false)}
          />
        )}

        {/* Main Content */}
        <main className="flex-1 lg:ml-0 min-h-[calc(100vh-4rem)]">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <div className="animate-fade-in">
              {children || <Outlet />}
            </div>
          </div>
        </main>
      </div>
    </div>
  );
};

export default Layout;
