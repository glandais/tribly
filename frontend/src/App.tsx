import { useEffect } from 'react'
import { RouterProvider, createBrowserRouter, UNSAFE_createRouter } from 'react-router-dom'
import type { QueryClient } from '@tanstack/react-query'
import { ErrorBoundary } from './components/common/ErrorBoundary'
import { buildRoutes } from './config/RouteGenerator'
import { useAuthStore } from './store/authStore'
import { useAuth } from './hooks/useAuth'
import { prefetchCommonRoutes } from './lib/prefetch'
import { getPinnedHistory } from './config/pinnedHistory'

const isServer = typeof window === 'undefined'

type AppRouter = ReturnType<typeof createBrowserRouter>

// Module singleton: the router is created once for the lifetime of the client session.
let router: AppRouter | undefined

/**
 * Lazily create the client data router. On a pinned single-team host it wraps the hidden-prefix
 * history (built by getPinnedHistory) with UNSAFE_createRouter — which requires an explicit
 * initialize() — otherwise a plain browser router. Server code must use buildRoutes() directly via
 * entry-server.tsx and never reach this.
 */
function getRouter(queryClient: QueryClient): AppRouter {
  if (isServer) {
    throw new Error(
      '[SSR] getRouter() must not be called on the server — use buildRoutes() in entry-server.tsx'
    )
  }
  if (!router) {
    const routes = buildRoutes(queryClient)
    const pinned = getPinnedHistory()
    router = pinned
      ? UNSAFE_createRouter({ routes, history: pinned }).initialize()
      : createBrowserRouter(routes)
  }
  return router
}

/**
 * Effects-only companion to the router. Initializes auth and warms common routes once auth settles.
 * Renders nothing: the router (and hence the app shell) renders immediately, unblocked by auth —
 * authenticated-only content resolves client-side after hydration.
 */
function AuthEffects() {
  const isInitialized = useAuthStore((state) => state.isInitialized)
  const initialize = useAuthStore((state) => state.initialize)
  // useAuth triggers the /me query so auth state is populated for the rest of the tree.
  const { isLoading } = useAuth()

  useEffect(() => {
    initialize()
  }, [initialize])

  useEffect(() => {
    if (isInitialized && !isLoading) {
      prefetchCommonRoutes()
    }
  }, [isInitialized, isLoading])

  return null
}

export default function App({ queryClient }: { queryClient: QueryClient }) {
  return (
    <ErrorBoundary>
      <AuthEffects />
      <RouterProvider router={getRouter(queryClient)} />
    </ErrorBoundary>
  )
}
