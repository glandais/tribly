import { useEffect } from 'react'
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { ArrowPathIcon } from '@heroicons/react/24/outline'
import { Layout } from './components/common/Layout'
import { ErrorBoundary } from './components/common/ErrorBoundary'
import { LoginPage } from './pages/auth/LoginPage'
import { UserProfilePage } from './pages/auth/UserProfilePage'
import { AuthenticatedRoute, UnauthenticatedRoute } from './components/auth/ProtectedRoute'
import { TeamListPage } from './pages/team/TeamListPage'
import { MyTeamsPage } from './pages/team/MyTeamsPage'
import { TeamDetailPage } from './pages/team/TeamDetailPage'
import { TeamMembersPage } from './pages/team/TeamMembersPage'
import { CreateTeamPage } from './pages/team/CreateTeamPage'
import { TeamSettingsPage } from './pages/team/TeamSettingsPage'
import { RideListPage } from './pages/ride/RideListPage'
import { RideDetailPage } from './pages/ride/RideDetailPage'
import { CreateRidePage } from './pages/ride/CreateRidePage'
import { EditRidePage } from './pages/ride/EditRidePage'
import { TripListPage } from './pages/trip/TripListPage'
import { TripDetailPage } from './pages/trip/TripDetailPage'
import { CreateTripPage } from './pages/trip/CreateTripPage'
import { EditTripPage } from './pages/trip/EditTripPage'
import { PostListPage } from './pages/post/PostListPage'
import { PostDetailPage } from './pages/post/PostDetailPage'
import { CreatePostPage } from './pages/post/CreatePostPage'
import { EditPostPage } from './pages/post/EditPostPage'
import { PublicationListPage } from './pages/publication/PublicationListPage'
import { RouteListPage } from './pages/route/RouteListPage'
import { RouteDetailPage } from './pages/route/RouteDetailPage'
import { CreateRoutePage } from './pages/route/CreateRoutePage'
import { EditRoutePage } from './pages/route/EditRoutePage'
import { HomePage } from './pages/home/HomePage'
import { useAuthStore } from './store/authStore'

function NotFoundPage() {
  const { t } = useTranslation('errors')
  return (
    <div className="flex flex-col items-center justify-center min-h-[60vh]">
      <h1 className="text-4xl font-bold text-gray-900 mb-4">{t('notFound.title')}</h1>
      <p className="text-lg text-gray-600 mb-6">{t('notFound.message')}</p>
      <Link to="/" className="text-indigo-600 hover:text-indigo-500 font-medium">
        {t('notFound.backHome')}
      </Link>
    </div>
  )
}

function App() {
  const isInitialized = useAuthStore((state) => state.isInitialized)
  const initialize = useAuthStore((state) => state.initialize)
  const { t } = useTranslation('common')

  // Initialize auth on mount
  useEffect(() => {
    initialize()
  }, [initialize])

  // Wait for auth initialization before rendering routes
  if (!isInitialized) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <ArrowPathIcon className="inline-block h-8 w-8 animate-spin text-indigo-600" />
          <p className="mt-4 text-gray-600">{t('status.checkingAuth')}</p>
        </div>
      </div>
    )
  }

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
              path="teams/:teamSlug/edit"
              element={
                <AuthenticatedRoute>
                  <TeamSettingsPage />
                </AuthenticatedRoute>
              }
            />

            {/* Publications route */}
            <Route path="teams/:teamSlug/publications" element={<PublicationListPage />} />

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

            {/* Trip routes */}
            <Route path="teams/:teamSlug/trips" element={<TripListPage />} />
            <Route
              path="teams/:teamSlug/trips/new"
              element={
                <AuthenticatedRoute>
                  <CreateTripPage />
                </AuthenticatedRoute>
              }
            />
            <Route path="teams/:teamSlug/trips/:tripSlug" element={<TripDetailPage />} />
            <Route
              path="teams/:teamSlug/trips/:tripSlug/edit"
              element={
                <AuthenticatedRoute>
                  <EditTripPage />
                </AuthenticatedRoute>
              }
            />

            {/* Post routes */}
            <Route path="teams/:teamSlug/posts" element={<PostListPage />} />
            <Route
              path="teams/:teamSlug/posts/new"
              element={
                <AuthenticatedRoute>
                  <CreatePostPage />
                </AuthenticatedRoute>
              }
            />
            <Route path="teams/:teamSlug/posts/:postSlug" element={<PostDetailPage />} />
            <Route
              path="teams/:teamSlug/posts/:postSlug/edit"
              element={
                <AuthenticatedRoute>
                  <EditPostPage />
                </AuthenticatedRoute>
              }
            />

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
            <Route path="teams/:teamSlug/routes/:routeSlug" element={<RouteDetailPage />} />
            <Route
              path="teams/:teamSlug/routes/:routeSlug/edit"
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
  )
}

export default App
