import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext.jsx';
import ProtectedRoute from './components/ProtectedRoute.jsx';
import Login from './pages/Login.jsx';

// Pages Formateur
import FormateurDashboard from './pages/formateur/Dashboard.jsx';
import FormateurCours from './pages/formateur/Cours.jsx';
import FormateurCoursDetails from './pages/formateur/CoursDetails.jsx';
import FormateurSeances from './pages/formateur/Seances.jsx';
import FormateurCreateSeance from './pages/formateur/CreateSeance.jsx';
import FormateurEditSeance from './pages/formateur/EditSeance.jsx';
import FormateurNotes from './pages/formateur/Notes.jsx';
import FormateurStatistiques from './pages/formateur/Statistiques.jsx';

// Pages Étudiant
import EtudiantDashboard from './pages/etudiant/Dashboard.jsx';
import EtudiantCours from './pages/etudiant/Cours.jsx';
import EtudiantCoursDetails from './pages/etudiant/CoursDetails.jsx';
import EtudiantInscriptionCours from './pages/etudiant/InscriptionCours.jsx';
import EtudiantNotes from './pages/etudiant/Notes.jsx';
import EtudiantPlanning from './pages/etudiant/Planning.jsx';

function AppRoutes() {
  const { isAuthenticated, hasRole } = useAuth();

  return (
    <Routes>
      {/* Route publique */}
      <Route path="/login" element={!isAuthenticated ? <Login /> : <Navigate to="/dashboard" />} />

      {/* Routes Formateur */}
      <Route
        path="/formateur/dashboard"
        element={
          <ProtectedRoute requiredRole="FORMATEUR">
            <FormateurDashboard />
          </ProtectedRoute>
        }
      />
      <Route
        path="/formateur/cours"
        element={
          <ProtectedRoute requiredRole="FORMATEUR">
            <FormateurCours />
          </ProtectedRoute>
        }
      />
      <Route
        path="/formateur/cours/:code"
        element={
          <ProtectedRoute requiredRole="FORMATEUR">
            <FormateurCoursDetails />
          </ProtectedRoute>
        }
      />
      <Route
        path="/formateur/seances"
        element={
          <ProtectedRoute requiredRole="FORMATEUR">
            <FormateurSeances />
          </ProtectedRoute>
        }
      />
      <Route
        path="/formateur/seances/new"
        element={
          <ProtectedRoute requiredRole="FORMATEUR">
            <FormateurCreateSeance />
          </ProtectedRoute>
        }
      />
      <Route
        path="/formateur/seances/:id/edit"
        element={
          <ProtectedRoute requiredRole="FORMATEUR">
            <FormateurEditSeance />
          </ProtectedRoute>
        }
      />
      <Route
        path="/formateur/notes"
        element={
          <ProtectedRoute requiredRole="FORMATEUR">
            <FormateurNotes />
          </ProtectedRoute>
        }
      />
      <Route
        path="/formateur/statistiques"
        element={
          <ProtectedRoute requiredRole="FORMATEUR">
            <FormateurStatistiques />
          </ProtectedRoute>
        }
      />

      {/* Routes Étudiant */}
      <Route
        path="/etudiant/dashboard"
        element={
          <ProtectedRoute requiredRole="ETUDIANT">
            <EtudiantDashboard />
          </ProtectedRoute>
        }
      />
      <Route
        path="/etudiant/cours"
        element={
          <ProtectedRoute requiredRole="ETUDIANT">
            <EtudiantCours />
          </ProtectedRoute>
        }
      />
      <Route
        path="/etudiant/cours/:code"
        element={
          <ProtectedRoute requiredRole="ETUDIANT">
            <EtudiantCoursDetails />
          </ProtectedRoute>
        }
      />
      <Route
        path="/etudiant/inscription"
        element={
          <ProtectedRoute requiredRole="ETUDIANT">
            <EtudiantInscriptionCours />
          </ProtectedRoute>
        }
      />
      <Route
        path="/etudiant/notes"
        element={
          <ProtectedRoute requiredRole="ETUDIANT">
            <EtudiantNotes />
          </ProtectedRoute>
        }
      />
      <Route
        path="/etudiant/planning"
        element={
          <ProtectedRoute requiredRole="ETUDIANT">
            <EtudiantPlanning />
          </ProtectedRoute>
        }
      />

      {/* Route racine - redirection selon le rôle */}
      <Route
        path="/"
        element={
          isAuthenticated ? (
            hasRole('ADMIN') ? (
              <Navigate to="/admin/dashboard" replace />
            ) : hasRole('FORMATEUR') ? (
              <Navigate to="/formateur/dashboard" replace />
            ) : hasRole('ETUDIANT') ? (
              <Navigate to="/etudiant/dashboard" replace />
            ) : (
              <Navigate to="/login" replace />
            )
          ) : (
            <Navigate to="/login" replace />
          )
        }
      />

      {/* Route par défaut */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <AppRoutes />
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;

