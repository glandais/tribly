import { Suspense } from 'react'
import { Routes, Route } from 'react-router-dom'
import { routesConfig } from './routes.config'
import type { RouteConfig, AuthRequirement } from './routes.types'
import { AuthenticatedRoute, UnauthenticatedRoute } from '../components/auth/ProtectedRoute'
import { Layout } from '../components/common/Layout'
import { NotFoundPage } from '../pages/NotFoundPage'
import { Loader } from '@mantine/core'

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
 * Emit one React Router `<Route>` per locale variant so any URL matches the
 * same component regardless of the user's current language.
 */
function generateRoutes(config: RouteConfig): React.ReactNode[] {
  const Component = config.component

  const element = (
    <Suspense fallback={<Loader />}>
      <Component />
    </Suspense>
  )

  const wrappedElement = wrapWithAuth(element, config.auth)

  if (config.index) {
    return [<Route key={`${config.id}:index`} index element={wrappedElement} />]
  }

  const uniquePaths = [...new Set(Object.values(config.paths))]
  return uniquePaths.map((path) => (
    <Route key={`${config.id}:${path}`} path={path} element={wrappedElement} />
  ))
}

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<Layout />}>
        {routesConfig.flatMap(generateRoutes)}
        <Route path="*" element={<NotFoundPage />} />
      </Route>
    </Routes>
  )
}
