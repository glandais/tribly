import { Suspense } from 'react'
import { Routes, Route } from 'react-router-dom'
import { routesConfig } from './routes.config'
import type { RouteConfig, AuthRequirement } from './routes.types'
import { AuthenticatedRoute, UnauthenticatedRoute } from '../components/auth/ProtectedRoute'
import { Layout } from '../components/common/Layout'
import { LoadingSpinner } from '../components/common/LoadingSpinner'
import { NotFoundPage } from '../pages/NotFoundPage'

/**
 * Wrap component based on auth requirement
 */
function wrapWithAuth(element: React.ReactNode, auth: AuthRequirement): React.ReactNode {
  switch (auth) {
    case 'authenticated':
      return <AuthenticatedRoute>{element}</AuthenticatedRoute>
    case 'unauthenticated':
      return <UnauthenticatedRoute>{element}</UnauthenticatedRoute>
    case 'public':
    default:
      return element
  }
}

/**
 * Generate a Route element from configuration
 */
function generateRoute(config: RouteConfig): React.ReactNode {
  const Component = config.component

  const element = (
    <Suspense fallback={<LoadingSpinner />}>
      <Component />
    </Suspense>
  )

  const wrappedElement = wrapWithAuth(element, config.auth)

  // Handle index routes
  if (config.index) {
    return <Route key={config.id} index element={wrappedElement} />
  }

  return <Route key={config.id} path={config.path} element={wrappedElement} />
}

/**
 * Main route generator component
 */
export function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<Layout />}>
        {routesConfig.map(generateRoute)}
        <Route path="*" element={<NotFoundPage />} />
      </Route>
    </Routes>
  )
}
