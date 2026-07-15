import { Suspense } from 'react'
import { Navigate, type RouteObject } from 'react-router-dom'
import type { QueryClient } from '@tanstack/react-query'
import Axios from 'axios'
import { routesConfig } from './routes.config'
import type { RouteConfig, AuthRequirement, RouteParams } from './routes.types'
import { AuthenticatedRoute, UnauthenticatedRoute } from '../components/auth/ProtectedRoute'
import { Layout } from '../components/common/Layout'
import { NotFoundPage } from '../pages/NotFoundPage'
import { paths } from './paths'
import { isSingleTeam } from './appConfig'
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
 * Wrap a route's prefetch into a React Router loader. Runs in both environments; anonymous on the
 * server and pre-auth on the client, so 401/403/404 are expected and only warned. Any other error
 * is logged but swallowed — the loader always resolves to null so the render proceeds and each
 * component handles its own loading/error state.
 */
function makeLoader(config: RouteConfig, queryClient: QueryClient) {
  return async ({
    request,
    params,
  }: {
    request: Request
    params: Record<string, string | undefined>
  }) => {
    // Filter out undefined values so prefetch functions receive guaranteed strings.
    const definedParams = Object.fromEntries(
      Object.entries(params).filter((entry): entry is [string, string] => entry[1] !== undefined)
    )
    try {
      await config.prefetch!(queryClient, definedParams as RouteParams)
    } catch (err) {
      const isExpected =
        Axios.isAxiosError(err) && [401, 403, 404].includes(err.response?.status ?? 0)
      const logFn = isExpected ? console.warn : console.error
      logFn(
        `[SSR] prefetch failed for route "${config.id}" url="${request.url}" params=${JSON.stringify(definedParams)}:`,
        err
      )
    }
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
      <Suspense fallback={<Loader />}>
        <Component />
      </Suspense>,
      config.auth
    )
  )

  // Gated redirects carry no prefetch: the component never renders here.
  const loader = config.prefetch && !gated ? makeLoader(config, queryClient) : undefined

  // Expose the route's SSR meta() (link previews) on `handle` so the static handler's matched leaf
  // carries it — entry-server reads leafMatch.route.handle.meta after the loaders have run. Gated
  // redirects render no page, so they carry no meta.
  const handle = !gated && config.meta ? { meta: config.meta } : undefined

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
