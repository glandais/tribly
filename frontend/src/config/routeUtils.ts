import { matchPath, generatePath } from 'react-router-dom'
import { routesConfig } from './routes.config'
import { getCurrentLocale } from './locale-context'
import { DEFAULT_LOCALE } from './paths'
import type { RouteConfig, RouteParams } from './routes.types'

export const routeById = new Map<string, RouteConfig>(
  routesConfig.map((route) => [route.id, route])
)

/**
 * Find the route config that matches the current pathname. Each config is
 * registered against all locale variants, so any language of the URL matches.
 */
export function findMatchingRoute(pathname: string): {
  route: RouteConfig
  params: RouteParams
} | null {
  for (const route of routesConfig) {
    for (const pattern of Object.values(route.paths)) {
      const match = matchPath(pattern, pathname)
      if (match) {
        return { route, params: match.params as RouteParams }
      }
    }
  }
  return null
}

export function buildBreadcrumbChain(routeId: string): RouteConfig[] {
  const chain: RouteConfig[] = []
  let currentId: string | null = routeId

  while (currentId) {
    const route = routeById.get(currentId)
    if (!route) break

    if (route.breadcrumb) {
      chain.unshift(route)
    }

    currentId = route.parentId
  }

  return chain
}

/**
 * Build the URL for a route in the current locale, substituting params.
 */
export function buildRoutePath(route: RouteConfig, params: RouteParams): string {
  const pattern = route.paths[getCurrentLocale()] ?? route.paths[DEFAULT_LOCALE]
  return generatePath(pattern, params as Record<string, string>)
}

export function getRouteById(id: string): RouteConfig | undefined {
  return routeById.get(id)
}
