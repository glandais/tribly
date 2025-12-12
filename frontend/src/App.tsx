import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { Layout } from './components/common/Layout';
import { ErrorBoundary } from './components/common/ErrorBoundary';
import { LoginPage } from './pages/auth/LoginPage';
import { OAuthCallbackPage } from './pages/auth/OAuthCallbackPage';
import { UserProfilePage } from './pages/auth/UserProfilePage';
import { AuthenticatedRoute, UnauthenticatedRoute } from './components/auth/ProtectedRoute';
import { TeamListPage } from './pages/team/TeamListPage';
import { TeamDetailPage } from './pages/team/TeamDetailPage';
import { CreateTeamPage } from './pages/team/CreateTeamPage';
import { TeamSettingsPage } from './pages/team/TeamSettingsPage';

function HomePage() {
  return (
    <div className="flex flex-col items-center justify-center min-h-[60vh]">
      <h1 className="text-4xl font-bold text-gray-900 mb-4">Welcome to Tribly</h1>
      <p className="text-lg text-gray-600">Your cycling team management platform</p>
    </div>
  );
}

function NotFoundPage() {
  return (
    <div className="flex flex-col items-center justify-center min-h-[60vh]">
      <h1 className="text-4xl font-bold text-gray-900 mb-4">404</h1>
      <p className="text-lg text-gray-600">Page not found</p>
    </div>
  );
}

function App() {
  return (
    <ErrorBoundary>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Layout />}>
            <Route index element={<HomePage />} />

            {/* Auth routes */}
            <Route
              path="login"
              element={
                <UnauthenticatedRoute>
                  <LoginPage />
                </UnauthenticatedRoute>
              }
            />
            <Route path="auth/callback/:provider" element={<OAuthCallbackPage />} />

            {/* User profile routes */}
            <Route
              path="profile"
              element={
                <AuthenticatedRoute>
                  <UserProfilePage />
                </AuthenticatedRoute>
              }
            />
            <Route
              path="profile/settings"
              element={
                <AuthenticatedRoute>
                  <UserProfilePage />
                </AuthenticatedRoute>
              }
            />

            {/* Team routes */}
            <Route path="teams" element={<TeamListPage />} />
            <Route
              path="teams/new"
              element={
                <AuthenticatedRoute>
                  <CreateTeamPage />
                </AuthenticatedRoute>
              }
            />
            <Route path="teams/:teamSlug" element={<TeamDetailPage />} />
            <Route
              path="teams/:teamSlug/settings"
              element={
                <AuthenticatedRoute>
                  <TeamSettingsPage />
                </AuthenticatedRoute>
              }
            />

            {/* Ride routes - to be implemented */}
            <Route path="teams/:teamSlug/rides" element={<div>Rides List</div>} />
            <Route path="teams/:teamSlug/rides/new" element={<div>Create Ride</div>} />
            <Route path="teams/:teamSlug/rides/:rideId" element={<div>Ride Detail</div>} />

            {/* Trip routes - to be implemented */}
            <Route path="teams/:teamSlug/trips" element={<div>Trips List</div>} />
            <Route path="teams/:teamSlug/trips/new" element={<div>Create Trip</div>} />
            <Route path="teams/:teamSlug/trips/:tripId" element={<div>Trip Detail</div>} />

            {/* Route routes - to be implemented */}
            <Route path="teams/:teamSlug/routes" element={<div>Routes List</div>} />
            <Route path="teams/:teamSlug/routes/new" element={<div>Create Route</div>} />
            <Route path="teams/:teamSlug/routes/:routeId" element={<div>Route Detail</div>} />

            {/* Catch all */}
            <Route path="*" element={<NotFoundPage />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </ErrorBoundary>
  );
}

export default App;
