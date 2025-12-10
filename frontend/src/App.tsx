import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Layout } from './components/common/Layout';
import { ErrorBoundary } from './components/common/ErrorBoundary';

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

            {/* Auth routes - to be implemented */}
            <Route path="login" element={<div>Login Page</div>} />
            <Route path="auth/callback/:provider" element={<div>OAuth Callback</div>} />

            {/* Team routes - to be implemented */}
            <Route path="teams" element={<div>Teams List</div>} />
            <Route path="teams/new" element={<div>Create Team</div>} />
            <Route path="teams/:teamSlug" element={<div>Team Detail</div>} />
            <Route path="teams/:teamSlug/settings" element={<div>Team Settings</div>} />

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

            {/* User profile routes - to be implemented */}
            <Route path="profile" element={<div>User Profile</div>} />
            <Route path="profile/settings" element={<div>Profile Settings</div>} />

            {/* Catch all */}
            <Route path="*" element={<NotFoundPage />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </ErrorBoundary>
  );
}

export default App;
