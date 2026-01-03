import { matchPath } from 'react-router-dom'
import { routesConfig } from './routes.config'
import type { RouteConfig, RouteParams } from './routes.types'

/**
 * Map of route ID to route config for fast lookups
 */
export const routeById = new Map<string, RouteConfig>(
  routesConfig.map((route) => [route.id, route])
)

/**
 * Find the route config that matches the current pathname
 * Routes are checked in order, so more specific routes should come first
 */
export function findMatchingRoute(pathname: string): {
  route: RouteConfig
  params: RouteParams
} | null {
  for (const route of routesConfig) {
    const match = matchPath(route.path, pathname)
    if (match) {
      return {
        route,
        params: match.params as RouteParams,
      }
    }
  }
  return null
}

/**
 * Build the breadcrumb chain by following parent references
 * Returns array from root to current (e.g., [teams, team-detail, ride-detail])
 */
export function buildBreadcrumbChain(routeId: string): RouteConfig[] {
  const chain: RouteConfig[] = []
  let currentId: string | null = routeId

  while (currentId) {
    const route = routeById.get(currentId)
    if (!route) break

    // Only include routes that have breadcrumb config
    if (route.breadcrumb) {
      chain.unshift(route)
    }

    currentId = route.parentId
  }

  return chain
}

/**
 * Build the URL path for a route, substituting params
 */
export function buildRoutePath(route: RouteConfig, params: RouteParams): string {
  let path = route.path

  if (params.teamSlug) {
    path = path.replace(':teamSlug', params.teamSlug)
  }
  if (params.rideSlug) {
    path = path.replace(':rideSlug', params.rideSlug)
  }
  if (params.postSlug) {
    path = path.replace(':postSlug', params.postSlug)
  }
  if (params.tripSlug) {
    path = path.replace(':tripSlug', params.tripSlug)
  }
  if (params.routeSlug) {
    path = path.replace(':routeSlug', params.routeSlug)
  }
  if (params.templateSlug) {
    path = path.replace(':templateSlug', params.templateSlug)
  }
  if (params.adSlug) {
    path = path.replace(':adSlug', params.adSlug)
  }
  if (params.pageSlug) {
    path = path.replace(':pageSlug', params.pageSlug)
  }

  return path
}

/**
 * Get a route config by ID
 */
export function getRouteById(id: string): RouteConfig | undefined {
  return routeById.get(id)
}
