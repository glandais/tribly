import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { Layout } from './components/common/Layout';
import { ErrorBoundary } from './components/common/ErrorBoundary';
import { LoginPage } from './pages/auth/LoginPage';
import { UserProfilePage } from './pages/auth/UserProfilePage';
import { AuthenticatedRoute, UnauthenticatedRoute } from './components/auth/ProtectedRoute';
import { TeamListPage } from './pages/team/TeamListPage';
import { MyTeamsPage } from './pages/team/MyTeamsPage';
import { TeamDetailPage } from './pages/team/TeamDetailPage';
import { TeamMembersPage } from './pages/team/TeamMembersPage';
import { CreateTeamPage } from './pages/team/CreateTeamPage';
import { TeamSettingsPage } from './pages/team/TeamSettingsPage';
import { RideListPage } from './pages/ride/RideListPage';
import { RideDetailPage } from './pages/ride/RideDetailPage';
import { CreateRidePage } from './pages/ride/CreateRidePage';
import { EditRidePage } from './pages/ride/EditRidePage';
import { RouteListPage } from './pages/route/RouteListPage';
import { RouteDetailPage } from './pages/route/RouteDetailPage';
import { CreateRoutePage } from './pages/route/CreateRoutePage';
import { EditRoutePage } from './pages/route/EditRoutePage';

function HomePage() {
  const { t } = useTranslation('auth');
  return (
    <div className="flex flex-col items-center justify-center min-h-[60vh]">
      <h1 className="text-4xl font-bold text-gray-900 mb-4">{t('home.title')}</h1>
      <p className="text-lg text-gray-600">{t('home.subtitle')}</p>
    </div>
  );
}

function NotFoundPage() {
  const { t } = useTranslation('errors');
  return (
    <div className="flex flex-col items-center justify-center min-h-[60vh]">
      <h1 className="text-4xl font-bold text-gray-900 mb-4">{t('notFound.title')}</h1>
      <p className="text-lg text-gray-600 mb-6">{t('notFound.message')}</p>
      <Link
        to="/"
        className="text-indigo-600 hover:text-indigo-500 font-medium"
      >
        {t('notFound.backHome')}
      </Link>
    </div>
  );
}

function Placeholder({ textKey }: { textKey: string }) {
  const { t } = useTranslation('common');
  return (
    <div className="flex items-center justify-center min-h-[40vh]">
      <p className="text-lg text-gray-500">{t(textKey)}</p>
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
              path="myteams"
              element={
                <AuthenticatedRoute>
                  <MyTeamsPage />
                </AuthenticatedRoute>
              }
            />
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
              path="teams/:teamSlug/members"
              element={
                <AuthenticatedRoute>
                  <TeamMembersPage />
                </AuthenticatedRoute>
              }
            />
            <Route
              path="teams/:teamSlug/settings"
              element={
                <AuthenticatedRoute>
                  <TeamSettingsPage />
                </AuthenticatedRoute>
              }
            />

            {/* Ride routes */}
            <Route path="teams/:teamSlug/rides" element={<RideListPage />} />
            <Route
              path="teams/:teamSlug/rides/new"
              element={
                <AuthenticatedRoute>
                  <CreateRidePage />
                </AuthenticatedRoute>
              }
            />
            <Route path="teams/:teamSlug/rides/:rideSlug" element={<RideDetailPage />} />
            <Route
              path="teams/:teamSlug/rides/:rideSlug/edit"
              element={
                <AuthenticatedRoute>
                  <EditRidePage />
                </AuthenticatedRoute>
              }
            />

            {/* Trip routes - to be implemented */}
            <Route path="teams/:teamSlug/trips" element={<Placeholder textKey="placeholder.tripsList" />} />
            <Route path="teams/:teamSlug/trips/new" element={<Placeholder textKey="placeholder.createTrip" />} />
            <Route path="teams/:teamSlug/trips/:tripId" element={<Placeholder textKey="placeholder.tripDetail" />} />

            {/* Route routes */}
            <Route path="teams/:teamSlug/routes" element={<RouteListPage />} />
            <Route
              path="teams/:teamSlug/routes/new"
              element={
                <AuthenticatedRoute>
                  <CreateRoutePage />
                </AuthenticatedRoute>
              }
            />
            <Route path="teams/:teamSlug/routes/:routeId" element={<RouteDetailPage />} />
            <Route
              path="teams/:teamSlug/routes/:routeId/edit"
              element={
                <AuthenticatedRoute>
                  <EditRoutePage />
                </AuthenticatedRoute>
              }
            />

            {/* Catch all */}
            <Route path="*" element={<NotFoundPage />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </ErrorBoundary>
  );
}

export default App;
