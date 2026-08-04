import { matchRoutes, generatePath, type RouteObject } from 'react-router-dom'
import { routesConfig } from './routes.config'
import { getCurrentLocale } from './locale-context'
import { DEFAULT_LOCALE } from './paths'
import type { RouteConfig, RouteParams } from './routes.types'

export const routeById = new Map<string, RouteConfig>(
  routesConfig.map((route) => [route.id, route])
)

/**
 * One entry per locale variant of every route — the same flattening RouteGenerator does, minus the
 * elements. Matching goes through React Router's own `matchRoutes` rather than a hand-rolled scan so
 * a path is resolved by *specificity*, exactly as the router will resolve it on the click that
 * follows: `/equipes/nouvelle` is the team-creation page, never `team-detail` with
 * `teamSlug=nouvelle`. A first-match-wins loop over routesConfig agrees only as long as nobody
 * reorders the file.
 */
const matchTable: RouteObject[] = routesConfig.flatMap((route) =>
  [...new Set(Object.values(route.paths))].map((path) => ({ path, handle: route }))
)

/**
 * Find the route config that matches a pathname. Each config is registered against all locale
 * variants, so any language of the URL matches.
 */
export function findMatchingRoute(pathname: string): {
  route: RouteConfig
  params: RouteParams
} | null {
  const matches = matchRoutes(matchTable, pathname)
  const match = matches?.[0]
  if (!match) return null
  return { route: match.route.handle as RouteConfig, params: match.params as RouteParams }
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
