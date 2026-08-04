import { ErrorBoundary } from '@/components/common/ErrorBoundary'
import { Navigate, type RouteObject } from 'react-router-dom'
import type { QueryClient } from '@tanstack/react-query'
import { routesConfig } from './routes.config'
import { runRoutePrefetch } from './runRoutePrefetch'
import type { RouteConfig, AuthRequirement } from './routes.types'
import { AuthenticatedRoute, UnauthenticatedRoute } from '../components/auth/ProtectedRoute'
import { Layout } from '../components/common/Layout'
import { NotFoundPage } from '../pages/NotFoundPage'
import { paths } from './paths'
import { isSingleTeam } from './appConfig'

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
 * Wrap a route's prefetch into a React Router loader. The running and the error handling live in
 * `runRoutePrefetch`, shared with the hover path (`lib/prefetch.ts`) so a link and the navigation it
 * leads to fetch exactly the same thing. The loader always resolves to null: a failed prefetch never
 * blocks the render.
 */
function makeLoader(config: RouteConfig, queryClient: QueryClient) {
  return async ({
    request,
    params,
  }: {
    request: Request
    params: Record<string, string | undefined>
  }) => {
    await runRoutePrefetch(config, queryClient, params, new URL(request.url), 'loader')
    return null
  }
}

/**
 * Emit one RouteObject per locale variant so any URL matches the same component regardless of the
 * user's current language. Used by BOTH the client data router (App.tsx) and the server static
 * handler (entry-server.tsx).
 */
function buildRoutesForConfig(config: RouteConfig, queryClient: QueryClient): RouteObject[] {
  const gated = Boolean(config.hideWhenSingleTeam && isSingleTeam())

  const Component = config.component
  const element = gated ? (
    <Navigate to={paths.home()} replace />
  ) : (
    wrapWithAuth(
      // Per-route boundary: a crash in one page shows an error block instead of a blank app.
      <ErrorBoundary>
        <Component />
      </ErrorBoundary>,
      config.auth
    )
  )

  // Gated redirects carry no prefetch: the component never renders here.
  const loader = config.prefetch && !gated ? makeLoader(config, queryClient) : undefined

  // Expose the route's SSR meta() (link previews) on `handle` so the static handler's matched leaf
  // carries it — entry-server reads leafMatch.route.handle.meta after the loaders have run. `routeId`
  // rides along the same mechanism to identify the matched route (routes.config.ts's own `id`,
  // stable across locale variants — RouteObject.id itself can't be reused for that, React Router
  // requires it unique per object and each locale variant is a separate object). Gated redirects
  // render no page, so they carry no handle.
  const handle = gated
    ? undefined
    : { routeId: config.id, ...(config.meta && { meta: config.meta }) }

  if (config.index) {
    return [{ index: true, element, loader, handle }]
  }

  const uniquePaths = [...new Set(Object.values(config.paths))]
  return uniquePaths.map((path) => ({ path, element, loader, handle }))
}

export function buildRoutes(queryClient: QueryClient): RouteObject[] {
  // `layout: 'bare'` routes render outside the shared <Layout> AppShell (no header/footer/
  // breadcrumbs), as siblings of the "/" branch. Auth wrapping still applies via
  // buildRoutesForConfig.
  const bareConfigs = routesConfig.filter((config) => config.layout === 'bare')
  const appConfigs = routesConfig.filter((config) => config.layout !== 'bare')

  return [
    ...bareConfigs.flatMap((config) => buildRoutesForConfig(config, queryClient)),
    {
      path: '/',
      element: <Layout />,
      children: [
        ...appConfigs.flatMap((config) => buildRoutesForConfig(config, queryClient)),
        { path: '*', element: <NotFoundPage /> },
      ],
    },
  ]
}
