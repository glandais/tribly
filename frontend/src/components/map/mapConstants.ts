/** Layer name given to ST_AsMVT by the backend; MapLibre needs it as `source-layer`. */
export const ROUTES_SOURCE_LAYER = 'routes'

/**
 * MapLibre fetches tiles itself, outside the axios instance, so it never sends the bearer token.
 * Same-origin requests carry the session cookie instead, which the backend falls back to — the
 * same mechanism that authenticates secured images. Hence absolute, same-origin URLs.
 */
const tilesUrl = (path: string) => `${window.location.origin}${path}`

export const allRoutesTilesUrl = () => tilesUrl('/api/routes/tiles/{z}/{x}/{y}.mvt')

export const teamRoutesTilesUrl = (teamSlug: string) =>
  tilesUrl(`/api/teams/${encodeURIComponent(teamSlug)}/routes/tiles/{z}/{x}/{y}.mvt`)

/** Metropolitan France, the default view before any route is in sight. */
export const DEFAULT_MAP_VIEW = { longitude: 2.4, latitude: 46.6, zoom: 4.6 }

export const ROUTE_LINE_COLOR = '#1d32a8'
export const ROUTE_LINE_HOVER_COLOR = '#c90808'
