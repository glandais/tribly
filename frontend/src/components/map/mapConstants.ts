import type { MinRole } from '@/api/dto'
import type { RouteFilters } from '@/hooks/filters/routeFilters'

/** Layer name given to ST_AsMVT by the backend; MapLibre needs it as `source-layer`. */
export const ROUTES_SOURCE_LAYER = 'routes'

/**
 * The subset of the route filters a tile understands. Sorting and pagination are deliberately left
 * out: a tile holds every matching route, and its projection is an aggregate that an ORDER BY would
 * break.
 */
export type RouteTileFilters = Pick<
  RouteFilters,
  | 'search'
  | 'minDistance'
  | 'maxDistance'
  | 'minElevationGain'
  | 'maxElevationGain'
  | 'hilliness'
  | 'surfaceType'
  | 'windDirection'
> & { minRole?: MinRole }

/** Drops the sort and the page, which the tile endpoints reject. */
export const toRouteTileFilters = (filters: RouteFilters): RouteTileFilters => ({
  search: filters.search,
  minDistance: filters.minDistance,
  maxDistance: filters.maxDistance,
  minElevationGain: filters.minElevationGain,
  maxElevationGain: filters.maxElevationGain,
  hilliness: filters.hilliness,
  surfaceType: filters.surfaceType,
  windDirection: filters.windDirection,
})

/**
 * MapLibre fetches tiles itself, outside the axios instance, so it never sends the bearer token.
 * Same-origin requests carry the session cookie instead, which the backend falls back to — the
 * same mechanism that authenticates secured images. Hence absolute, same-origin URLs.
 */
const tilesUrl = (path: string, filters?: RouteTileFilters) => {
  // MapLibre only reads/fetches this URL after client-side hydration, so an SSR render never needs
  // it resolved — a relative path is enough to avoid touching `window` on the server.
  const origin = typeof window === 'undefined' ? '' : window.location.origin
  return `${origin}${path}${tilesQuery(filters)}`
}

/** The `{z}/{x}/{y}` placeholders live in the path, so a query string appends cleanly. */
const tilesQuery = (filters?: RouteTileFilters) => {
  if (!filters) return ''
  const params = new URLSearchParams()
  for (const [key, value] of Object.entries(filters)) {
    if (value !== undefined && value !== '') {
      params.set(key, String(value))
    }
  }
  const query = params.toString()
  return query ? `?${query}` : ''
}

export const allRoutesTilesUrl = (filters?: RouteTileFilters) =>
  tilesUrl('/api/routes/tiles/{z}/{x}/{y}.mvt', filters)

export const teamRoutesTilesUrl = (teamSlug: string, filters?: RouteTileFilters) =>
  tilesUrl(`/api/teams/${encodeURIComponent(teamSlug)}/routes/tiles/{z}/{x}/{y}.mvt`, filters)

/** Metropolitan France, the default view before any route is in sight. */
export const DEFAULT_MAP_VIEW = { longitude: 2.4, latitude: 46.6, zoom: 4.6 }

/**
 * Framing of a route set. The zoom is capped because a lone short route has a near-punctual
 * extent, which `fitBounds` would otherwise answer with the deepest zoom the style allows.
 */
export const ROUTES_FIT_OPTIONS = { padding: 40, maxZoom: 13, duration: 0 } as const

export const ROUTE_LINE_COLOR = '#1d32a8'
export const ROUTE_LINE_HOVER_COLOR = '#c90808'
